import dev.codeman.eventbus.EventBus;
import dev.codeman.eventbus.EventHandler;
import dev.codeman.eventbus.Listener;

public class TestMain {

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        TestClass testClass = new TestClass();
        eventBus.subscribe(testClass);
        eventBus.publish(new Main.TestEvent());
    }

    static class TestClass {
        @EventHandler
        private final Listener<Main.TestEvent> listener = event -> {
            System.out.println("test event received");
        };

        @EventHandler(priority = -1)
        private final Listener<Main.TestEvent> listener2 = event -> {
            System.out.println("test event received 2");
        };

    }

}
