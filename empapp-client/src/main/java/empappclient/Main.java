package empappclient;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

public class Main {

    public static void main(String[] args) {
        SeContainerInitializer initializer =
                SeContainerInitializer.newInstance();
        try (SeContainer container =
                initializer
             .disableDiscovery()
             .addBeanClasses(JmsConfig.class, EmployeeSender.class)
             .initialize()
        ) {

            EmployeeSender sender = container.select(EmployeeSender.class).get();
            sender.sendMessage("Hello John Doe - CDI2.0");


        }
    }
}
