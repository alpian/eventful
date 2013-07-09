package com.ind.eventful.future.executors;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.ind.eventful.future.CompletedFuture;
import com.ind.eventful.future.FailedFuture;

public class CurrentThreadExecutorService implements ExecutorService {
    private final CountDownLatch running = new CountDownLatch(1);
    
    @Override public void execute(Runnable command) {
        try {
            command.run();
        } catch (Exception ex) {
            // intentionally swallow this exception
        }
    }
    
    @Override public boolean awaitTermination(long time, TimeUnit unit) { return true; }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) {
        return newArrayList(futuresOf(tasks));
    }
    
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long time, TimeUnit unit) {
        return invokeAll(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return find(futuresOf(tasks), 
                    new IsSuccessfulFuture<T>(), 
                    new FailedFuture<T>(new IllegalStateException("No tasks completed successfully")))
                    .get();
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long time, TimeUnit unit) throws InterruptedException, ExecutionException {
        return invokeAny(tasks);
    }

    @Override
    public boolean isShutdown() {
        return running.getCount() == 0;
    }

    @Override
    public boolean isTerminated() {
        return true;
    }

    @Override
    public void shutdown() {
        running.countDown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return newArrayList();
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        FutureTask<T> task = new FutureTask<T>(callable);
        execute(task);
        return task;
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        FutureTask<?> task = new FutureTask<Void>(runnable, null);
        execute(task);
        return task;
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T value) {
        submit(runnable);
        return new CompletedFuture<T>(value);
    }
    
    private <T> Iterable<Future<T>> futuresOf(Collection<? extends Callable<T>> tasks) {
        return transform(tasks, new CallableToFuture<T>());
    }
    
    private final class IsSuccessfulFuture<T> implements Predicate<Future<T>> {
        @Override public boolean apply(Future<T> future) {
            return isSuccessful(future);
        }
        
        private boolean isSuccessful(Future<?> future) {
            if (future.isDone()) {
                try {
                    future.get();
                    return true;
                } catch (InterruptedException ex) {
                    return false;
                } catch (ExecutionException ex) {
                    return false;
                }
            }
            return false;
        }
    }

    private final class CallableToFuture<T> implements Function<Callable<T>, Future<T>> {
        @Override public Future<T> apply(Callable<T> callable) {
            return submit(callable);
        }
    }
}