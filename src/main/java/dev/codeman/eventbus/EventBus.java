package dev.codeman.eventbus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EventBus {
    private final List<ListenerWrapper<? extends Event>> listeners = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile ListenerWrapper<?>[] cache = new ListenerWrapper[0];

    /**
     * Call all listeners for the given event.
     *
     * @param event The event to call listeners for.
     */
    public void publish(Event event) {
        for (ListenerWrapper<? extends Event> listenerWrapper : this.cache) {
            if (event.getClass() != listenerWrapper.getType())
                continue;

            listenerWrapper.call(event);
        }
    }

    /**
     * Subscribe a object to the event bus.
     *
     * @param object The object to subscribe.
     */
    public void subscribe(Object object) {
        this.lock.writeLock().lock();
        try {
            if (isSubscribed(object)) return;
            for (Field field : object.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(EventHandler.class) || !field.getType().isAssignableFrom(Listener.class))
                    continue;
                if (!field.isAccessible()) field.setAccessible(true);
                this.listeners.add(new ListenerWrapper<>(object, field));
            }
            this.listeners.sort(Comparator.comparingInt(ListenerWrapper::getPriority));
            this.cache = this.listeners.toArray(new ListenerWrapper<?>[0]);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Unsubscribe a object from the event bus.
     *
     * @param object The object to unsubscribe.
     */
    public void unsubscribe(Object object) {
        this.lock.writeLock().lock();
        try {
            this.listeners.removeIf(listener -> Objects.equals(listener.getParent(), object));
            this.cache = this.listeners.toArray(new ListenerWrapper<?>[0]);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Check if a object is subscribed to the event bus.
     *
     * @param object The object to check.
     * @return True if the object is subscribed, false otherwise.
     */
    public boolean isSubscribed(Object object) {
        for (ListenerWrapper<? extends Event> listener : this.cache) {
            if (Objects.equals(listener.getParent(), object))
                return true;
        }
        return false;
    }
}