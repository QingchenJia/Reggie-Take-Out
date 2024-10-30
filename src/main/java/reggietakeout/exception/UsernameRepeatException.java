package reggietakeout.exception;

public class UsernameRepeatException extends RuntimeException {
    public UsernameRepeatException(String message) {
        super(message);
    }
}
