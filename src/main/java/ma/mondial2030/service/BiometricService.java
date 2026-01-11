package ma.mondial2030.service;

import javafx.scene.image.Image;
import ma.mondial2030.dao.BiometricDataDAO;
import ma.mondial2030.dao.TicketDAO;
import ma.mondial2030.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service pour la gestion biométrique
 */
public class BiometricService {
    private static final Logger logger = LoggerFactory.getLogger(BiometricService.class);
    private final BiometricDataDAO biometricDataDAO = new BiometricDataDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    /**
     * Crée une empreinte faciale à partir d'une image
     */
    public boolean createFaceEmbedding(int userId, Image faceImage) {
        try {
            // Simuler la création d'empreinte faciale
            // Dans une vraie application, on utiliserait une bibliothèque comme OpenCV ou Face Recognition
            // Pour l'instant, on simule en convertissant l'image en base64
            
            // Convertir l'image en bytes (simulation)
            byte[] imageBytes = simulateImageToBytes(faceImage);
            
            // Créer l'empreinte faciale (simulation d'un embedding)
            byte[] embedding = simulateFaceEmbedding(imageBytes);
            
            // Sauvegarder dans la base de données
            return biometricDataDAO.saveFaceEmbedding(userId, embedding, null);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'empreinte faciale", e);
            return false;
        }
    }

    /**
     * Vérifie si l'utilisateur a des données biométriques
     */
    public boolean hasBiometricData(int userId) {
        return biometricDataDAO.hasBiometricData(userId);
    }

    /**
     * Assigne la biométrie à un utilisateur
     */
    public boolean assignBiometricToUser(int userId) {
        return biometricDataDAO.assignBiometricToUser(userId);
    }

    /**
     * Génère un QR Code pour l'utilisateur
     */
    public String generateUserQRCode(int userId) {
        // Générer un QR Code unique pour l'utilisateur avec ses données biométriques
        String qrData = String.format("USER:%d:BIOMETRIC:ENABLED:TIMESTAMP:%d", 
                                      userId, System.currentTimeMillis());
        return qrData;
    }

    /**
     * Vérifie si l'utilisateur a un ticket valide
     */
    public boolean hasValidTicket(int userId) {
        var tickets = ticketDAO.findByUserId(userId);
        return tickets.stream().anyMatch(ticket -> ticket.isValid());
    }

    /**
     * Simule la conversion d'une image en bytes
     */
    private byte[] simulateImageToBytes(Image image) {
        // Dans une vraie application, on utiliserait ImageIO ou une autre bibliothèque
        // Pour l'instant, on simule
        return new byte[1024]; // Simulation
    }

    /**
     * Simule la création d'un embedding facial
     */
    private byte[] simulateFaceEmbedding(byte[] imageBytes) {
        // Dans une vraie application, on utiliserait une bibliothèque de reconnaissance faciale
        // Pour l'instant, on simule un embedding de 128 dimensions (comme FaceNet)
        return new byte[128 * 4]; // 128 floats = 512 bytes
    }
}
