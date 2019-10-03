package empappclient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.jms.*;

@ApplicationScoped
public class EmployeeSender {

//    @Inject
//    private ConnectionFactory connectionFactory;

    @Inject
    private JMSContext jmsContext;

    @Inject
    private Queue queue;

    @ActivateRequestContext
    public void sendMessage(String name) {
//        try (JMSContext context = connectionFactory.createContext("guest1", "guest1")) {
//            context.createProducer().send(queue, name);
//        }

        jmsContext.createProducer()
                .setProperty("recipient", "mdb")
//                .setAsync(new CompletionListener() {
//                    @Override
//                    public void onCompletion(Message message) {
//
//                    }
//
//                    @Override
//                    public void onException(Message message, Exception e) {
//
//                    }
//                })
                .send(queue, name);
    }
}
