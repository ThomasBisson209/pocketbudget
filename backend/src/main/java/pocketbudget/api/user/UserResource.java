package pocketbudget.api.user;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.auth.AuthService;
import pocketbudget.application.auth.dtos.ChangePasswordDto;
import pocketbudget.infra.auth.JwtService;

import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final AuthService authService;
    private final JwtService jwtService;

    @Inject
    public UserResource(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GET
    @Path("/me")
    public Response getProfile(@Context HttpHeaders headers) {
        String username = extractUsername(headers);
        return Response.ok(Map.of("username", username)).build();
    }

    @PUT
    @Path("/me/password")
    public Response changePassword(@Context HttpHeaders headers, ChangePasswordDto dto) {
        String username = extractUsername(headers);
        authService.changePassword(username, dto);
        return Response.noContent().build();
    }

    private String extractUsername(HttpHeaders headers) {
        String authHeader = headers.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return jwtService.validateTokenAndGetUsername(authHeader.substring(7));
    }
}
