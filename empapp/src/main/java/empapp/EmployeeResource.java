package empapp;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("employees")
public class EmployeeResource {

//    @Inject
//    private EmployeeService employeeService;

    @Inject
    private EmployeeDao employeeDao;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> listEmployees() {
        return employeeDao.listEmployees();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Status createEmployee(CreateEmployeeCommand command) {
//        employeeService.createEmployee(command.getName());
        employeeDao.insertEmployee(new Employee(command.getName()));
        return new Status("ok");
    }
}
