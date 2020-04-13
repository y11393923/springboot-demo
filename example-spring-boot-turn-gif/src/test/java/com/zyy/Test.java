package com.zyy;

import com.google.common.collect.Lists;
import com.zyy.utils.TurnGifUtil;
import com.zyy.utils.VideoUtil;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class Test {

    @org.junit.jupiter.api.Test
    public void test() throws Exception {
        long timeMillis = System.currentTimeMillis();
        String inputFile = "rtsp://10.111.32.84:5454/W6.mp4";
        String outputFile = "D:\\zyy\\MP4\\record.mp4";
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        VideoUtil.frameRecord(inputFile, outputFile,0, 5, countDownLatch);
        countDownLatch.await();
        TurnGifUtil.buildGif(outputFile, 0, null, 10,3);
        System.out.println("耗时："+(System.currentTimeMillis() - timeMillis)/1000+"秒");
    }


    public static void main2(String[] args) {
        List<Double> doubles = Lists.newArrayList(10d,5d,6d,7d,8.5,1.0,1.2);
        Double min = doubles.stream().min(Comparator.comparing(Double::doubleValue)).orElse(0.0);
        Double max = doubles.stream().max(Comparator.comparing(Double::doubleValue)).orElse(0.0);
        System.out.println(min);
        System.out.println(max);
    }


    //开关
    volatile boolean sw = true;
    //判断是否有数组已输出完
    volatile boolean over = false;
    public static void main(String[] args) {
        int[] a1 = new int[]{1, 2, 3, 4};
        int[] a2 = new int[]{5, 6, 7, 8, 9};
        new Test().process(a1, a2);


    }

    private void process(int[] a1, int[] a2) {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < a1.length; i++) {
                while (sw != true && over == false) {
                    //自旋；可以加睡眠时间
                }
                if (sw == true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(a1[i]);
                    sw = false;
                }
            }
            over = true;
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < a2.length; i++) {
                while (sw != false && over == false) {
                    //自旋
                }
                if (sw == false || over == true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(a2[i]);
                    sw = true;
                }
            }
            over = true;
        });
        t1.start();
        t2.start();
    }
}
