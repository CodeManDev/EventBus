package dev.codeman.eventbus;

public class Event {
    boolean cancelled = false;

    /**
     * Cancel the event.
     * @param cancelled true if the event is cancelled.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Check if the event is cancelled.
     * @return true if the event is cancelled.
     */
    public boolean isCancelled() {
        return cancelled;
    }
}
