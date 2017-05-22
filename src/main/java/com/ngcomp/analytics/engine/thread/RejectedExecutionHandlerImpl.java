package com.ngcomp.analytics.engine.thread;

/**
 * User: Ram Parashar
 * Date: 8/27/13
 * Time: 10:45 PM
 */
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println(r.toString() + " is rejected");
    }

}