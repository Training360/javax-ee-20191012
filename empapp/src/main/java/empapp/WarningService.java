package empapp;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.jms.Topic;

@ApplicationScoped
public class WarningService {

    @Inject
    private JMSContext context;

    @Resource(mappedName = "java:/jms/topic/WarningTopic")
    private Topic topic;

    public void sendWarning(WarningCommand command) {
        context.createProducer().send(topic, command.getMessage());
    }
}
