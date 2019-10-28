package com.zyy.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;

public class EsClient {
    private static final String clusterName = "elasticsearch";
    private static final String clusterAddress = "10.111.32.79:10229";
    private TransportClient client;
    private static volatile EsClient esClient;

    private EsClient(){
        try{
            //指定ES集群
            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            //创建访问ES服务器的客户端
            this.client = new PreBuiltTransportClient(settings);
            String[] nodes = clusterAddress.split(",");
            for (String node : nodes){
                if (node.length() > 0 && node.contains(":")){
                    String[] hostPort = node.split(":");
                    this.client.addTransportAddress(
                            new TransportAddress(InetAddress.getByName(hostPort[0]), Integer.parseInt(hostPort[1])));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static EsClient getInstance(){
        if (esClient == null){
            synchronized (EsClient.class){
                if (esClient == null){
                    esClient = new EsClient();
                }
            }
        }
        return esClient;
    }


    public static TransportClient getClient(){
        return EsClient.getInstance().client;
    }

}
