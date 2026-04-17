package pocketbudget.api.account;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.account.AccountService;
import pocketbudget.application.account.dtos.CreateAccountDto;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    private final AccountService accountService;

    @Inject
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    public Response getAllAccounts() {
        return Response.ok(accountService.getAllAccounts()).build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") String accountId) {
        return Response.ok(accountService.getAccount(accountId)).build();
    }

    @POST
    public Response createAccount(CreateAccountDto dto) {
        return Response.status(Response.Status.CREATED)
            .entity(accountService.createAccount(dto))
            .build();
    }

    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) {
        accountService.deleteAccount(accountId);
        return Response.noContent().build();
    }
}
