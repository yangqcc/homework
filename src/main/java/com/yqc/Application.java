package com.yqc;

import com.yqc.entity.PersistenceData;
import com.yqc.service.PersistenceTask;
import com.yqc.service.ReadTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>title:</p>
 * <p>description:</p>
 *
 * @author yangqc
 * @date Created in 2018-03-24
 * @modified By yangqc
 */
public class Application {

    static final int CAPACITY = 20000;
    static AtomicBoolean isComplete = new AtomicBoolean(false);

    public static void main(String[] args) {
        LinkedBlockingQueue<PersistenceData> taskQueue = new LinkedBlockingQueue<>(CAPACITY);
        new Thread(new ReadTask(taskQueue, isComplete)).start();
        new Thread(new PersistenceTask(taskQueue, isComplete)).start();
    }
}
