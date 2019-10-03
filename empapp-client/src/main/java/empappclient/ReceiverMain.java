package empappclient;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class ReceiverMain {

    public static void main(String[] args) {
        SeContainerInitializer initializer =
                SeContainerInitializer.newInstance();
        try (SeContainer container =
                initializer
             .disableDiscovery()
             .addBeanClasses(JmsConfig.class, BlockerMessageReceiver.class, WarningReceiver.class)
             .initialize()
        ) {

//            BlockerMessageReceiver receiver = container.select(BlockerMessageReceiver.class).get();
//            receiver.readMessage();

            WarningReceiver receiver = container.select(WarningReceiver.class).get();
            receiver.subscribe();
        }
    }
}
