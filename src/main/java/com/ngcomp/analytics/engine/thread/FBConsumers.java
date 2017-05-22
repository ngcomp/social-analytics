package com.ngcomp.analytics.engine.thread;

import com.rabbitmq.client.impl.WorkPool;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Ram Parashar
 * Date: 8/31/13
 * Time: 10:42 AM
 */
final class FBConsumers {

    private static final int MAX_RUNNABLE_BLOCK_SIZE = 16;
    private static final int     DEFAULT_NUM_THREADS = 5;
    private final ExecutorService executor;
    private final boolean privateExecutor;
    private final WorkPool<Channel, Runnable> workPool;


    public FBConsumers(ExecutorService executor) {
        this.privateExecutor = (executor == null);
        this.executor        = (executor == null) ? Executors.newFixedThreadPool(DEFAULT_NUM_THREADS) : executor;
        this.workPool        = new WorkPool<Channel, Runnable>();
    }

    /**
     * Stop executing all consumer work
     */
    public void shutdown() {
        this.workPool.unregisterAllKeys();
        if (privateExecutor){
            this.executor.shutdown();
        }
    }

    /**
     * Stop executing all consumer work for a particular channel
     * @param channel to stop consumer work for
     */
    public void stopWork(Channel channel) {
        this.workPool.unregisterKey(channel);
    }

    public void registerKey(Channel channel) {
        this.workPool.registerKey(channel);
    }

    public void addWork(Channel channel, Runnable runnable) {
        if (this.workPool.addWorkItem(channel, runnable)) {
            this.executor.execute(new WorkPoolRunnable());
        }
    }

    private final class WorkPoolRunnable implements Runnable {

        public void run() {
            int size = MAX_RUNNABLE_BLOCK_SIZE;
            List<Runnable> block = new ArrayList<Runnable>(size);
            try {
                Channel key = FBConsumers.this.workPool.nextWorkBlock(block, size);
                if (key == null) return; // nothing ready to run
                try {
                    for (Runnable runnable : block) {
                        runnable.run();
                    }
                } finally {
                    if (FBConsumers.this.workPool.finishWorkBlock(key)) {
                        FBConsumers.this.executor.execute(new WorkPoolRunnable());
                    }
                }
            } catch (RuntimeException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}