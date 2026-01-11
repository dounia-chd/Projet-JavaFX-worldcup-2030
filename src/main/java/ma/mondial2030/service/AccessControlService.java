package ma.mondial2030.service;

import ma.mondial2030.dao.AccessLogDAO;
import ma.mondial2030.dao.GateDeviceDAO;
import ma.mondial2030.dao.TicketDAO;
import ma.mondial2030.model.AccessLog;
import ma.mondial2030.model.GateDevice;
import ma.mondial2030.model.Ticket;
import ma.mondial2030.util.QRCodeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service pour le contrôle d'accès
 */
public class AccessControlService {
    private static final Logger logger = LoggerFactory.getLogger(AccessControlService.class);
    private final TicketDAO ticketDAO = new TicketDAO();
    private final AccessLogDAO accessLogDAO = new AccessLogDAO();
    private final GateDeviceDAO gateDeviceDAO = new GateDeviceDAO();
    private final TicketService ticketService = new TicketService();

    /**
     * Traite une tentative d'accès via QR Code
     * Accepte soit le format complet du QR Code (TICKET:CODE:USER:ID:MATCH:ID)
     * soit simplement le code du ticket
     */
    public AccessResult processQRCodeAccess(String qrCodeData, int gateDeviceId) {
        if (qrCodeData == null || qrCodeData.trim().isEmpty()) {
            logAccess(null, null, gateDeviceId, AccessLog.AccessType.TICKET, 
                     AccessLog.AccessResult.DENIED, "QR Code vide", null);
            return new AccessResult(false, "QR Code vide");
        }

        String ticketCode = null;
        Integer expectedMatchEventId = null;
        
        // Essayer de parser les données QR Code au format complet
        QRCodeReader.TicketQRData qrData = QRCodeReader.parseTicketQRData(qrCodeData);
        if (qrData != null) {
            // Format complet détecté
            ticketCode = qrData.getTicketCode();
            expectedMatchEventId = qrData.getMatchEventId();
        } else {
            // Si le parsing échoue, supposer que c'est juste le code du ticket
            // Vérifier si ça commence par "TKT-" (format de code de ticket)
            String trimmed = qrCodeData.trim();
            if (trimmed.startsWith("TKT-")) {
                ticketCode = trimmed;
            } else {
                // Essayer quand même de trouver un ticket avec ce code
                ticketCode = trimmed;
            }
        }

        // Trouver le ticket
        Ticket ticket = ticketDAO.findByCode(ticketCode);
        if (ticket == null) {
            logAccess(null, null, gateDeviceId, AccessLog.AccessType.TICKET, 
                     AccessLog.AccessResult.DENIED, "Ticket non trouvé: " + ticketCode, null);
            return new AccessResult(false, "Ticket non trouvé");
        }

        // Vérifier si le ticket est valide
        if (!ticket.isValid()) {
            logAccess(ticket.getUser(), ticket, gateDeviceId, AccessLog.AccessType.TICKET, 
                     AccessLog.AccessResult.DENIED, "Ticket invalide ou déjà utilisé", null);
            return new AccessResult(false, "Ticket invalide ou déjà utilisé");
        }

        // Si on a un matchEventId attendu (format complet), vérifier la correspondance
        if (expectedMatchEventId != null && ticket.getMatchEvent() != null) {
            if (ticket.getMatchEvent().getId() != expectedMatchEventId) {
                logAccess(ticket.getUser(), ticket, gateDeviceId, AccessLog.AccessType.TICKET, 
                         AccessLog.AccessResult.DENIED, "Ticket ne correspond pas au match", null);
                return new AccessResult(false, "Ticket ne correspond pas au match");
            }
        }

        // Autoriser l'accès
        ticketService.validateTicket(ticket.getTicketCode());
        logAccess(ticket.getUser(), ticket, gateDeviceId, AccessLog.AccessType.TICKET, 
                 AccessLog.AccessResult.GRANTED, null, null);
        
        return new AccessResult(true, "Accès autorisé");
    }

    /**
     * Enregistre un log d'accès
     */
    private void logAccess(ma.mondial2030.model.User user, Ticket ticket, int gateDeviceId, 
                          AccessLog.AccessType accessType, AccessLog.AccessResult result, 
                          String denialReason, String ipAddress) {
        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setTicket(ticket);
        
        if (gateDeviceId > 0) {
            GateDevice device = gateDeviceDAO.findById(gateDeviceId);
            log.setGateDevice(device);
        }
        
        log.setAccessType(accessType);
        log.setAccessResult(result);
        log.setDenialReason(denialReason);
        log.setIpAddress(ipAddress);
        
        accessLogDAO.create(log);
    }

    /**
     * Récupère tous les logs d'accès
     */
    public List<AccessLog> getAllAccessLogs() {
        return accessLogDAO.findAll();
    }

    /**
     * Classe pour représenter le résultat d'une tentative d'accès
     */
    public static class AccessResult {
        private final boolean granted;
        private final String message;

        public AccessResult(boolean granted, String message) {
            this.granted = granted;
            this.message = message;
        }

        public boolean isGranted() {
            return granted;
        }

        public String getMessage() {
            return message;
        }
    }
}
