package empappclient;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClientMain {

    private CountDownLatch countDownLatch;

    public WebSocketClientMain(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(
                    new WebSocketClientMain(countDownLatch),
                    URI.create("ws://localhost:8080/empapp/socket/warning"));
        } catch (DeploymentException | IOException e) {
            throw new IllegalStateException("Can not create listener", e);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException ie) {
            throw new IllegalStateException("Countdownlatch has interrupted", ie);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        System.out.println(message);
        countDownLatch.countDown();
    }


}
