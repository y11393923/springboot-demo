package com.example.fastdfs;

import com.example.exception.FileSystemException;
import com.example.util.PropertiesSupport;
import org.csource.fastdfs.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

class FastdfsConnectionProvider {

    public static final int DEFAULT_CONNECT_TIMEOUT = 5; // second
    public static final int DEFAULT_NETWORK_TIMEOUT = 30; // second

    private TrackerGroup trackerGroup;

    public FastdfsConnectionProvider(String configPath) {
        init(configPath);
    }


    public FastdfsConnection createConnection() throws IOException {
        TrackerClient trackerClient = new TrackerClient(trackerGroup);
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return new FastdfsConnection(storageClient, trackerServer);
    }


    /**
     * 获取TrackerGroup
     *
     * @return
     */
    public TrackerGroup getTrackerGroup() {
        return trackerGroup;
    }

    private void init(String configPath) {
        File configFile = new File(configPath);
        if(configFile == null || !configFile.exists() || !configFile.canRead()) {
            throw new FileSystemException("can not load fastdfs config from [" + configPath + "]");
        }

        try (InputStream is = new FileInputStream(configFile)) {
            PropertiesSupport properties = new PropertiesSupport(is);

            String[] parts;

            int connectiTimeOut = properties.getInteger("connect_timeout", DEFAULT_CONNECT_TIMEOUT);
            if (connectiTimeOut < 0) {
                connectiTimeOut = DEFAULT_CONNECT_TIMEOUT;
            }
            ClientGlobal.setG_connect_timeout(connectiTimeOut * 1000); // millisecond

            int networkTimeOut = properties.getInteger("network_timeout", DEFAULT_NETWORK_TIMEOUT);
            if (networkTimeOut < 0) {
                networkTimeOut = DEFAULT_NETWORK_TIMEOUT;
            }
            ClientGlobal.setG_network_timeout(networkTimeOut * 1000);

            String charset = properties.getString("charset");
            if (charset == null || charset.length() == 0) {
                charset = "ISO8859-1";
            }
            ClientGlobal.setG_charset(charset);

            String[] trackerServers = properties.getArrayString("tracker_server", ",");
            if (trackerServers == null) {
                throw new FileSystemException("item \"tracker_server\" in " + configPath + " not found");
            }

            InetSocketAddress[] addresses = new InetSocketAddress[trackerServers.length];
            for (int i = 0; i < trackerServers.length; i++) {
                parts = trackerServers[i].split("\\:", 2);
                if (parts.length != 2) {
                    throw new FileSystemException(
                            "the value of item \"tracker_server\" is invalid, the correct format is host:port");
                }
                addresses[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
            TrackerGroup trackerGroup = new TrackerGroup(addresses);
            ClientGlobal.setG_tracker_group(trackerGroup);

            int port = properties.getInteger("http.tracker_http_port", 80);
            ClientGlobal.setG_tracker_http_port(port);

            boolean hasToken = properties.getBoolean("http.anti_steal_token", false);
            if (hasToken) {
                String secretKey = properties.getString("http.secret_key");
                ClientGlobal.setG_anti_steal_token(hasToken);
                ClientGlobal.setG_secret_key(secretKey);
            }

            this.trackerGroup = ClientGlobal.getG_tracker_group();
        } catch (IOException e) {
            throw new FileSystemException("loading fastdfs config error!", e);
        }
    }
}
