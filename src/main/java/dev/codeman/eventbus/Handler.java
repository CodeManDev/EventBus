package dev.codeman.eventbus;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {

    /**
     * The priority of the event.
     * @return The priority of the event.
     */
    EventPriority value() default EventPriority.DEFAULT;
}