package pocketbudget.domain.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @EmbeddedId
    private UserId userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String hashedPassword;

    protected User() {}

    public User(UserId userId, String username, String hashedPassword) {
        this.userId = userId;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
