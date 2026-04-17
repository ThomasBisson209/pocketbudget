package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.user.exceptions.InvalidCredentialsException;

@Provider
public class InvalidCredentialsExceptionMapper implements ExceptionMapper<InvalidCredentialsException> {
    @Override
    public Response toResponse(InvalidCredentialsException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(new ErrorResponse(e.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
