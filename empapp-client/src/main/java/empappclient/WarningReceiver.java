package empappclient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.jms.*;
import java.lang.IllegalStateException;
import java.util.concurrent.CountDownLatch;

@ApplicationScoped
public class WarningReceiver implements MessageListener {

    @Inject
    private JMSContext context;

    @Inject
    private Topic topic;

    private CountDownLatch countDownLatch = new CountDownLatch(10);

    @ActivateRequestContext
    public void subscribe() {
//        context.createConsumer(topic)
//                .setMessageListener(this);
            context.setClientID("client1");
            context.createDurableConsumer(topic, "sub1")
                    .setMessageListener(this);
        try {
            countDownLatch.await();
        }catch (InterruptedException ie) {
            throw new IllegalStateException("Interrupted", ie);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println(message.getBody(String.class));
            countDownLatch.countDown();
        }
        catch (JMSException e) {
            throw new IllegalStateException("Can not read message", e);
        }
    }
}
