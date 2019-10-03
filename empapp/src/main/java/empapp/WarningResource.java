package empapp;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.UUID;

@Path("warning")
@Singleton
public class WarningResource {

    @Inject
    private WarningService warningService;

    private Sse sse;

    private SseBroadcaster broadcaster;

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        broadcaster = sse.newBroadcaster();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Status crateWarning(WarningCommand command) {
        warningService.sendWarning(command);
        // broadcaster.broadcast(sse.newEvent(command.getMessage()));
        return new Status("ok");
    }

    public void handleEvent(@Observes @ForServerSideEvent String message) {
        OutboundSseEvent event = sse.newEventBuilder()
                .id(UUID.randomUUID().toString())
                .comment("New warning message")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .name("warning")
                .data(message)
        .build();

        broadcaster.broadcast(event);
    }

    @GET
    @Path("stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listWarnings(@Context SseEventSink sseEventSink,
                             @Context Sse sse) {
//        for (int i = 0; i < 10;i++) {
//            OutboundSseEvent event = see.newEvent("hello from sse");
//            sseEventSink.send(event);
//            try {
//                Thread.sleep(2000);
//            }
//            catch (InterruptedException ie) {
//                throw new IllegalStateException("Interrupted", ie);
//            }
//        }
        broadcaster.register(sseEventSink);
    }

}
