package com.ind.eventful;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.ind.eventful.name.Name;


public class Topic<T> {
    public final Name name;
    
    private final ExecutorService executorService;
    
    private volatile Iterable<Subscriber<T>> subscribers = ImmutableList.of();


    public Topic(Name name, ExecutorService executorService) {
        this.name = name;
        this.executorService = executorService;
    }

    public static <T> Topic<T> create(Name name, ExecutorService executorService) {
        return new Topic<T>(name, executorService);
    }

    public void subscribe(Subscriber<T> subscriber) {
        subscribers = concat(subscribers, ImmutableList.of(subscriber));
    }

    public void publish(T message) {
        try {
            executorService.invokeAll(ImmutableList.copyOf(transform(subscribers, toCallSubscriberTask(message))));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    private Function<Subscriber<T>, CallSubscriber<T>> toCallSubscriberTask(final T message) {
        return new Function<Subscriber<T>, CallSubscriber<T>>() {
            @Override
            public CallSubscriber<T> apply(Subscriber<T> subscriber) {
                return new CallSubscriber<T>(subscriber, message);
            }
        };
    }

    private static final class CallSubscriber<T> implements Callable<Void> {
        private final Subscriber<T> subscriber;
        private final T message;

        public CallSubscriber(Subscriber<T> subscriber, T message) {
            this.subscriber = subscriber;
            this.message = message;
        }
        
        @Override public Void call() throws Exception {
            subscriber.react(message);
            return null;
        }
    }
}