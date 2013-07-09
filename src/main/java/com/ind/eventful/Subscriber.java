package com.ind.eventful;

public interface Subscriber<T> {
    void react(T message);
}