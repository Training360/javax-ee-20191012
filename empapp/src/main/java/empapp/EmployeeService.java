package empapp;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.transaction.Transactional;
import java.util.Arrays;
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
//        context.createProducer().send(queue, name);
        logEntryService.createLogEntry("Employee has created with name "
                + createEmployeeCommand.getName());

        Employee employee = new Employee();
        employee.setName(createEmployeeCommand.getName());
        employee.setSkills(Arrays.asList(
                createEmployeeCommand.getSkills().split(",")));

        employeeDao.insertEmployee(employee);
    }

    public List<EmployeeDto> listEmployees() {
        return employeeDao.listEmployees()
                .stream()
                .map(e -> new EmployeeDto(e.getId(), e.getName()))
                .collect(Collectors.toList());
    }
}
