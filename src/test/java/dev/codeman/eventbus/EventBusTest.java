package dev.codeman.eventbus;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

class EventBusTest {

    private EventBus eventBus;

    static class TestEvent extends Event {}
    static class OtherEvent extends Event {}

    static class TestSubscriber {
        List<TestEvent> received = new ArrayList<>();

        @EventHandler
        Listener<TestEvent> onTest = event -> received.add(event);
    }

    static class MultiSubscriber {
        List<Event> testEvents = new ArrayList<>();
        List<Event> otherEvents = new ArrayList<>();

        @EventHandler
        Listener<TestEvent> onTest = event -> testEvents.add(event);

        @EventHandler
        Listener<OtherEvent> onOther = event -> otherEvents.add(event);
    }

    static class PrioritySubscriber {
        List<String> callOrder = new ArrayList<>();

        @EventHandler(priority = 1)
        Listener<TestEvent> lowPriority = event -> callOrder.add("low");

        @EventHandler(priority = 10)
        Listener<TestEvent> highPriority = event -> callOrder.add("high");
    }

    @BeforeEach
    void setUp() {
        eventBus = new EventBus();
    }

    @Test
    void subscribe_registersListener() {
        TestSubscriber sub = new TestSubscriber();
        eventBus.subscribe(sub);
        assertTrue(eventBus.isSubscribed(sub));
    }

    @Test
    void subscribe_isIdempotent() {
        TestSubscriber sub = new TestSubscriber();
        eventBus.subscribe(sub);
        eventBus.subscribe(sub); // second call should do nothing

        TestEvent event = new TestEvent();
        eventBus.publish(event);

        // listener should only fire once, not twice
        assertEquals(1, sub.received.size());
    }

    @Test
    void unsubscribe_removesListener() {
        TestSubscriber sub = new TestSubscriber();
        this.eventBus.subscribe(sub);
        this.eventBus.unsubscribe(sub);
        assertFalse(this.eventBus.isSubscribed(sub));
    }

    @Test
    void unsubscribe_stopsListenerFromReceivingEvents() {
        TestSubscriber sub = new TestSubscriber();
        this.eventBus.subscribe(sub);
        this.eventBus.unsubscribe(sub);
        this.eventBus.publish(new TestEvent());
        assertTrue(sub.received.isEmpty());
    }

    @Test
    void publish_routesEventToCorrectListener() {
        MultiSubscriber sub = new MultiSubscriber();
        this.eventBus.subscribe(sub);

        this.eventBus.publish(new TestEvent());

        assertEquals(1, sub.testEvents.size());
        assertEquals(0, sub.otherEvents.size());
    }

    @Test
    void publish_deliversToMultipleIndependentSubscribers() {
        TestSubscriber subA = new TestSubscriber();
        TestSubscriber subB = new TestSubscriber();
        this.eventBus.subscribe(subA);
        this.eventBus.subscribe(subB);

        this.eventBus.publish(new TestEvent());

        assertEquals(1, subA.received.size());
        assertEquals(1, subB.received.size());
    }

    @Test
    void publish_passesCorrectEventInstance() {
        TestSubscriber sub = new TestSubscriber();
        this.eventBus.subscribe(sub);

        TestEvent event = new TestEvent();
        this.eventBus.publish(event);

        assertSame(event, sub.received.get(0));
    }

    @Test
    void priority_listenersCalledInPriorityOrder() {
        PrioritySubscriber sub = new PrioritySubscriber();
        this.eventBus.subscribe(sub);
        this.eventBus.publish(new TestEvent());

        assertEquals(Arrays.asList("low", "high"), sub.callOrder);
    }

    @Test
    void isSubscribed_returnsFalseBeforeSubscribing() {
        assertFalse(this.eventBus.isSubscribed(new TestSubscriber()));
    }

    @Test
    void isSubscribed_returnsTrueAfterSubscribing() {
        TestSubscriber sub = new TestSubscriber();
        this.eventBus.subscribe(sub);
        assertTrue(this.eventBus.isSubscribed(sub));
    }
}