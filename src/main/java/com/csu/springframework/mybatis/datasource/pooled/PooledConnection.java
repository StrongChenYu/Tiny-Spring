package com.csu.springframework.mybatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 因为这个地方需要拦截Connection.close()方法
 */
public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] INTERFACES = new Class<?>[]{Connection.class};

    private int hashCode = 0;
    // 这个connection属于的dataSource
    private PooledDataSource dataSource;

    private Connection realConnection;
    private Connection proxyConnection;
    // 从连接池中取出的时间
    private long checkoutTime;
    // 创建时间
    private long createdTime;
    // 上次使用时间
    private long lastUsedTime;
    // 连接池类型编码
    private int connectionTypeCode;
    // 连接是否可用
    private boolean valid;

    public PooledConnection(PooledDataSource dataSource, Connection connection) {
        this.dataSource = dataSource;
        this.realConnection = connection;

        this.hashCode = connection.hashCode();
        this.createdTime = System.currentTimeMillis();
        this.lastUsedTime = System.currentTimeMillis();
        this.valid = true;
        // Connection是一个代理
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), INTERFACES, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        // CLOSE.hashCode() == methodName.hashCode() 这个条件是为了做什么
        if (CLOSE.equals(methodName) && CLOSE.hashCode() == methodName.hashCode()) {
            // 把connection放回到线程池中
            dataSource.pullConnection(this);
            return null;
        } else {
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            return method.invoke(realConnection, args);
        }
    }


    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }

    public void invalidate() {
        this.valid = false;
    }

    public boolean isValid() {
        // 这个代理类中的valid=true && 真实连接不为空 && dataSource去ping这个connection能ping通
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public long getTimeElapsedSinceLastUse() {
        // 获取从上一次使用完之后的时间
        return System.currentTimeMillis() - lastUsedTime;
    }

    public long getAge() {
        // 获取从创建开始的时间
        return System.currentTimeMillis() - createdTime;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - this.checkoutTime;
    }

    public void setCheckoutTime(long checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object object) {
        // 这里主要判断传进来的connection
        if (object instanceof PooledConnection) {
            return realConnection.hashCode() == ((PooledConnection) object).getRealConnection().hashCode();
        } else if (object instanceof Connection) {
            return object.hashCode() == hashCode;
        } else {
            return false;
        }
    }
}
