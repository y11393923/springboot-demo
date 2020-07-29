package com.zyy.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

/**
 * 读取客户端发送的服务名
 * 判断服务是否发布
 * 如果发布，则走反射逻辑，动态调用，返回结果
 * 如果未发布，则返回提示通知
 * @Author: zhouyuyang
 * @Date: 2020/7/29 14:20
 */
public class ServerThread implements Runnable {

    private Socket socket;

    private List<Object> serviceList;

    public ServerThread(Socket socket, List<Object> serviceList) {
        this.socket = socket;
        this.serviceList = serviceList;
    }

    @Override
    public void run() {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())){
            Class clazz = (Class) input.readObject();
            Optional<Object> service = serviceList.stream()
                    .filter(val -> clazz.isAssignableFrom(val.getClass())).findFirst();
            if (!service.isPresent()){
                output.writeObject(clazz.getName() + "服务未发现");
            }else{
                //利用反射调用该方法，返回结果
                try {
                    String methodName = input.readUTF();
                    Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                    Object[] arguments = (Object[]) input.readObject();
                    Method method = service.get().getClass().getMethod(methodName, parameterTypes);
                    Object result = method.invoke(service.get(), arguments);
                    output.writeObject(result);
                }catch (Throwable t){
                    output.writeObject(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
