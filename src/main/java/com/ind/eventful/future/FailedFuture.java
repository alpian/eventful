package com.ind.eventful.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FailedFuture<T> implements Future<T> {
    private final Exception exception;

    public FailedFuture(Exception exception) {
        this.exception = exception;
    }
    
     @Override public boolean cancel(boolean mayInterruptIfRunning) { return false; }

    @Override public T get() throws ExecutionException { throw new ExecutionException(exception); }

    @Override public T get(long timeout, TimeUnit unit) throws ExecutionException { return get(); }

    @Override public boolean isCancelled() { return false; }

    @Override public boolean isDone() { return true; }
    
}