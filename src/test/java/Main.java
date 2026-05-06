import dev.codeman.eventbus.Event;
import dev.codeman.eventbus.EventBus;
import dev.codeman.eventbus.Listener;
import dev.codeman.eventbus.EventHandler;

import java.util.Arrays;

public class Main {

    static class TestClass {
        @EventHandler
        public Listener<TestEvent> testListener = event -> {};
    }

    public static class TestEvent extends Event {}

    private static final int WARMUP_EPOCHS   = 20;
    private static final int EPOCHS          = 100;
    private static final int ITERATIONS      = 1_000_000;

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        TestClass testClass = new TestClass();

        eventBus.subscribe(testClass);

        // pre-allocate events so allocation cost is excluded from measurement
        TestEvent[] events = new TestEvent[ITERATIONS];
        for (int i = 0; i < ITERATIONS; i++) events[i] = new TestEvent();

        // warm up so jit can do its thing
        System.out.println("Warming up...");
        for (int j = 0; j < WARMUP_EPOCHS; j++) {
            for (int i = 0; i < ITERATIONS; i++) {
                eventBus.publish(events[i]);
            }
        }

        double[] times = new double[EPOCHS];
        for (int j = 0; j < EPOCHS; j++) {
            long current = System.nanoTime();
            for (int i = 0; i < ITERATIONS; i++) {
                eventBus.publish(events[i]);
            }
            times[j] = (System.nanoTime() - current) / 1_000_000.0;
        }

        double min    = Arrays.stream(times).min().getAsDouble();
        double max    = Arrays.stream(times).max().getAsDouble();
        double avg    = Arrays.stream(times).average().getAsDouble();
        double median = median(times);
        double stddev = stddev(times, avg);

        System.out.println("\n--- Results (" + EPOCHS + " epochs, " + ITERATIONS + " iterations each) ---");
        System.out.printf("Min:    %.2fms%n", min);
        System.out.printf("Max:    %.2fms%n", max);
        System.out.printf("Avg:    %.2fms%n", avg);
        System.out.printf("Median: %.2fms%n", median);
        System.out.printf("StdDev: %.2fms%n", stddev);
        System.out.printf("Avg per call: %.4fus%n", (avg / ITERATIONS) * 1000.0); // microseconds

        eventBus.unsubscribe(testClass);
    }

    private static double median(double[] times) {
        double[] sorted = Arrays.copyOf(times, times.length);
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 0
                ? (sorted[mid - 1] + sorted[mid]) / 2.0
                : sorted[mid];
    }

    private static double stddev(double[] times, double mean) {
        double sum = 0;
        for (double t : times) sum += (t - mean) * (t - mean);
        return Math.sqrt(sum / times.length);
    }
}