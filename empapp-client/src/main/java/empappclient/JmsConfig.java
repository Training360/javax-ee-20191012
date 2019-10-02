package empappclient;

import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JmsConfig {

    @Produces
    public Context createContext() throws NamingException {
        Properties jndiProperties = new Properties();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.jboss.naming.remote.client.InitialContextFactory");
        jndiProperties.put(Context.URL_PKG_PREFIXES,
                "org.jboss.ejb.client.naming");
        jndiProperties.put(Context.PROVIDER_URL,
                "http-remoting://localhost:8080");
        jndiProperties.put("jboss.naming.client.ejb.context", true);

        return new InitialContext(jndiProperties);
    }

    @Produces
    public ConnectionFactory createConnectionFactory(Context context)
        throws NamingException {
        return  (ConnectionFactory) context
                .lookup("jms/RemoteConnectionFactory");
    }

    @Produces
    public Queue createQueue(Context context)
            throws NamingException {
        return  (Queue) context
                .lookup("/queue/EmployeeQueue");
    }
}
