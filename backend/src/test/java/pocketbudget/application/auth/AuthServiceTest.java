package pocketbudget.application.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pocketbudget.application.auth.dtos.LoginDto;
import pocketbudget.application.auth.dtos.RegisterDto;
import pocketbudget.application.auth.dtos.TokenDto;
import pocketbudget.domain.user.User;
import pocketbudget.domain.user.UserId;
import pocketbudget.domain.user.UserRepository;
import pocketbudget.domain.user.exceptions.InvalidCredentialsException;
import pocketbudget.domain.user.exceptions.UserAlreadyExistsException;
import pocketbudget.infra.auth.JwtService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepositoryMock;

    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = new AuthService(userRepositoryMock, new JwtService());
    }

    @Test
    void givenNewUsername_whenRegister_thenUserIsSavedAndTokenReturned() {
        when(userRepositoryMock.existsByUsername("alice")).thenReturn(false);

        RegisterDto dto = new RegisterDto();
        dto.username = "alice";
        dto.password = "password123";

        TokenDto result = authService.register(dto);

        verify(userRepositoryMock).save(any(User.class));
        assertEquals("alice", result.username);
        assertNotNull(result.token);
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowsUserAlreadyExistsException() {
        when(userRepositoryMock.existsByUsername("alice")).thenReturn(true);

        RegisterDto dto = new RegisterDto();
        dto.username = "alice";
        dto.password = "password123";

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(dto));
    }

    @Test
    void givenValidCredentials_whenLogin_thenTokenReturned() {
        String hashed = BCrypt.hashpw("secret", BCrypt.gensalt());
        User user = new User(UserId.generate(), "bob", hashed);
        when(userRepositoryMock.findByUsername("bob")).thenReturn(Optional.of(user));

        LoginDto dto = new LoginDto();
        dto.username = "bob";
        dto.password = "secret";

        TokenDto result = authService.login(dto);

        assertEquals("bob", result.username);
        assertNotNull(result.token);
    }

    @Test
    void givenWrongPassword_whenLogin_thenThrowsInvalidCredentialsException() {
        String hashed = BCrypt.hashpw("correct", BCrypt.gensalt());
        User user = new User(UserId.generate(), "bob", hashed);
        when(userRepositoryMock.findByUsername("bob")).thenReturn(Optional.of(user));

        LoginDto dto = new LoginDto();
        dto.username = "bob";
        dto.password = "wrong";

        assertThrows(InvalidCredentialsException.class, () -> authService.login(dto));
    }

    @Test
    void givenUnknownUsername_whenLogin_thenThrowsInvalidCredentialsException() {
        when(userRepositoryMock.findByUsername(any())).thenReturn(Optional.empty());

        LoginDto dto = new LoginDto();
        dto.username = "ghost";
        dto.password = "pass";

        assertThrows(InvalidCredentialsException.class, () -> authService.login(dto));
    }
}
