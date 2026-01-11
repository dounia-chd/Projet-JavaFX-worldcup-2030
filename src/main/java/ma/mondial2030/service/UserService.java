package ma.mondial2030.service;

import ma.mondial2030.dao.RoleDAO;
import ma.mondial2030.dao.UserDAO;
import ma.mondial2030.model.Role;
import ma.mondial2030.model.User;
import ma.mondial2030.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service pour la gestion des utilisateurs
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    /**
     * Crée un nouvel utilisateur
     */
    public boolean registerUser(String username, String email, String password, 
                               String firstName, String lastName, String phone, String roleName) {
        // Vérifier si l'utilisateur existe déjà
        if (userDAO.findByUsername(username) != null) {
            logger.warn("Tentative d'inscription avec un nom d'utilisateur existant: {}", username);
            return false;
        }

        if (userDAO.findByEmail(email) != null) {
            logger.warn("Tentative d'inscription avec un email existant: {}", email);
            return false;
        }

        // Récupérer le rôle
        Role role = roleDAO.findByName(roleName);
        if (role == null) {
            logger.error("Rôle non trouvé: {}", roleName);
            return false;
        }

        // Créer l'utilisateur
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordHasher.hashPassword(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setRole(role);
        user.setActive(true);

        boolean success = userDAO.create(user);
        if (success) {
            logger.info("Nouvel utilisateur créé: {}", username);
        }
        return success;
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Met à jour un utilisateur
     */
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }

    /**
     * Trouve un utilisateur par son ID
     */
    public User findUserById(int id) {
        return userDAO.findById(id);
    }
}
