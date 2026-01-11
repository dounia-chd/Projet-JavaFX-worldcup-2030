package ma.mondial2030.dao;

import ma.mondial2030.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * DAO pour la gestion des données biométriques
 */
public class BiometricDataDAO {
    private static final Logger logger = LoggerFactory.getLogger(BiometricDataDAO.class);

    /**
     * Vérifie si l'utilisateur a des données biométriques
     */
    public boolean hasBiometricData(int userId) {
        String sql = "SELECT has_face_data FROM biometric_data WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("has_face_data");
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification des données biométriques", e);
        }
        return false;
    }

    /**
     * Sauvegarde une empreinte faciale
     */
    public boolean saveFaceEmbedding(int userId, byte[] embedding, String imagePath) {
        String sql = "INSERT INTO face_embeddings (biometric_data_id, embedding_data, image_path) " +
                     "VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            // Vérifier si biometric_data existe, sinon le créer
            int biometricDataId = getOrCreateBiometricData(userId, conn);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, biometricDataId);
                stmt.setBytes(2, embedding);
                stmt.setString(3, imagePath);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    // Mettre à jour le flag has_face_data
                    updateBiometricDataFlag(userId, true, false, conn);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde de l'empreinte faciale", e);
        }
        return false;
    }

    /**
     * Assign la biométrie à un utilisateur
     */
    public boolean assignBiometricToUser(int userId) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            return updateBiometricDataFlag(userId, true, false, conn);
        } catch (SQLException e) {
            logger.error("Erreur lors de l'assignation de la biométrie", e);
            return false;
        }
    }

    /**
     * Obtient ou crée l'entrée biometric_data pour un utilisateur
     */
    private int getOrCreateBiometricData(int userId, Connection conn) throws SQLException {
        String selectSql = "SELECT id FROM biometric_data WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // Créer l'entrée si elle n'existe pas
        String insertSql = "INSERT INTO biometric_data (user_id, has_face_data, has_fingerprint_data) " +
                           "VALUES (?, FALSE, FALSE)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Impossible de créer l'entrée biometric_data");
    }

    /**
     * Met à jour les flags de données biométriques
     */
    private boolean updateBiometricDataFlag(int userId, boolean hasFace, boolean hasFingerprint, Connection conn) throws SQLException {
        // S'assurer que l'entrée existe
        getOrCreateBiometricData(userId, conn);
        
        String sql = "UPDATE biometric_data SET has_face_data = ?, has_fingerprint_data = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, hasFace);
            stmt.setBoolean(2, hasFingerprint);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
