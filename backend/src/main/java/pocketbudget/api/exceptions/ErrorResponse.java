package pocketbudget.api.exceptions;

public class ErrorResponse {
    public String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
