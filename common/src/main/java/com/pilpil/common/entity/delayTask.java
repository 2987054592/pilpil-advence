package com.pilpil.common.entity;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class delayTask<D> implements Delayed {
    public D data;
    public long deadline;
    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(Math.max(0, deadline - System.nanoTime()),TimeUnit.NANOSECONDS);
    }

    public delayTask(D data, Duration deadline) {
        this.data = data;
        this.deadline = System.nanoTime()+deadline.toNanos() ;
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        long l=getDelay(TimeUnit.NANOSECONDS)-o.getDelay(TimeUnit.NANOSECONDS);
        if(l>0){
            return 1;
        }
        if(l<0){
            return -1;
        }
        return 0;
    }
}
