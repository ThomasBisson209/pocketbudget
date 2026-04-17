package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.account.exceptions.InvalidBalanceException;

@Provider
public class InvalidBalanceExceptionMapper implements ExceptionMapper<InvalidBalanceException> {
    @Override
    public Response toResponse(InvalidBalanceException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(new ErrorResponse(exception.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
