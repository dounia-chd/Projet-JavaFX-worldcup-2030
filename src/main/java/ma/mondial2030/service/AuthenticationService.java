package ma.mondial2030.service;

import ma.mondial2030.dao.UserDAO;
import ma.mondial2030.exception.AuthenticationException;
import ma.mondial2030.model.User;
import ma.mondial2030.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service d'authentification
 */
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserDAO userDAO = new UserDAO();
    private static User currentUser;

    /**
     * Authentifie un utilisateur
     */
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Le nom d'utilisateur est requis");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Le mot de passe est requis");
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            logger.warn("Tentative de connexion avec un nom d'utilisateur inexistant: {}", username);
            throw new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect");
        }

        if (!user.isActive()) {
            logger.warn("Tentative de connexion avec un compte désactivé: {}", username);
            throw new AuthenticationException("Ce compte est désactivé");
        }

        if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            logger.warn("Tentative de connexion avec un mot de passe incorrect pour: {}", username);
            throw new AuthenticationException("Nom d'utilisateur ou mot de passe incorrect");
        }

        currentUser = user;
        logger.info("Utilisateur connecté avec succès: {}", username);
        return user;
    }

    /**
     * Déconnecte l'utilisateur actuel
     */
    public void logout() {
        if (currentUser != null) {
            logger.info("Déconnexion de l'utilisateur: {}", currentUser.getUsername());
        }
        currentUser = null;
    }

    /**
     * Obtient l'utilisateur actuellement connecté
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Vérifie si l'utilisateur actuel a un rôle spécifique
     */
    public static boolean hasRole(String roleName) {
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        return currentUser.getRole().getName().equals(roleName);
    }

    /**
     * Vérifie si l'utilisateur actuel est un membre du staff
     * (Staff a maintenant tous les droits d'admin : gestion tickets, annonces, etc.)
     */
    public static boolean isStaff() {
        return hasRole("STAFF");
    }

    /**
     * Vérifie si l'utilisateur actuel est un supporter
     */
    public static boolean isSupporter() {
        return hasRole("SUPPORTER");
    }
}
