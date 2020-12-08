package com.example.fastdfs;


import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerServer;

import java.io.Closeable;
import java.io.IOException;

public class FastdfsConnection implements Closeable {
    private final StorageClient storageClient;
    private final TrackerServer trackerServer;

    private FastdfsConnectionFactory connectionFactory;

    public FastdfsConnection(StorageClient storageClient, TrackerServer trackerServer) {
        this.storageClient = storageClient;
        this.trackerServer = trackerServer;
    }


    @Override
    public void close() {
        if (connectionFactory != null) {
            connectionFactory.release(this);
        }
        else {
            try {
                destroy();
            } catch (IOException e) {
            }
        }
    }


    void destroy() throws IOException {
        if (trackerServer != null) {
            trackerServer.close();
        }
    }

    void setConnectionFactory(FastdfsConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public StorageClient getStorageClient() {
        return storageClient;
    }

    public TrackerServer getTrackerServer() {
        return trackerServer;
    }

}
