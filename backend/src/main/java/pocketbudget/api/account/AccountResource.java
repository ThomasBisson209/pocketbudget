package pocketbudget.api.account;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.account.AccountService;
import pocketbudget.application.account.dtos.CreateAccountDto;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    private final AccountService accountService;

    @Context
    private ContainerRequestContext requestContext;

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    private String userId() {
        return (String) requestContext.getProperty("username");
    }

    @GET
    public Response getAllAccounts() {
        return Response.ok(accountService.getAllAccounts(userId())).build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId) {
        return Response.ok(accountService.getAccount(accountId, userId())).build();
    }

    @POST
    public Response createAccount(CreateAccountDto dto) {
        return Response.status(Response.Status.CREATED)
            .entity(accountService.createAccount(dto, userId()))
            .build();
    }

    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) {
        accountService.deleteAccount(accountId, userId());
        return Response.noContent().build();
    }

    @GET
    @Path("/{accountId}/balance-history/month/{month}/year/{year}")
    public Response getBalanceHistory(
            @PathParam("accountId") String accountId,
            @PathParam("month") int month,
            @PathParam("year") int year) {
        return Response.ok(accountService.getBalanceHistory(accountId, month, year, userId())).build();
    }
}
