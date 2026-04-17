package pocketbudget.api.filters;

import io.jsonwebtoken.JwtException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import pocketbudget.infra.auth.JwtService;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String path = ctx.getUriInfo().getPath();
        if (path.startsWith("auth/") || path.equals("health")) {
            return;
        }

        String header = ctx.getHeaderString(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            abort(ctx, "Missing or invalid Authorization header");
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());
        try {
            String username = jwtService.validateTokenAndGetUsername(token);
            ctx.setProperty("username", username);
        } catch (JwtException e) {
            abort(ctx, "Invalid or expired token");
        }
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
            .entity("{\"error\":\"" + message + "\"}")
            .type(MediaType.APPLICATION_JSON)
            .build());
    }
}
