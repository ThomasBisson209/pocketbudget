package pocketbudget.api.dashboard;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.dashboard.DashboardService;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {
    private final DashboardService dashboardService;

    @Context
    private ContainerRequestContext requestContext;

    @Inject
    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private String userId() {
        return (String) requestContext.getProperty("username");
    }

    @GET
    @Path("/month/{month}/year/{year}")
    public Response getDashboard(@PathParam("month") int month, @PathParam("year") int year) {
        return Response.ok(dashboardService.getDashboard(month, year, userId())).build();
    }
}
