package com.tumuyan.ncnn.realsr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class WorkJobPool {
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);
    public static void submit(Runnable runnable){
         executor.submit(runnable);
    }
}
