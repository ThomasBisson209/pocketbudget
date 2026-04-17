package pocketbudget.api.filters;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RequestLoggerFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger requestLog = LoggerFactory.getLogger("REQUEST");
    private static final String START_TIME = "startTime";

    @Override
    public void filter(ContainerRequestContext ctx) {
        ctx.setProperty(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext res) {
        Long start = (Long) req.getProperty(START_TIME);
        long duration = start != null ? System.currentTimeMillis() - start : -1;
        requestLog.info("{} {} -> {} ({}ms)",
            req.getMethod(),
            req.getUriInfo().getRequestUri().getPath(),
            res.getStatus(),
            duration);
    }
}
