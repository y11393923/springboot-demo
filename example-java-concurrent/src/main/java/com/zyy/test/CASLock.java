package com.zyy.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: zhouyuyang
 * @Date: 2020/10/12 15:04
 */
public class CASLock {

    private final AtomicReference<Thread> atomicReference = new AtomicReference<>();


    public void lock(){
        System.out.println(Thread.currentThread().getName() + "lock");
        //线程2会一直阻塞，直到设置成功
        while (!atomicReference.compareAndSet(null, Thread.currentThread())){
        }
    }

    public void unlock(){
        System.out.println(Thread.currentThread().getName() + "unlock");
        atomicReference.compareAndSet(Thread.currentThread(), null);
    }

    public static void main(String[] args) {
        CASLock casLock = new CASLock();
        new Thread(() ->{
            casLock.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            casLock.unlock();
        }).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() ->{
            casLock.lock();
            casLock.unlock();
        }).start();
    }

}
