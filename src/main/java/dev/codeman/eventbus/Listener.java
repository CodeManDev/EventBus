package dev.codeman.eventbus;

public interface Listener<T extends Event> {
    void call(T event);
}
