package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.budget.exceptions.BudgetNotFoundException;

@Provider
public class BudgetNotFoundExceptionMapper implements ExceptionMapper<BudgetNotFoundException> {
    @Override
    public Response toResponse(BudgetNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(new ErrorResponse(exception.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
