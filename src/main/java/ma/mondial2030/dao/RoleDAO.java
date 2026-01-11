package ma.mondial2030.dao;

import ma.mondial2030.model.Role;
import ma.mondial2030.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des rôles
 */
public class RoleDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoleDAO.class);

    /**
     * Trouve un rôle par son ID
     */
    public Role findById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRole(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du rôle par ID", e);
        }
        return null;
    }

    /**
     * Trouve un rôle par son nom
     */
    public Role findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRole(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du rôle par nom", e);
        }
        return null;
    }

    /**
     * Récupère tous les rôles
     */
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les rôles", e);
        }
        return roles;
    }

    /**
     * Mappe un ResultSet vers un objet Role
     */
    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            role.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return role;
    }
}
