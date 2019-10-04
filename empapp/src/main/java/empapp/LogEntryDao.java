package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class LogEntryDao {

    @PersistenceContext
    private EntityManager em;

    public void saveLogEntry(LogEntry logEntry) {
        em.persist(logEntry);
    }

}
