import dev.codeman.eventbus.EventBus;
import dev.codeman.eventbus.Listener;
import dev.codeman.eventbus.Handler;

import java.util.Arrays;

public enum Main {
    INSTANCE;

    EventBus eventBus = new EventBus();

    public static void main(String[] args) {
        INSTANCE.eventBus.subscribe(INSTANCE);

        int epochs = 100;
        double[] times = new double[epochs];
        for (int j = 0; j < epochs; j++) {
            int iterations = 1000000;
            final long current = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                INSTANCE.eventBus.call(new EventTest());
            }
            final double end = (System.nanoTime() - current) / 1000000.0;
            times[j] = end;
        }
        System.out.printf("Max: %.2fms\n", Arrays.stream(times).max().getAsDouble());
        System.out.printf("Average: %.2fms\n", (Arrays.stream(times).average().getAsDouble()));
        System.out.printf("Min: %.2fms\n", Arrays.stream(times).min().getAsDouble());

        INSTANCE.eventBus.unsubscribe(INSTANCE);
    }

    @Handler
    public Listener<EventTest> testListener = event -> {
    };
}
