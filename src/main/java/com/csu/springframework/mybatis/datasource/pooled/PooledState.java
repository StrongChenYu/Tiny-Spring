package com.csu.springframework.mybatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了统计连接池的信息
 */
public class PooledState {

    protected PooledDataSource pooledDataSource;

    // 空闲的连接
    protected final List<PooledConnection> idleConnection = new ArrayList<>();
    // 活跃的连接
    protected final List<PooledConnection> activeConnections = new ArrayList<>();

    // 连接被取出的次数
    protected long requestCount = 0;
    // 取出请求到请求准备结束的累计时间 accumulated 累计
    protected long accumulatedRequestTime = 0;
    // 累计被取出的时间，和上面有什么区别
    protected long accumulatedCheckOutTime = 0;
    // 过期的连接数量
    protected long claimedOverdueConnectionOut = 0;

    // 总等待时间
    protected long accumulatedWaitTime = 0;
    // 等待的轮次
    protected long hadToWaitCount = 0;
    // 失败连接次数
    protected long badConnectionCount = 0;

    public PooledState(PooledDataSource dataSource) {
        this.pooledDataSource = dataSource;
    }


    public synchronized List<PooledConnection> getIdleConnection() {
        return idleConnection;
    }

    public synchronized List<PooledConnection> getActiveConnections() {
        return activeConnections;
    }

    public synchronized long getRequestCount() {
        return requestCount;
    }

    public synchronized long getAccumulatedRequestTime() {
        return accumulatedRequestTime;
    }

    public synchronized long getAccumulatedCheckOutTime() {
        return accumulatedCheckOutTime;
    }

    public synchronized long getClaimedOverdueConnectionOut() {
        return claimedOverdueConnectionOut;
    }

    public synchronized long getAccumulatedWaitTime() {
        return accumulatedWaitTime;
    }

    public synchronized long getHadToWaitCount() {
        return hadToWaitCount;
    }

    public synchronized long getBadConnectionCount() {
        return badConnectionCount;
    }
}
