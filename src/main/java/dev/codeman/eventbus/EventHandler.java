package dev.codeman.eventbus;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * The priority of the event.
     * @return The priority of the event.
     */
    int priority() default EventPriority.DEFAULT;
}