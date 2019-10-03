package empapp;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

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

    @GET
    @Path("stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listWarnings(@Context SseEventSink sseEventSink,
                             @Context Sse see) {
        for (int i = 0; i < 10;i++) {
            OutboundSseEvent event = see.newEvent("hello from sse");
            sseEventSink.send(event);
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException ie) {
                throw new IllegalStateException("Interrupted", ie);
            }
        }
    }
}
