package empapp;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EmployeeService {

    @Inject
    private JMSContext context;

    @Resource(mappedName = "java:/jms/queue/EmployeeQueue")
    private Queue queue;

    @Inject
    private EmployeeDao employeeDao;

    @Inject
    private LogEntryService logEntryService;

    @Transactional
    public void createEmployee(CreateEmployeeCommand createEmployeeCommand) {
        employeeDao.insertEmployee(new Employee(createEmployeeCommand.getName()));
//        context.createProducer().send(queue, name);
        logEntryService.createLogEntry("Employee has created with name "
                + createEmployeeCommand.getName());
    }

    public List<EmployeeDto> listEmployees() {
        return employeeDao.listEmployees()
                .stream()
                .map(e -> new EmployeeDto(e.getId(), e.getName()))
                .collect(Collectors.toList());
    }
}
