package com.csu.springframework.mybatis.datasource.pooled;


import com.csu.springframework.mybatis.datasource.unpooled.UnPooledDataSource;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {


    private org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDataSource.class);

    private final PooledState state = new PooledState(this);

    private final UnPooledDataSource dataSource;

    // 活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 空闲连接数
    protected int poolMaximumIdleConnections = 5;
    // 最大取出的时间？checkout到底是什么意思
    // checkout有离开宾馆房间时付款的意思
    protected int poolMaximumCheckoutTime = 20000;
    // ??
    protected int poolTimeToWait = 20000;
    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置poolPingQuery多长时间被使用一次
    protected int poolPingConnectionsNotUsedFor = 0;
    // ??
    protected String poolPingQuery = "NO PING QUERY SET";
    // 存储池子中的连接的编码，编码用("" + url + username + password).hashCode()算出来
    // 因此，整个池子中的所有连接的编码必须是一致的，里面的连接是等价的
    protected int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnPooledDataSource();
    }

    public void pullConnection(PooledConnection connection) throws SQLException {
        synchronized (state) {
            state.getActiveConnections().remove(connection);
            // 实际的空闲连接数，比最大的少的话，就把这个连接的状态改成空闲
            if (!connection.isValid()) {
                logger.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
                return;
            }

            if (state.idleConnection.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                // state
                state.accumulatedCheckOutTime += connection.getCheckoutTime();
                if (!connection.getRealConnection().getAutoCommit()) {
                    // 如果没有设置自动提交的话，不会帮他自动提交
                    connection.getRealConnection().rollback();
                }
                // 这里我不知道为什么要new一个，我的理解是要重置pool的状态
                PooledConnection newConnection = new PooledConnection(this, connection.getRealConnection());
                newConnection.setCreatedTime(connection.getCreatedTime());
                newConnection.setLastUsedTime(connection.getLastUsedTime());
                // 设置他的状态valid为false
                newConnection.invalidate();

                state.idleConnection.add(connection);
                logger.info("Returned connection " + newConnection.getRealHashCode() + " to pool.");

                // 通知在这个锁上面所有等待的元素
                // ?????????????
                // 因为运行到这里才说明连接池中有元素可以拿，所以要通知等待拿连接的线程
                state.notifyAll();
            } else {
                // 空闲连接数已满
                // 要把这个连接关了
                state.accumulatedCheckOutTime += connection.getCheckoutTime();
                if (!connection.getRealConnection().getAutoCommit()) {
                    // 如果没有设置自动提交的话，不会帮他自动提交
                    connection.getRealConnection().rollback();
                }

                connection.getRealConnection().close();
                logger.info("Closed connection " + connection.getRealHashCode() + ".");
                // 其实这个没用感觉，因为已经没有值指向他了
                connection.invalidate();
            }
        }
    }

    public boolean pingConnection(PooledConnection pooledConnection) {
        return false;
    }

    public PooledConnection popConnection(String userName, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (state) {
                if (!state.idleConnection.isEmpty()) {
                    // 返回空闲连接的第一个元素
                    conn = state.idleConnection.remove(0);
                    logger.info("Checked out connection " + conn.getRealHashCode() + " from pool.");
                } else {
                    // 没有空闲连接了
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        // 说明活跃了连接里面还能创建活跃连接
                        conn = new PooledConnection(this, dataSource.getConnection());
                        logger.info("Created connection " + conn.getRealHashCode() + ".");
                    } else {
                        // 没有空闲连接，并且也不能创建新的连接了

                        // 最老的连接
                        PooledConnection oldConnection = state.activeConnections.get(0);
                        long checkOutTime = oldConnection.getCheckoutTime();

                        if (checkOutTime > poolMaximumCheckoutTime) {
                            // 说明已经大于了规定的时间
                            // 说明拿出的时间，已经规定了规定的使用时间
                            // 每个连接只能使用一定的时间
                            // 说明你已经
                            state.claimedOverdueConnectionOut++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += checkOutTime;
                            state.accumulatedCheckOutTime += checkOutTime;
                            state.activeConnections.remove(oldConnection);

                            // 大疑惑！！！！！！！！！别的正在使用这个连接的时候咋办
                            // 直接把人家回滚了？
                            // 确实应该回滚
                            // 因为已经超过使用时间了
                            // 没有资格在使用连接了自然要回滚
                            if (!oldConnection.getRealConnection().getAutoCommit()) {
                                oldConnection.getRealConnection().rollback();
                            }

                            conn = new PooledConnection(this, dataSource.getConnection());
                            oldConnection.invalidate();

                            logger.info("Claimed overdue connection " + conn.getRealHashCode() + ".");
                        } else {
                            // 到这里的时候，就需要等待了
                            // 没有空闲连接 && 活跃连接满了 && 所有的活跃连接都没有到应该归还的时间
                            try {
                                // 这个countedWait状态是为了，多次运行这个地方的时候，只统计一次wait的次数
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                logger.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                break;
                            }

                        }
                    }
                }
            }

            if (conn != null) {
                if (conn.isValid()) {
                    // 只有get到的这个connection是valid的则返回
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }

                    conn.setCheckoutTime(System.currentTimeMillis());
                    conn.setLastUsedTime(System.currentTimeMillis());
                    state.activeConnections.add(conn);
                    state.requestCount++;
                    state.accumulatedRequestTime += System.currentTimeMillis() - t;
                } else {
                    // 连接不合法直接g
                    logger.info("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                    state.badConnectionCount++;
                    localBadConnectionCount++;
                    conn = null;

                    // 失败次数多的话抛异常
                    if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                        logger.debug("PooledDataSource: Could not get a good connection to the database.");
                        throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                    }
                }
            }
        }

        if (conn == null) {
            logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;
    }

    private void forceCloseAll() {
    }

    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUserName(), dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUserName(String userName) {
        dataSource.setUserName(userName);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }

    public void setAutoCommit(Boolean autoCommit) {
        dataSource.setAutoCommit(autoCommit);
        forceCloseAll();
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
        this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
    }

    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }
}
