package com.tencent.community.util;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestBlockingQueue {
    public static void main(String[] args) {
        // 指定容量...
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
        // 生产线程当阻塞队列满的时候被阻塞，消费线程当阻塞队列为空的时候被阻塞
        new Thread(new Producer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
    }
}

// 生产线程
class Producer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                // 设置生产停顿时间
                Thread.sleep(20);
                this.queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生成" + queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

// 消费线程
class Consumer implements Runnable{

    private BlockingQueue<Integer> blockingQueue;

    public Consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try{
            while(true){
                // 设置消费停顿时间
                Thread.sleep(new Random().nextInt(1000));
                this.blockingQueue.take();
                System.out.println(Thread.currentThread().getName() + "消费" + this.blockingQueue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
