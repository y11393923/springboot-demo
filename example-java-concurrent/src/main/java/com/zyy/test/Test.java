package com.zyy.test;


import com.zyy.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 11:29 2019/5/5
 */
public class Test {
    private static final Logger log= LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        AtomicInteger number=new AtomicInteger();
        System.out.println(number.incrementAndGet());

        ExecutorService service = Executors.newCachedThreadPool();
        FutureTask futureTask=new FutureTask(new CallAbleTest());
        service.submit(futureTask);

        System.out.println(futureTask.get());


        long sTime= System.currentTimeMillis();
        final CountDownLatch countDownLatch=new CountDownLatch(10);
        int corePoolSize= Runtime.getRuntime().availableProcessors()*2;

        ThreadPoolExecutor executor=new ThreadPoolExecutor(corePoolSize, corePoolSize, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),new DefaultThreadFactory("test"));
        for (int i = 0; i < 10; i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    log.info("aaa"+ Math.random());
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        log.info("耗时："+(System.currentTimeMillis()-sTime));
        executor.shutdown();

        Lock lock=new ReentrantLock();
        ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();


        LocalDateTime dateTime= LocalDateTime.now();
        String format = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);
        System.out.println(dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());

        BeanCopier copier= BeanCopier.create(User.class,User.class,false);
        copier.copy(new User(),new User(),null);
    }


    static class CallAbleTest implements Callable {

        @Override
        public Object call() throws Exception {
            return "abc"+ Math.random();
        }
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(){
            this(null);
        }
        DefaultThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" +
                    poolNumber.getAndIncrement() + (namePrefix == null ? "" : "-"+namePrefix) +
                    "-thread-";
        }
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
