package empapp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LogEntryService {

    @Inject
    private LogEntryDao logEntryDao;

    public void createLogEntry(String message) {
        try {
            logEntryDao.saveLogEntry(new LogEntry(message));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
