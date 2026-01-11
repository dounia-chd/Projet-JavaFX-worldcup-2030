package ma.mondial2030.service;

import ma.mondial2030.dao.MatchEventDAO;
import ma.mondial2030.dao.TicketDAO;
import ma.mondial2030.model.MatchEvent;
import ma.mondial2030.model.Ticket;
import ma.mondial2030.model.User;
import ma.mondial2030.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des tickets
 */
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);
    private final TicketDAO ticketDAO = new TicketDAO();
    private final MatchEventDAO matchEventDAO = new MatchEventDAO();

    /**
     * Achète un ticket pour un utilisateur
     */
    public Ticket purchaseTicket(User user, int matchEventId, String seatNumber) {
        MatchEvent matchEvent = matchEventDAO.findById(matchEventId);
        if (matchEvent == null) {
            logger.error("Match non trouvé: {}", matchEventId);
            return null;
        }

        if (matchEvent.getAvailableTickets() <= 0) {
            logger.warn("Plus de tickets disponibles pour le match: {}", matchEventId);
            return null;
        }

        // Générer un code de ticket unique
        String ticketCode = generateTicketCode();
        
        // Créer le ticket
        Ticket ticket = new Ticket();
        ticket.setTicketCode(ticketCode);
        ticket.setUser(user);
        ticket.setMatchEvent(matchEvent);
        ticket.setSeatNumber(seatNumber);
        ticket.setStatus(Ticket.Status.VALID);
        ticket.setPurchaseDate(LocalDateTime.now());
        
        // Générer les données QR Code
        String qrData = QRCodeGenerator.generateTicketQRData(ticketCode, user.getId(), matchEventId);
        ticket.setQrCodeData(qrData);

        // Sauvegarder le ticket
        if (ticketDAO.create(ticket)) {
            // Mettre à jour les tickets disponibles
            matchEventDAO.updateAvailableTickets(matchEventId, matchEvent.getAvailableTickets() - 1);
            logger.info("Ticket créé avec succès: {} pour l'utilisateur {}", ticketCode, user.getUsername());
            return ticket;
        }

        return null;
    }

    /**
     * Récupère tous les tickets d'un utilisateur
     */
    public List<Ticket> getUserTickets(int userId) {
        return ticketDAO.findByUserId(userId);
    }

    /**
     * Valide un ticket (lors du scan)
     */
    public boolean validateTicket(String ticketCode) {
        Ticket ticket = ticketDAO.findByCode(ticketCode);
        if (ticket == null) {
            logger.warn("Ticket non trouvé: {}", ticketCode);
            return false;
        }

        if (ticket.getStatus() != Ticket.Status.VALID) {
            logger.warn("Ticket invalide ou déjà utilisé: {}", ticketCode);
            return false;
        }

        // Marquer le ticket comme utilisé
        ticketDAO.updateStatus(ticket.getId(), Ticket.Status.USED);
        logger.info("Ticket validé et marqué comme utilisé: {}", ticketCode);
        return true;
    }

    /**
     * Génère un code de ticket unique
     */
    private String generateTicketCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TKT-" + timestamp + "-" + uniqueId;
    }
}
