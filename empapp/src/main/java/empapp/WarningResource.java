package empapp;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("warning")
public class WarningResource {

    @Inject
    private WarningService warningService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Status crateWarning(WarningCommand command) {
        warningService.sendWarning(command);
        return new Status("ok");
    }
}
