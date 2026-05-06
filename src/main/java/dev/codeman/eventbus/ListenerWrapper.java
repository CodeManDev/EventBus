package dev.codeman.eventbus;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class ListenerWrapper<T extends Event> {
    private final Listener<T> listener;
    private final int priority;
    private final Object parent;
    private final Class<?> type;

    public ListenerWrapper(Object parent, Field field) {
        this.priority = field.getAnnotation(EventHandler.class).priority();
        this.parent = parent;
        this.type = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]);
        Listener<T> listener = null;
        try {
            listener = (Listener<T>) field.get(parent);
        } catch (IllegalAccessException e) { e.printStackTrace(); }
        this.listener = listener;
    }

    public void call(Event event) {
        this.listener.call((T) event);
    }

    public int getPriority() {
        return priority;
    }

    public Object getParent() {
        return parent;
    }

    public Class<?> getType() {
        return type;
    }
}
