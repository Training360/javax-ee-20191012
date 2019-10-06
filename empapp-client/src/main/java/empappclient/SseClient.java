package empappclient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;

// Nem mukodik!!!!
public class SseClient {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch l = new CountDownLatch(3);
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080/empapp/api/warning/stream");
        try (SseEventSource source = SseEventSource.target(target)
                .reconnectingEvery(30, SECONDS)

                .build()) {
            source.register((inboundSseEvent) -> {
                System.out.println(inboundSseEvent.readData(String.class));l.countDown();},
                    t -> t.printStackTrace(), () -> SseClient.ready())
            ;
            source.open();
            System.out.println(source.isOpen());
        }
        System.out.println("waiting");
        l.await();
    }

    public static void ready() {
        System.out.println("no more events");
    }
}
