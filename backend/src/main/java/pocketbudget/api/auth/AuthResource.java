package pocketbudget.api.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pocketbudget.application.auth.AuthService;
import pocketbudget.application.auth.dtos.LoginDto;
import pocketbudget.application.auth.dtos.RegisterDto;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final AuthService authService;

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    public Response register(RegisterDto dto) {
        return Response.status(Response.Status.CREATED)
            .entity(authService.register(dto))
            .build();
    }

    @POST
    @Path("/login")
    public Response login(LoginDto dto) {
        return Response.ok(authService.login(dto)).build();
    }
}
