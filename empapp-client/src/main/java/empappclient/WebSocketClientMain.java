package empappclient;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
@ClientEndpoint
public class WebSocketClientMain {

    public static void main(String[] args) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            container.connectToServer(
                    new WebSocketClientMain(),
                    URI.create("ws://localhost:8080/empapp/socket/warning"));
        } catch (DeploymentException | IOException e) {
            throw new IllegalStateException("Can not create listener", e);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        System.out.println(message);
    }


}
