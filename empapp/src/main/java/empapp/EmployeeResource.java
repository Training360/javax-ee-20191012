package empapp;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("employees")
public class EmployeeResource {

    @Inject
    private EmployeeService employeeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EmployeeDto> listEmployees() {
        return employeeService.listEmployees();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Status createEmployee(CreateEmployeeCommand command) {
//        employeeService.createEmployee(command.getName());
        employeeService.createEmployee(command);
        return new Status("ok");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}/addresses")
    public Status addAddress(@PathParam("id") long id,
                             CreateAddressCommand createAddressCommand) {
        employeeService.addAddress(id, createAddressCommand);
        return new Status("ok");
    }

}
