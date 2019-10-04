package empapp;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.transaction.Transactional;
import java.util.ArrayList;
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
    public void createEmployee(CreateEmployeeCommand command) {
//        context.createProducer().send(queue, name);
        logEntryService.createLogEntry("Create employee with name "
                + command.getName());

        if (!employeeDao.existsEmployeeWithName(command.getName())) {
            Employee employee = new EmployeeConverter().convert(command);
            employeeDao.insertEmployee(employee);
        }
    }

//    @Transactional
    public List<EmployeeDto> listEmployees() {
        return employeeDao.listEmployees()
                .stream()
                .map(e -> new EmployeeDto(e.getId(), e.getName(),
//                        new ArrayList<>(e.getSkills())
                        e.getAddresses().stream().map(Address::getCity).collect(Collectors.toList())
                        ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addAddress(long id, CreateAddressCommand createAddressCommand) {
        employeeDao.addAddress(id, createAddressCommand.getCity());
    }
}
