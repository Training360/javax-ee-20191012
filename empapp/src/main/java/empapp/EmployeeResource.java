package empapp;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("employees")
public class EmployeeResource {

    @Inject
    private EmployeeService employeeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listEmployees() {
        return "John Doe";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Status createEmployee(CreateEmployeeCommand command) {
        employeeService.createEmployee(command.getName());
        return new Status("ok");
    }
}
