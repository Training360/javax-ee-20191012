package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ApplicationScoped
public class WarningMediator {

    private List<Consumer<String>> consumers = Collections.synchronizedList(new ArrayList<>());

    public void register(Consumer<String> consumer) {
        consumers.add(consumer);
    }

    public void processEvent(@Observes @ForWebsocket String event) {
        System.out.println("Process event: " + event);
        consumers.stream().forEach(c -> c.accept(event));
    }
}
