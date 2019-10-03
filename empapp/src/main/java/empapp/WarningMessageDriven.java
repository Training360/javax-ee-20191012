package empapp;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/topic/WarningTopic"
        )
})
public class WarningMessageDriven implements MessageListener {

    @Inject
    private WarningWebsocketServer warningWebsocketServer;

    @Override
    public void onMessage(Message message) {
            try {
                String messageText = message.getBody(String.class);
                System.out.println(messageText);
                warningWebsocketServer.broadcast(messageText);
            }
            catch (JMSException e) {
                throw new IllegalStateException("Can not read message", e);
            }
    }
}
