import dev.codeman.eventbus.EventBus;
import dev.codeman.eventbus.EventHandler;
import dev.codeman.eventbus.Listener;

public class TestMain {

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        TestClass testClass = new TestClass();
        TestClass2 testClass2 = new TestClass2();
        eventBus.subscribe(testClass);
        eventBus.subscribe(testClass2);
        eventBus.publish(new Main.TestEvent());
    }

    static class TestClass {
        @EventHandler(priority = 99)
        private final Listener<Main.TestEvent> listener = event -> {
            System.out.println("test event received");
        };

        @EventHandler
        private final Listener<Main.TestEvent> listener2 = event -> {
            System.out.println("test event received 2");
        };
    }

    static class TestClass2 {
        @EventHandler(priority = 55)
        private final Listener<Main.TestEvent> listener = event -> {
            System.out.println("test event received by class2");
        };
    }

}
