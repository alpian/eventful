package com.ind.eventful.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompletedFuture<T> implements Future<T> {
    private final T value;

    public CompletedFuture(T value) {
        this.value = value;
    }
    
    @Override public boolean cancel(boolean mayInterruptIfRunning) { return false; }

    @Override public T get() throws InterruptedException, ExecutionException { return value; }

    @Override public T get(long timeout, TimeUnit unit) { return value; }

    @Override public boolean isCancelled() { return false; }

    @Override public boolean isDone() { return true; }
    
}