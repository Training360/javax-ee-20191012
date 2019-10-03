package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ApplicationScoped
@ServerEndpoint("/socket/warning")
public class WarningWebsocketServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connected");
        try {
            session.getBasicRemote().sendText("Welcome");
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Error reply on websocket", ioe);
        }
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
            try {
                session.getBasicRemote().sendText("Reply - " + message);
            } catch (IOException ioe) {
                throw new IllegalStateException("Error reply on websocket", ioe);
            }
    }
}
