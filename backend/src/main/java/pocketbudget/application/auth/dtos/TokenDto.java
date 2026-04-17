package pocketbudget.application.auth.dtos;

public class TokenDto {
    public String token;
    public String username;

    public TokenDto(String token, String username) {
        this.token = token;
        this.username = username;
    }
}
