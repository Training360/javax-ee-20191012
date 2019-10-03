package empappclient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;

@ApplicationScoped
public class BlockerMessageReceiver {

    @Inject
    private JMSContext jmsContext;

    @Inject
    private Queue queue;

    @ActivateRequestContext
    public void readMessage() {
        String name = jmsContext
                .createConsumer(queue, "recipient = 'jse'").receiveBody(String.class);
        System.out.println(name);
    }
}
