package empapp;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
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
    private Event<String> eventPublisher;

    @Override
    public void onMessage(Message message) {
            try {
                String messageText = message.getBody(String.class);
                System.out.println(messageText);

                eventPublisher.fire(messageText);
            }
            catch (JMSException e) {
                throw new IllegalStateException("Can not read message", e);
            }
    }
}
