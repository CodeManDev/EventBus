package dev.codeman.eventbus;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private final CopyOnWriteArrayList<ListenerWrapper<? extends Event>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Call all listeners for the given event.
     *
     * @param event The event to call listeners for.
     */
    public void publish(Event event) {
        for (ListenerWrapper<? extends Event> listenerWrapper : listeners) {
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
            listeners.add(new ListenerWrapper<>(object, field));
        }
        listeners.sort(Comparator.comparingInt(ListenerWrapper::getPriority));
    }

    /**
     * Unsubscribe a class from the event bus.
     *
     * @param object The class to unsubscribe.
     */
    public void unsubscribe(Object object) {
        listeners.removeIf(listener -> listener.getParent().getClass().equals(object.getClass()));
    }

    /**
     * Check if a class is subscribed to the event bus.
     *
     * @param object The class to check.
     * @return True if the class is subscribed, false otherwise.
     */
    public boolean isSubscribed(Object object) {
        return listeners.stream().anyMatch(listener -> listener.getParent().getClass().equals(object.getClass()));
    }
}