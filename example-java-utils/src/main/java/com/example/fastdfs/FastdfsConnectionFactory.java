package com.example.fastdfs;


import com.example.exception.FileSystemException;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class FastdfsConnectionFactory {

    private GenericObjectPool<FastdfsConnection> connectionPool;

    public FastdfsConnectionFactory(String configPath) {
        this(configPath, null);
    }

    public FastdfsConnectionFactory(String configPath, FastdfsConnectionPoolConfig config) {
        if (config == null) {
            config = new FastdfsConnectionPoolConfig();
        }
        FastdfsConnectionProvider provider = new FastdfsConnectionProvider(configPath);
        FastdfsConnectionPool pool = new FastdfsConnectionPool(provider);
        this.connectionPool = new GenericObjectPool<>(pool, config);
    }

    /**
     * 获取连接
     *
     * @return
     */
    public FastdfsConnection getConnection() {
        try {
            FastdfsConnection connection = connectionPool.borrowObject();
            connection.setConnectionFactory(this);
            return connection;
        } catch (Exception e) {
            throw new FileSystemException("get connection error!", e);
        }
    }


    /**
     * 释放连接
     *
     * @param connection
     */
    public void release(FastdfsConnection connection) {
        connectionPool.returnObject(connection);
    }



}
