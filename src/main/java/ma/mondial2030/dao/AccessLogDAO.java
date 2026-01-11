package ma.mondial2030.dao;

import ma.mondial2030.model.AccessLog;
import ma.mondial2030.model.GateDevice;
import ma.mondial2030.model.Ticket;
import ma.mondial2030.model.User;
import ma.mondial2030.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des logs d'accès
 */
public class AccessLogDAO {
    private static final Logger logger = LoggerFactory.getLogger(AccessLogDAO.class);
    private final UserDAO userDAO = new UserDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    /**
     * Crée un nouveau log d'accès
     */
    public boolean create(AccessLog accessLog) {
        String sql = "INSERT INTO access_logs (user_id, ticket_id, gate_device_id, access_type, access_result, denial_reason, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (accessLog.getUser() != null) {
                stmt.setInt(1, accessLog.getUser().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            
            if (accessLog.getTicket() != null) {
                stmt.setInt(2, accessLog.getTicket().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            if (accessLog.getGateDevice() != null) {
                stmt.setInt(3, accessLog.getGateDevice().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, accessLog.getAccessType().name());
            stmt.setString(5, accessLog.getAccessResult().name());
            stmt.setString(6, accessLog.getDenialReason());
            stmt.setString(7, accessLog.getIpAddress());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    accessLog.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du log d'accès", e);
        }
        return false;
    }

    /**
     * Récupère tous les logs d'accès
     */
    public List<AccessLog> findAll() {
        List<AccessLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM access_logs ORDER BY access_timestamp DESC LIMIT 1000";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                logs.add(mapResultSetToAccessLog(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des logs d'accès", e);
        }
        return logs;
    }

    /**
     * Récupère les logs d'accès d'un utilisateur
     */
    public List<AccessLog> findByUserId(int userId) {
        List<AccessLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM access_logs WHERE user_id = ? ORDER BY access_timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAccessLog(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des logs d'accès de l'utilisateur", e);
        }
        return logs;
    }

    /**
     * Mappe un ResultSet vers un objet AccessLog
     */
    private AccessLog mapResultSetToAccessLog(ResultSet rs) throws SQLException {
        // Lire toutes les données du ResultSet AVANT d'appeler les autres DAO
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        boolean userIdWasNull = rs.wasNull();
        int ticketId = rs.getInt("ticket_id");
        boolean ticketIdWasNull = rs.wasNull();
        int gateDeviceId = rs.getInt("gate_device_id");
        boolean gateDeviceIdWasNull = rs.wasNull();
        String accessTypeStr = rs.getString("access_type");
        String accessResultStr = rs.getString("access_result");
        String denialReason = rs.getString("denial_reason");
        String ipAddress = rs.getString("ip_address");
        Timestamp accessTimestamp = rs.getTimestamp("access_timestamp");
        
        // Maintenant on peut appeler les autres DAO
        AccessLog log = new AccessLog();
        log.setId(id);
        
        if (!userIdWasNull) {
            User user = userDAO.findById(userId);
            log.setUser(user);
        }
        
        if (!ticketIdWasNull) {
            Ticket ticket = ticketDAO.findById(ticketId);
            log.setTicket(ticket);
        }
        
        if (!gateDeviceIdWasNull) {
            // GateDevice sera chargé si nécessaire
            GateDevice gateDevice = new GateDevice();
            gateDevice.setId(gateDeviceId);
            log.setGateDevice(gateDevice);
        }
        
        if (accessTypeStr != null) {
            log.setAccessType(AccessLog.AccessType.valueOf(accessTypeStr));
        }
        
        if (accessResultStr != null) {
            log.setAccessResult(AccessLog.AccessResult.valueOf(accessResultStr));
        }
        
        log.setDenialReason(denialReason);
        log.setIpAddress(ipAddress);
        
        if (accessTimestamp != null) {
            log.setAccessTimestamp(accessTimestamp.toLocalDateTime());
        }
        
        return log;
    }
}
