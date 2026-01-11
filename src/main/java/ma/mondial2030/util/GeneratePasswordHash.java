package ma.mondial2030.util;

import ma.mondial2030.security.PasswordHasher;

/**
 * Utilitaire temporaire pour générer un hash de mot de passe
 * Utilisez cette classe pour générer le hash correct pour les utilisateurs par défaut
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = PasswordHasher.hashPassword(password);
        System.out.println("Mot de passe: " + password);
        System.out.println("Hash BCrypt: " + hash);
    }
}
