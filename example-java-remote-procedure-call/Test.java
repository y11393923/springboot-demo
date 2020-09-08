package com.example;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * @Author: zhouyuyang
 * @Date: 2020/9/7 17:05
 */
public class Test {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTime1 = localDateTime.with(TemporalAdjusters.previous(DayOfWeek.TUESDAY)).withHour(0).withMinute(0).withSecond(0);
        System.out.println(localDateTime1);
        System.out.println(localDateTime.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
        System.out.println(LocalDateTime.now().with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.MONDAY)));
        System.out.println(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,1,1)));


        File file = new File("D://param.json");
        System.out.println(file.length());

        try {
            System.out.println(InetAddress.getLocalHost());
            System.out.println(InetAddress.getByName("www.baidu.com"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @org.junit.jupiter.api.Test
    public void server() throws IOException {
        ServerSocket serverSocket = new ServerSocket(18080);
        Socket socket = serverSocket.accept();
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes)) != -1){
            outputStream.write(bytes, 0, len);
        }
        System.out.println(outputStream.toString());
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("D://param2.json")));
        outputStream.writeTo(bufferedOutputStream);
        bufferedOutputStream.close();

        OutputStream socketOutputStream = socket.getOutputStream();
        socketOutputStream.write("已收到".getBytes());

        socketOutputStream.close();
        outputStream.close();
        inputStream.close();
        serverSocket.close();
    }

    @org.junit.jupiter.api.Test
    public void client() throws IOException {
        Socket socket = new Socket("127.0.0.1", 18080);
        OutputStream outputStream = socket.getOutputStream();
        //outputStream.write("abc".getBytes());
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File("D://param.json")));
        IOUtils.copy(inputStream, outputStream);
        socket.shutdownOutput();

        InputStream socketInputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(socketInputStream, byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.toString());

        byteArrayOutputStream.close();
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
