package pocketbudget.api.transaction;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.transaction.TransactionService;
import pocketbudget.application.transaction.dtos.CreateTransactionDto;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {
    private final TransactionService transactionService;

    @Context
    private ContainerRequestContext requestContext;

    @Inject
    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private String userId() {
        return (String) requestContext.getProperty("username");
    }

    @GET
    public Response getAllTransactions() {
        return Response.ok(transactionService.getAllTransactions(userId())).build();
    }

    @GET
    @Path("/{transactionId}")
    public Response getTransaction(@PathParam("transactionId") String id) {
        return Response.ok(transactionService.getTransaction(id, userId())).build();
    }

    @GET
    @Path("/account/{accountId}")
    public Response getByAccount(@PathParam("accountId") String accountId) {
        return Response.ok(transactionService.getTransactionsByAccount(accountId, userId())).build();
    }

    @POST
    public Response createTransaction(CreateTransactionDto dto) {
        return Response.status(Response.Status.CREATED)
            .entity(transactionService.createTransaction(dto, userId()))
            .build();
    }
}
