package empappclient;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class EmployeeSendMain {

    public static void main(String[] args) {
        Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProperties.put(Context.URL_PKG_PREFIXES,
                "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.PROVIDER_URL,
                "http-remoting://localhost:8080");
        jndiProperties.put("jboss.naming.client.ejb.context", true);

        ConnectionFactory connectionFactory;
        Queue destination;
        try {
            Context ctx = new InitialContext(jndiProperties);
            connectionFactory = (ConnectionFactory) ctx
                    .lookup("jms/RemoteConnectionFactory");
            destination = (Queue) ctx.lookup("/queue/EmployeeQueue");

        }
        catch (NamingException ne) {
            throw new IllegalStateException("Can not get from jndi", ne);
        }

        try (JMSContext jmsContext = connectionFactory.createContext("guest1", "guest1")) {

            jmsContext.createProducer().send(destination, "John Doe - client");
        }
    }
}
