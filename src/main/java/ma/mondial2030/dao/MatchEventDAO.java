package ma.mondial2030.dao;

import ma.mondial2030.model.MatchEvent;
import ma.mondial2030.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des matchs/événements
 */
public class MatchEventDAO {
    private static final Logger logger = LoggerFactory.getLogger(MatchEventDAO.class);

    /**
     * Trouve un match par son ID
     */
    public MatchEvent findById(int id) {
        String sql = "SELECT * FROM match_events WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMatchEvent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du match par ID", e);
        }
        return null;
    }

    /**
     * Récupère tous les matchs
     */
    public List<MatchEvent> findAll() {
        List<MatchEvent> matchEvents = new ArrayList<>();
        String sql = "SELECT * FROM match_events ORDER BY match_date ASC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                matchEvents.add(mapResultSetToMatchEvent(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de tous les matchs", e);
        }
        return matchEvents;
    }

    /**
     * Récupère les matchs à venir
     */
    public List<MatchEvent> findUpcoming() {
        List<MatchEvent> matchEvents = new ArrayList<>();
        String sql = "SELECT * FROM match_events WHERE status = 'UPCOMING' AND match_date > NOW() ORDER BY match_date ASC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                matchEvents.add(mapResultSetToMatchEvent(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des matchs à venir", e);
        }
        return matchEvents;
    }

    /**
     * Crée un nouveau match
     */
    public boolean create(MatchEvent matchEvent) {
        String sql = "INSERT INTO match_events (match_name, match_date, venue, team_a, team_b, total_capacity, available_tickets, ticket_price, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, matchEvent.getMatchName());
            stmt.setTimestamp(2, Timestamp.valueOf(matchEvent.getMatchDate()));
            stmt.setString(3, matchEvent.getVenue());
            stmt.setString(4, matchEvent.getTeamA());
            stmt.setString(5, matchEvent.getTeamB());
            stmt.setInt(6, matchEvent.getTotalCapacity());
            stmt.setInt(7, matchEvent.getAvailableTickets());
            stmt.setBigDecimal(8, matchEvent.getTicketPrice());
            stmt.setString(9, matchEvent.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    matchEvent.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du match", e);
        }
        return false;
    }

    /**
     * Met à jour les tickets disponibles
     */
    public boolean updateAvailableTickets(int matchEventId, int newAvailableTickets) {
        String sql = "UPDATE match_events SET available_tickets = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newAvailableTickets);
            stmt.setInt(2, matchEventId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour des tickets disponibles", e);
        }
        return false;
    }

    /**
     * Mappe un ResultSet vers un objet MatchEvent
     */
    private MatchEvent mapResultSetToMatchEvent(ResultSet rs) throws SQLException {
        MatchEvent matchEvent = new MatchEvent();
        matchEvent.setId(rs.getInt("id"));
        matchEvent.setMatchName(rs.getString("match_name"));
        
        Timestamp matchDate = rs.getTimestamp("match_date");
        if (matchDate != null) {
            matchEvent.setMatchDate(matchDate.toLocalDateTime());
        }
        
        matchEvent.setVenue(rs.getString("venue"));
        matchEvent.setTeamA(rs.getString("team_a"));
        matchEvent.setTeamB(rs.getString("team_b"));
        matchEvent.setTotalCapacity(rs.getInt("total_capacity"));
        matchEvent.setAvailableTickets(rs.getInt("available_tickets"));
        matchEvent.setTicketPrice(rs.getBigDecimal("ticket_price"));
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            matchEvent.setStatus(MatchEvent.Status.valueOf(statusStr));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            matchEvent.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            matchEvent.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return matchEvent;
    }
}
