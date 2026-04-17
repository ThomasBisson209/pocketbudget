package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.transaction.exceptions.TransactionNotFoundException;

@Provider
public class TransactionNotFoundExceptionMapper implements ExceptionMapper<TransactionNotFoundException> {
    @Override
    public Response toResponse(TransactionNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(new ErrorResponse(e.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
