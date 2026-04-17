package pocketbudget.api.exceptions.mappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.api.exceptions.ErrorResponse;
import pocketbudget.domain.budget.exceptions.BudgetLimitExceededException;

@Provider
public class BudgetLimitExceededExceptionMapper implements ExceptionMapper<BudgetLimitExceededException> {
    @Override
    public Response toResponse(BudgetLimitExceededException e) {
        return Response.status(422)
            .entity(new ErrorResponse(e.getMessage()))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
