package ma.mondial2030.dao;

import ma.mondial2030.model.GateDevice;
import ma.mondial2030.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des dispositifs de porte
 */
public class GateDeviceDAO {
    private static final Logger logger = LoggerFactory.getLogger(GateDeviceDAO.class);

    /**
     * Trouve un dispositif par son ID
     */
    public GateDevice findById(int id) {
        String sql = "SELECT * FROM gate_devices WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGateDevice(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du dispositif par ID", e);
        }
        return null;
    }

    /**
     * Récupère tous les dispositifs actifs
     */
    public List<GateDevice> findAllActive() {
        List<GateDevice> devices = new ArrayList<>();
        String sql = "SELECT * FROM gate_devices WHERE is_active = TRUE ORDER BY device_name";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                devices.add(mapResultSetToGateDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des dispositifs actifs", e);
        }
        return devices;
    }

    /**
     * Mappe un ResultSet vers un objet GateDevice
     */
    private GateDevice mapResultSetToGateDevice(ResultSet rs) throws SQLException {
        GateDevice device = new GateDevice();
        device.setId(rs.getInt("id"));
        device.setDeviceName(rs.getString("device_name"));
        device.setDeviceLocation(rs.getString("device_location"));
        
        String deviceTypeStr = rs.getString("device_type");
        if (deviceTypeStr != null) {
            device.setDeviceType(GateDevice.Type.valueOf(deviceTypeStr));
        }
        
        device.setActive(rs.getBoolean("is_active"));
        
        Timestamp lastSync = rs.getTimestamp("last_sync");
        if (lastSync != null) {
            device.setLastSync(lastSync.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            device.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return device;
    }
}
