package empapp;

import java.util.Arrays;

public class EmployeeConverter {

    public Employee convert(CreateEmployeeCommand command) {
        Employee employee = new Employee();
        employee.setName(command.getName());
        if (command.getSkills() != null) {
            employee.setSkills(Arrays.asList(
                    command.getSkills().split(",")));
        }
        if (command.getCities() != null) {
            Arrays.stream(command
                    .getCities().split(","))
                    .map(Address::new)
                    .forEach(employee::addAddress);

        }
        return employee;
    }
}
