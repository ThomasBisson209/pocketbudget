package pocketbudget.api.dashboard;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.dashboard.DashboardService;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {
    private final DashboardService dashboardService;

    @Inject
    public DashboardResource(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GET
    @Path("/month/{month}/year/{year}")
    public Response getDashboard(@PathParam("month") int month, @PathParam("year") int year) {
        return Response.ok(dashboardService.getDashboard(month, year)).build();
    }
}
