package empappclient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

@ApplicationScoped
public class EmployeeSender {

    @Inject
    private ConnectionFactory connectionFactory;

    @Inject
    private Queue queue;

    public void sendMessage(String name) {
        try (JMSContext context = connectionFactory.createContext("guest1", "guest1")) {
            context.createProducer().send(queue, name);
        }
    }
}
