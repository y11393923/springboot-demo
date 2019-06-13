package com.zyy.client;

import com.zyy.server.HelloWorld;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @create 2018/9/29
 * @since 1.0.0
 */
@Component
public class HelloWorldClient {
    @Value("${thrift.host}")
    private String host;
    @Value("${thrift.port}")
    private Integer port;
    @Value("${thrift.timeout}")
    private Integer timeout;

    private HelloWorld.Client client;

    public HelloWorld.Client getClient() {
        return client;
    }

    TTransport transport;

    public synchronized void open(){
        try {
            if (transport==null ){
                System.out.println("客服端启动=========");
                try {
                    //设置调用服务器地址为本机，端口为8080，超时设置为30秒
                    transport = new TSocket(host,port,timeout);
                    //协议要和服务端一致
                    TProtocol protocol=new TBinaryProtocol(transport);
                    client=new HelloWorld.Client(protocol);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        if (null != transport){
            transport.close();
        }
    }
}
