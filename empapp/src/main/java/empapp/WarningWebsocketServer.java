package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@ServerEndpoint("/socket/warning")
public class WarningWebsocketServer {

    private List<Session> sessions = Collections.synchronizedList(new ArrayList<>());

    public void broadcast(String message) {
        for (Session session: sessions) {
            try {
                session.getBasicRemote().sendText(message);
            }
            catch (IOException ioe) {
                throw new IllegalStateException("Error reply on websocket", ioe);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connected");
        sessions.add(session);
        try {
            session.getBasicRemote().sendText("Welcome");
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Error reply on websocket", ioe);
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
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
