package ma.mondial2030.dao;

import ma.mondial2030.model.MatchEvent;
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
 * DAO pour la gestion des tickets
 */
public class TicketDAO {
    private static final Logger logger = LoggerFactory.getLogger(TicketDAO.class);
    private final UserDAO userDAO = new UserDAO();
    private final MatchEventDAO matchEventDAO = new MatchEventDAO();

    /**
     * Trouve un ticket par son ID
     */
    public Ticket findById(int id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du ticket par ID", e);
        }
        return null;
    }

    /**
     * Trouve un ticket par son code
     */
    public Ticket findByCode(String ticketCode) {
        String sql = "SELECT * FROM tickets WHERE ticket_code = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticketCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du ticket par code", e);
        }
        return null;
    }

    /**
     * Récupère tous les tickets d'un utilisateur
     */
    public List<Ticket> findByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE user_id = ? ORDER BY purchase_date DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                // Lire toutes les données primitives d'abord
                List<TicketData> ticketDataList = new ArrayList<>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String ticketCode = rs.getString("ticket_code");
                    String seatNumber = rs.getString("seat_number");
                    String qrCodeData = rs.getString("qr_code_data");
                    String statusStr = rs.getString("status");
                    int ticketUserId = rs.getInt("user_id");
                    int matchEventId = rs.getInt("match_event_id");
                    Timestamp purchaseDate = rs.getTimestamp("purchase_date");
                    
                    ticketDataList.add(new TicketData(id, ticketCode, seatNumber, qrCodeData, 
                                                      statusStr, ticketUserId, matchEventId, purchaseDate));
                }
                
                // Maintenant construire les objets Ticket avec les données lues
                for (TicketData data : ticketDataList) {
                    User user = userDAO.findById(data.userId);
                    MatchEvent matchEvent = matchEventDAO.findById(data.matchEventId);
                    
                    Ticket ticket = new Ticket();
                    ticket.setId(data.id);
                    ticket.setTicketCode(data.ticketCode);
                    ticket.setSeatNumber(data.seatNumber);
                    ticket.setQrCodeData(data.qrCodeData);
                    
                    if (data.statusStr != null) {
                        ticket.setStatus(Ticket.Status.valueOf(data.statusStr));
                    }
                    
                    ticket.setUser(user);
                    ticket.setMatchEvent(matchEvent);
                    
                    if (data.purchaseDate != null) {
                        ticket.setPurchaseDate(data.purchaseDate.toLocalDateTime());
                    }
                    
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des tickets de l'utilisateur", e);
        }
        return tickets;
    }
    
    /**
     * Classe interne pour stocker temporairement les données du ticket
     */
    private static class TicketData {
        final int id;
        final String ticketCode;
        final String seatNumber;
        final String qrCodeData;
        final String statusStr;
        final int userId;
        final int matchEventId;
        final Timestamp purchaseDate;
        
        TicketData(int id, String ticketCode, String seatNumber, String qrCodeData,
                   String statusStr, int userId, int matchEventId, Timestamp purchaseDate) {
            this.id = id;
            this.ticketCode = ticketCode;
            this.seatNumber = seatNumber;
            this.qrCodeData = qrCodeData;
            this.statusStr = statusStr;
            this.userId = userId;
            this.matchEventId = matchEventId;
            this.purchaseDate = purchaseDate;
        }
    }

    /**
     * Crée un nouveau ticket
     */
    public boolean create(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_code, user_id, match_event_id, seat_number, qr_code_data, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, ticket.getTicketCode());
            stmt.setInt(2, ticket.getUser().getId());
            stmt.setInt(3, ticket.getMatchEvent().getId());
            stmt.setString(4, ticket.getSeatNumber());
            stmt.setString(5, ticket.getQrCodeData());
            stmt.setString(6, ticket.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du ticket", e);
        }
        return false;
    }

    /**
     * Met à jour le statut d'un ticket
     */
    public boolean updateStatus(int ticketId, Ticket.Status status) {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, ticketId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du statut du ticket", e);
        }
        return false;
    }

    /**
     * Mappe un ResultSet vers un objet Ticket
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        // Lire toutes les données du ResultSet AVANT d'appeler les autres DAO
        int id = rs.getInt("id");
        String ticketCode = rs.getString("ticket_code");
        String seatNumber = rs.getString("seat_number");
        String qrCodeData = rs.getString("qr_code_data");
        String statusStr = rs.getString("status");
        int userId = rs.getInt("user_id");
        int matchEventId = rs.getInt("match_event_id");
        Timestamp purchaseDate = rs.getTimestamp("purchase_date");
        
        // Maintenant on peut appeler les autres DAO
        User user = userDAO.findById(userId);
        MatchEvent matchEvent = matchEventDAO.findById(matchEventId);
        
        // Construire l'objet Ticket
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setTicketCode(ticketCode);
        ticket.setSeatNumber(seatNumber);
        ticket.setQrCodeData(qrCodeData);
        
        if (statusStr != null) {
            ticket.setStatus(Ticket.Status.valueOf(statusStr));
        }
        
        ticket.setUser(user);
        ticket.setMatchEvent(matchEvent);
        
        if (purchaseDate != null) {
            ticket.setPurchaseDate(purchaseDate.toLocalDateTime());
        }
        
        return ticket;
    }
}
