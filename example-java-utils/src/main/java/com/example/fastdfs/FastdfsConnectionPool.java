package com.example.fastdfs;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class FastdfsConnectionPool extends BasePooledObjectFactory<FastdfsConnection> {

    private FastdfsConnectionProvider connectionProvider;

    public FastdfsConnectionPool(FastdfsConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public FastdfsConnection create() throws Exception {
        return connectionProvider.createConnection();
    }

    @Override
    public PooledObject<FastdfsConnection> wrap(FastdfsConnection connection) {
        return new DefaultPooledObject<>(connection);
    }


    @Override
    public PooledObject<FastdfsConnection> makeObject() throws Exception {
        return wrap(create());
    }

    @Override
    public void destroyObject(PooledObject<FastdfsConnection> pooledObject) throws Exception {
        FastdfsConnection connection = pooledObject.getObject();
        if (connection != null) {
            connection.destroy();
        }
    }
}
