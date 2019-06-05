package com.zyy.test;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 17:57 2019/4/29
 */
public class TestQueue {

    public static void main(String[] args) {
        LinkedBlockingQueue queue=new LinkedBlockingQueue(10);
        Producer producer=new Producer(queue);
        Consumer consumer=new Consumer(queue);
        new Thread(producer).start();
        new Thread(consumer).start();
    }

    static class Producer implements Runnable {
        private LinkedBlockingQueue queue;
        public Producer(LinkedBlockingQueue queue){
            this.queue=queue;
        }
        @Override
        public void run() {
            while (true){
                synchronized (queue){
                    try {
                        for (int i=0;i<5;i++){
                            String uuid= UUID.randomUUID().toString();
                            System.out.println("生产："+uuid);
                            queue.put(uuid);
                        }
                        queue.notify();
                        queue.wait();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    static class Consumer implements Runnable {
        private LinkedBlockingQueue queue;
        public Consumer(LinkedBlockingQueue queue){
            this.queue=queue;
        }
        @Override
        public void run() {
            while (true){
                synchronized (queue){
                    try {
                        Object poll = queue.poll();
                        while (poll!=null){
                            System.out.println("消费："+poll);
                            poll=queue.poll();
                        }
                        queue.notify();
                        Thread.sleep(1000);
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
