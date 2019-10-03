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
        ),
        @ActivationConfigProperty(
                propertyName = "messageSelector",
                propertyValue = "recipient = 'mdb'"
        )
})
public class EmployeesMessageDriven implements MessageListener {

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("DELIVERED:" + message.getJMSRedelivered());
                System.out.println("COUNT:" + message.getIntProperty("JMSXDeliveryCount"));
                String name = textMessage.getText();
                System.out.println(name);
          //      throw new IllegalArgumentException("I DON'T LIKE THIS MESSAGE!");
            }
            catch (JMSException e) {
                throw new IllegalStateException("Can not read message", e);
            }
        }
    }
}
