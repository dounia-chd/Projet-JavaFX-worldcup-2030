package ma.mondial2030.exception;

/**
 * Exception personnalisée pour les erreurs de base de données
 */
public class DatabaseException extends Exception {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
