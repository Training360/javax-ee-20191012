package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@ApplicationScoped
public class LogEntryDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveLogEntry(LogEntry logEntry) {
        em.persist(logEntry);
        // throw new IllegalArgumentException("NEM JO");
    }

}
