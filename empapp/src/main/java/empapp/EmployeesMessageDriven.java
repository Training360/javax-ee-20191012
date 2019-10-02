package empapp;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/EmployeeQueue"
        )
})
public class EmployeesMessageDriven implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                String name = textMessage.getText();
                System.out.println(name);
            }
            catch (JMSException e) {
                throw new IllegalStateException("Can not read message", e);
            }
        }
    }
}
