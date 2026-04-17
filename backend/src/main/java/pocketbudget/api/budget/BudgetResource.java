package pocketbudget.api.budget;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.budget.BudgetService;
import pocketbudget.application.budget.dtos.CreateBudgetDto;

@Path("/budgets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BudgetResource {
    private final BudgetService budgetService;

    @Context
    private ContainerRequestContext requestContext;

    @Inject
    public BudgetResource(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    private String userId() {
        return (String) requestContext.getProperty("username");
    }

    @GET
    public Response getAllBudgets() {
        return Response.ok(budgetService.getAllBudgets(userId())).build();
    }

    @GET
    @Path("/{budgetId}")
    public Response getBudget(@PathParam("budgetId") String budgetId) {
        return Response.ok(budgetService.getBudget(budgetId, userId())).build();
    }

    @GET
    @Path("/month/{month}/year/{year}")
    public Response getBudgetsByMonth(@PathParam("month") int month, @PathParam("year") int year) {
        return Response.ok(budgetService.getBudgetsByMonth(month, year, userId())).build();
    }

    @POST
    public Response createBudget(CreateBudgetDto dto) {
        return Response.status(Response.Status.CREATED)
            .entity(budgetService.createBudget(dto, userId()))
            .build();
    }

    @DELETE
    @Path("/{budgetId}")
    public Response deleteBudget(@PathParam("budgetId") String budgetId) {
        budgetService.deleteBudget(budgetId, userId());
        return Response.noContent().build();
    }
}
