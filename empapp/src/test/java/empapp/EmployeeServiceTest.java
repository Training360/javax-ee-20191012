package empapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @Mock
    EmployeeDao employeeDao;

    @Mock
    LogEntryService logEntryService;

    @InjectMocks
    EmployeeService employeeService;

    @Test
    public void testShouldCallInsert() {
        CreateEmployeeCommand command = new CreateEmployeeCommand();
        command.setName("John Doe");
        employeeService.createEmployee(command);

        verify(employeeDao).insertEmployee(any());
    }
}
