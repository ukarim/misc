package com.ukarim.connpool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class PooledDataSource implements DataSource {

    private final String jdbcUrl;

    private final String username;

    private final String password;

    private final int timeoutSec; // how many second to wait for connection from pool

    private final BlockingQueue<Connection> connectionPool; // storage for connections

    private int allowedNewConnCount; // how many new connection allowed to create

    public PooledDataSource(String jdbcUrl, String username, String password, int poolSize, int timeoutSec) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.allowedNewConnCount = poolSize;
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        this.timeoutSec = timeoutSec;
    }

    // Package private method
    // Need this method only for ConnectionProxy.close() method
    void returnToPool(ConnectionProxy connectionProxy) {
        // return to pool
        connectionPool.add(connectionProxy);
    }

    @Override
    public Connection getConnection() throws SQLException {
        var availableConn = connectionPool.poll();
        if (availableConn != null) {
            return availableConn;
        }

        // Create new connection if allowed
        if (allowedNewConnCount > 0) {
            // create new connection
            var newConn = DriverManager.getConnection(jdbcUrl, username, password);
            // do not forget to decrease counter
            allowedNewConnCount = allowedNewConnCount - 1;
            // wrap new connection and return it
            return new ConnectionProxy(newConn, this);
        }

        // Wait for connection from pool

        Connection connFromPool = null;
        try {
            // this will wait for connection for specified amount of time
            connFromPool = connectionPool.poll(timeoutSec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while waiting connection from pool", e);
        }
        if (connFromPool != null) {
            // yeah! someone returned connection to pool
            return connFromPool;
        } else {
            // fuck! timeout reached
            throw new SQLTimeoutException("Connection pool timeout");
        }
    }

    // -- Our connection pool do not support following methods

    @Override
    public Connection getConnection(String username, String password) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public void setLoginTimeout(int seconds) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        throw new UnsupportedOperationException("unsupported operation");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        throw new UnsupportedOperationException("unsupported operation");
    }
}
