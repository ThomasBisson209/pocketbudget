package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.user.exceptions.UserAlreadyExistsException;

@Provider
public class UserAlreadyExistsExceptionMapper implements ExceptionMapper<UserAlreadyExistsException> {
    @Override
    public Response toResponse(UserAlreadyExistsException e) {
        return Response.status(Response.Status.CONFLICT)
            .entity(new ErrorResponse(e.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
