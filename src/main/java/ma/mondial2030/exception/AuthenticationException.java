package ma.mondial2030.exception;

/**
 * Exception personnalisée pour les erreurs d'authentification
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
