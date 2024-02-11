import dev.codeman.eventbus.Event;
import dev.codeman.eventbus.EventBus;
import dev.codeman.eventbus.Listener;
import dev.codeman.eventbus.EventHandler;

import java.util.Arrays;

public enum Main {
    INSTANCE;

    EventBus eventBus = new EventBus();

    public static void main(String[] args) {

        final long start = System.nanoTime();

        INSTANCE.eventBus.subscribe(INSTANCE);

        final double subscribeTime = (System.nanoTime() - start) / 1000000.0;

        System.out.println("Subscribe time: " + subscribeTime + "ms");

        int epochs = 100, iterations = 1000000;
        double[] times = new double[epochs];
        for (int j = 0; j < epochs; j++) {
            final long current = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                INSTANCE.eventBus.publish(new TestEvent());
            }
            final double end = (System.nanoTime() - current) / 1000000.0;
            times[j] = end;
        }
        System.out.printf("Max: %.2fms\n", Arrays.stream(times).max().getAsDouble());
        System.out.printf("Average: %.2fms\n", (Arrays.stream(times).average().getAsDouble()));
        System.out.printf("Min: %.2fms\n", Arrays.stream(times).min().getAsDouble());

        System.out.println("Average time per call: " + (Arrays.stream(times).average().getAsDouble() / iterations) + "ms");

        INSTANCE.eventBus.unsubscribe(INSTANCE);
    }

    @EventHandler
    public Listener<TestEvent> testListener = event -> {};

    public static class TestEvent extends Event {}
}
