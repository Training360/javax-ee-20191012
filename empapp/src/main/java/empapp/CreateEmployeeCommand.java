package empapp;

import lombok.Data;

@Data
public class CreateEmployeeCommand {

    private String name;

    private String skills;

    private String cities;
}
