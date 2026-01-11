package ma.mondial2030.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitaire simple pour générer un hash de mot de passe
 */
public class SimpleHashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("\nSQL UPDATE:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';");
    }
}
