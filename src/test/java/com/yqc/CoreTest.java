package com.yqc;

import com.yqc.service.PersistenceTask;
import com.yqc.service.ReadTask;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoreTest extends TestCase {

    AtomicBoolean isComplete = new AtomicBoolean(false);

    @Test
    public void test() throws InterruptedException {
        LinkedBlockingQueue queue = new LinkedBlockingQueue(12);
        List<Runnable> tasks = new ArrayList<>();
        tasks.add(new PersistenceTask(queue, isComplete));
        tasks.add(new ReadTask(queue, isComplete));
        assertConcurrent("错误", tasks);
    }

    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables)
            throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        final CountDownLatch allDone = new CountDownLatch(numThreads);
        try {
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(() -> {
                    try {
                        submittedTestRunnable.run();
                    } catch (final Throwable e) {
                        e.printStackTrace();
                        exceptions.add(e);
                    } finally {
                        allDone.countDown();
                    }
                });
            }
        } finally {
            threadPool.shutdown();
        }
        allDone.await();
    //    assertTrue(message + " failed with exception(s) " + exceptions, exceptions.isEmpty());
    }
}