package dev.codeman.eventbus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final CopyOnWriteArrayList<ListenerWrapper<? extends Event>> listeners = new CopyOnWriteArrayList<>();
    private ListenerWrapper<?>[] cache = new ListenerWrapper[0];

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
     * Subscribe a class to the event bus.
     *
     * @param object The class to subscribe.
     */
    public void subscribe(Object object) {
        if (isSubscribed(object)) return;
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(EventHandler.class) || !field.getType().isAssignableFrom(Listener.class))
                continue;
            if (!field.isAccessible()) field.setAccessible(true);
            this.listeners.add(new ListenerWrapper<>(object, field));
        }
        this.listeners.sort(Comparator.comparingInt(ListenerWrapper::getPriority));
        this.cache = this.listeners.toArray(new ListenerWrapper<?>[0]);
    }

    /**
     * Unsubscribe a class from the event bus.
     *
     * @param object The class to unsubscribe.
     */
    public void unsubscribe(Object object) {
        this.listeners.removeIf(listener -> Objects.equals(listener.getParent(), object));
        this.cache = this.listeners.toArray(new ListenerWrapper<?>[0]);
    }

    /**
     * Check if a class is subscribed to the event bus.
     *
     * @param object The class to check.
     * @return True if the class is subscribed, false otherwise.
     */
    public boolean isSubscribed(Object object) {
        for (ListenerWrapper<? extends Event> listener : this.cache) {
            if (Objects.equals(listener.getParent(), object))
                return true;
        }
        return false;
    }
}