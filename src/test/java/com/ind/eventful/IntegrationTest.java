package com.ind.eventful;

import static com.google.common.collect.Lists.newArrayList;
import static com.ind.eventful.name.StringName.named;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;

import org.junit.Test;

import com.ind.eventful.future.executors.CurrentThreadExecutorService;


public class IntegrationTest {
    @Test public void 
    can_post_an_event_to_a_topic_and_have_someone_react_to_it() {
        Topic<String> topic = Topic.create(named("my topic"), new CurrentThreadExecutorService());
        RecordingSubscriber<String> subscriber = new RecordingSubscriber<String>();
        
        topic.subscribe(subscriber);
        
        topic.publish("hello world");
        
        assertThat(subscriber.received, contains("hello world"));
    }
    
    public static class RecordingSubscriber<T> implements Subscriber<T> {
        public final List<T> received = newArrayList();
        
        @Override
        public void react(T message) {
            received.add(message);
        }
    }
}
