package com.zyy.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * @author Administrator
 * @create 2018/9/29
 * @since 1.0.0
 */
public class HelloWorldServerService {
    public static void main(String []args) throws TTransportException {
        System.out.println("服务端开启");
        //关联处理器
        TProcessor tProcessor=new HelloWorld.Processor<HelloWorld.Iface>(new HelloWorldServiceImpl());
        //设置服务端口为8080
        TServerSocket serverSocket=new TServerSocket(8080);
        //简单的单线程服务模型
        TServer.Args tArgs=new TServer.Args(serverSocket);
        tArgs.processor(tProcessor);
        //设置协议工厂
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        TServer server=new TSimpleServer(tArgs);
        server.serve();
    }
}
