package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject
    private WarningMediator warningMediator;

//    private List<Session> sessions = Collections.synchronizedList(new ArrayList<>());

//    public void broadcast(String message) {
//        for (Session session: sessions) {
//            try {
//                session.getBasicRemote().sendText(message);
//            }
//            catch (IOException ioe) {
//                throw new IllegalStateException("Error reply on websocket", ioe);
//            }
//        }
//    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connected");
//        sessions.add(session);
        sendMessage("Welcome", session);

        warningMediator.register(message -> sendMessage(message, session));

    }

    public void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        }
        catch (IOException ioe) {
            throw new IllegalStateException("Error reply on websocket", ioe);
        }
    }

    @OnClose
    public void onClose(Session session) {
//        sessions.remove(session);
        // TODO eltavolitani a consumer listabol
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
