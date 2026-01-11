package ma.mondial2030.util;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Lecteur de QR Code (simulation pour l'instant)
 */
public class QRCodeReader {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeReader.class);

    /**
     * Lit un QR Code depuis une image
     */
    public static String readQRCode(Image image) {
        try {
            BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(image, null);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            logger.warn("QR Code non trouvé dans l'image");
            return null;
        } catch (Exception e) {
            logger.error("Erreur lors de la lecture du QR Code", e);
            return null;
        }
    }

    /**
     * Parse les données d'un QR Code de ticket
     */
    public static TicketQRData parseTicketQRData(String qrData) {
        if (qrData == null || !qrData.startsWith("TICKET:")) {
            return null;
        }

        try {
            String[] parts = qrData.split(":");
            if (parts.length >= 6) {
                String ticketCode = parts[1];
                int userId = Integer.parseInt(parts[3]);
                int matchEventId = Integer.parseInt(parts[5]);
                return new TicketQRData(ticketCode, userId, matchEventId);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du parsing des données QR Code", e);
        }
        return null;
    }

    /**
     * Classe pour stocker les données parsées d'un QR Code de ticket
     */
    public static class TicketQRData {
        private String ticketCode;
        private int userId;
        private int matchEventId;

        public TicketQRData(String ticketCode, int userId, int matchEventId) {
            this.ticketCode = ticketCode;
            this.userId = userId;
            this.matchEventId = matchEventId;
        }

        public String getTicketCode() {
            return ticketCode;
        }

        public int getUserId() {
            return userId;
        }

        public int getMatchEventId() {
            return matchEventId;
        }
    }
}
