package pocketbudget.application.auth;

import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pocketbudget.application.auth.dtos.LoginDto;
import pocketbudget.application.auth.dtos.RegisterDto;
import pocketbudget.application.auth.dtos.TokenDto;
import pocketbudget.domain.user.User;
import pocketbudget.domain.user.UserId;
import pocketbudget.domain.user.UserRepository;
import pocketbudget.domain.user.exceptions.InvalidCredentialsException;
import pocketbudget.domain.user.exceptions.UserAlreadyExistsException;
import pocketbudget.infra.auth.JwtService;

public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Inject
    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public TokenDto register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.username)) {
            log.warn("Registration attempt for existing username: {}", dto.username);
            throw new UserAlreadyExistsException(dto.username);
        }
        String hashed = BCrypt.hashpw(dto.password, BCrypt.gensalt());
        User user = new User(UserId.generate(), dto.username, hashed);
        userRepository.save(user);
        log.info("New user registered: {}", dto.username);
        return new TokenDto(jwtService.generateToken(dto.username), dto.username);
    }

    public TokenDto login(LoginDto dto) {
        User user = userRepository.findByUsername(dto.username)
            .orElseThrow(() -> {
                log.warn("Login attempt for unknown username: {}", dto.username);
                return new InvalidCredentialsException();
            });
        if (!BCrypt.checkpw(dto.password, user.getHashedPassword())) {
            log.warn("Invalid password attempt for username: {}", dto.username);
            throw new InvalidCredentialsException();
        }
        log.info("User logged in: {}", dto.username);
        return new TokenDto(jwtService.generateToken(dto.username), dto.username);
    }
}
