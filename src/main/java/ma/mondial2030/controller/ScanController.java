package ma.mondial2030.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.model.User;
import ma.mondial2030.service.AccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour le scan de QR Code
 */
public class ScanController {
    private static final Logger logger = LoggerFactory.getLogger(ScanController.class);
    
    @FXML
    private TextField qrCodeField;
    
    @FXML
    private Button scanButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label resultLabel;

    private User currentUser;
    private AccessControlService accessControlService = new AccessControlService();
    private static final int DEFAULT_GATE_DEVICE_ID = 1; // À configurer selon le dispositif

    @FXML
    private void initialize() {
        resultLabel.setText("");
        qrCodeField.requestFocus();
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void handleScan() {
        String qrCodeData = qrCodeField.getText().trim();

        if (qrCodeData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", 
                     "Veuillez saisir ou scanner un QR Code");
            return;
        }

        // Traiter l'accès
        AccessControlService.AccessResult result = 
            accessControlService.processQRCodeAccess(qrCodeData, DEFAULT_GATE_DEVICE_ID);

        if (result.isGranted()) {
            resultLabel.setText("✓ ACCÈS AUTORISÉ");
            resultLabel.setTextFill(Color.GREEN);
            showAlert(Alert.AlertType.INFORMATION, "Accès autorisé", result.getMessage());
        } else {
            resultLabel.setText("✗ ACCÈS REFUSÉ");
            resultLabel.setTextFill(Color.RED);
            showAlert(Alert.AlertType.ERROR, "Accès refusé", result.getMessage());
        }

        // Effacer le champ pour le prochain scan
        qrCodeField.clear();
        qrCodeField.requestFocus();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de bord - Mondial 2030");
        } catch (IOException e) {
            logger.error("Erreur lors du retour au dashboard", e);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
