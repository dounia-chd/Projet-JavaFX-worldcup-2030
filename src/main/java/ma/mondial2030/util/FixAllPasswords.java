package ma.mondial2030.util;

import ma.mondial2030.dao.UserDAO;
import ma.mondial2030.model.User;
import ma.mondial2030.security.PasswordHasher;

/**
 * Utilitaire pour corriger tous les mots de passe des utilisateurs de test
 * Ce script met à jour les mots de passe pour :
 * - staff1 : password123
 * - supporter1 : password123
 * - supporter2 : password123
 * 
 * IMPORTANT: Exécutez ce script après avoir créé les utilisateurs dans la base de données
 * pour corriger les hashs BCrypt et permettre la connexion.
 */
public class FixAllPasswords {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            
            // Liste des utilisateurs et leurs mots de passe
            String[][] users = {
                {"staff1", "password123"},
                {"supporter1", "password123"},
                {"supporter2", "password123"}
            };
            
            System.out.println("=== Correction des mots de passe ===\n");
            System.out.println("Ce script met à jour les hashs BCrypt pour permettre la connexion.\n");
            
            int successCount = 0;
            int failCount = 0;
            
            for (String[] userInfo : users) {
                String username = userInfo[0];
                String password = userInfo[1];
                
                User user = userDAO.findByUsername(username);
                
                if (user == null) {
                    System.out.println("✗ Utilisateur '" + username + "' non trouvé");
                    System.out.println("  → Créez d'abord l'utilisateur dans la base de données\n");
                    failCount++;
                    continue;
                }
                
                // Générer un nouveau hash
                String newHash = PasswordHasher.hashPassword(password);
                
                // Mettre à jour le mot de passe
                boolean success = userDAO.updatePassword(user.getId(), newHash);
                
                if (success) {
                    System.out.println("✓ " + username + " : mot de passe mis à jour");
                    System.out.println("  Password: " + password);
                    System.out.println("  Hash: " + newHash);
                    System.out.println();
                    successCount++;
                } else {
                    System.out.println("✗ Erreur lors de la mise à jour de " + username + "\n");
                    failCount++;
                }
            }
            
            System.out.println("=== Résumé ===");
            System.out.println("✓ Réussis: " + successCount);
            System.out.println("✗ Échoués: " + failCount);
            System.out.println("\n=== Correction terminée ===");
            
            if (successCount > 0) {
                System.out.println("\nVous pouvez maintenant vous connecter avec:");
                for (String[] userInfo : users) {
                    System.out.println("  - " + userInfo[0] + " / " + userInfo[1]);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
