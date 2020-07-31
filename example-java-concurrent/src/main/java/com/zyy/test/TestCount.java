package com.zyy.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 给a,b两个文件，各存放50亿个url,每个url各占64b,内存限制是4g 找出a,b两个文件共同的url
 * @Author: zhouyuyang
 * @Date: 2020/7/31 9:52
 */
public class TestCount {

    private final String SEPARATOR = System.getProperty("line.separator");

    public void count(String aPath, String bPath) throws Exception {
        this.splitTxt(aPath, bPath);
        String tempPath = aPath.substring(0, aPath.lastIndexOf("/") + 1) + "0temp/";
        File tempFile = new File(tempPath);
        File[] files = tempFile.listFiles();
        if (files == null){
            return;
        }
        long start = System.currentTimeMillis();
        Stream.of(files).parallel().forEach(file ->{
            Set<String> lines = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null){
                    lines.add(line);
                }
                String path = bPath.substring(0, bPath.lastIndexOf("/") + 1) + "1temp/";
                File newFile = new File(path + file.getName());
                if (newFile.exists()){
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(newFile))){
                        Set<String> identical = bufferedReader.lines().filter(lines::contains).collect(Collectors.toSet());
                        identical.forEach(System.out::println);
                        this.write(new File(tempPath + "identical.txt"), identical);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("----耗时" + (System.currentTimeMillis() - start) + "ms");
        /*for (File file : files) {
            Set<String> lines = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while ((line = reader.readLine()) != null){
                    lines.add(line);
                }
            }
            tempPath = bPath.substring(0, bPath.lastIndexOf("/") + 1) + "1temp/";
            tempFile = new File(tempPath + file.getName());
            if (tempFile.exists()){
                try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))){
                    Set<String> identical = reader.lines().filter(lines::contains).collect(Collectors.toSet());
                    identical.forEach(System.out::println);
                    this.write(new File(tempPath + "identical.txt"), identical);
                }
            }
        }*/
    }


    public void splitTxt(String... paths) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(paths.length);
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            String tempFile = path.substring(0, path.lastIndexOf("/") + 1) + i + "temp/";
            File file = new File(tempFile);
            if (!file.exists()){
                file.mkdir();
            }
            new Thread(() -> {
                long start = System.currentTimeMillis();
                try (BufferedReader reader = new BufferedReader(new FileReader(path))){
                    String line;
                    Map<String, Set<String>> contents = new HashMap<>();
                    while ((line = reader.readLine()) != null){
                        String txtName = Math.abs(line.hashCode() % 1000) + ".txt";
                        String pathName = tempFile + txtName;
                        File newFile = new File(pathName);
                        if (!newFile.exists()){
                            newFile.createNewFile();
                        }
                        if (contents.containsKey(pathName)){
                            contents.get(pathName).add(line);
                        }else{
                            Set<String> content = new HashSet<>();
                            content.add(line);
                            contents.put(pathName, content);
                        }
                        if (contents.values().stream().anyMatch(e -> e.size() > 500)){
                            contents.forEach((key, val) -> {
                                try {
                                    this.write(key, val);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            contents.clear();
                        }
                    }
                    contents.forEach((key, val) -> {
                        try {
                            this.write(key, val);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    System.out.println(tempFile + "----耗时" + (System.currentTimeMillis() - start) + "ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
    }

    public void write(String path, Collection<String> lines) throws IOException {
        this.write(new File(path), lines);
    }

    public void write(File file, Collection<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))){
            //StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                writer.write(line + SEPARATOR);
//                builder.append(line);
//                builder.append(SEPARATOR);
            }
           // writer.write(builder.toString());
            writer.flush();
        }
    }


    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        new TestCount().count("D:/zyy/demo/input.txt", "D:/zyy/demo/input2.txt");
        /*List<String> lines = new ArrayList<>();
        Random random = new Random(2000000);
        for (int i = 0; i < 10000000; i++) {
            lines.add("http://10.111.32.119:10219/skyline/" + random.nextInt());
        }
        new TestCount().write(new File("D:/zyy/demo/input.txt"), lines);*/
        System.out.println("----共耗时" + (System.currentTimeMillis() - start) + "ms");
    }
}
