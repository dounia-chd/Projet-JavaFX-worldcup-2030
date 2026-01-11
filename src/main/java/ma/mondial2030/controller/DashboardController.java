package ma.mondial2030.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.model.User;
import ma.mondial2030.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour le tableau de bord principal
 */
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Button ticketsButton;
    
    @FXML
    private Button scanButton;
    
    @FXML
    private Button adminButton;
    
    @FXML
    private Button biometricButton;
    
    @FXML
    private Button logoutButton;

    private User currentUser;

    @FXML
    private void initialize() {
        // Les boutons seront configurés selon le rôle dans setUser
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            welcomeLabel.setText("Bienvenue, " + user.getFullName() + " !");
            roleLabel.setText("Rôle: " + user.getRole().getName());
            
            // Afficher/masquer les boutons selon le rôle
            boolean isStaff = AuthenticationService.isStaff();
            boolean isSupporter = AuthenticationService.isSupporter();
            
            ticketsButton.setVisible(true);
            // STAFF : scan QR Code + gestion tickets/annonces
            scanButton.setVisible(isStaff);
            adminButton.setVisible(isStaff);
            // SUPPORTER : scan QR Code + biométrie (QR Code, empreinte faciale, contrôle d'accès)
            if (isSupporter) {
                scanButton.setVisible(true);
            }
            biometricButton.setVisible(isSupporter);
        }
    }

    @FXML
    private void handleTickets() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/tickets.fxml"));
            Parent root = loader.load();
            
            TicketsController controller = loader.getController();
            controller.setUser(currentUser);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) ticketsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mes Tickets - Mondial 2030");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'écran des tickets", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger l'écran des tickets: " + e.getMessage());
        }
    }

    @FXML
    private void handleScan() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/scan.fxml"));
            Parent root = loader.load();
            
            ScanController controller = loader.getController();
            controller.setUser(currentUser);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) scanButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Scan QR Code - Mondial 2030");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'écran de scan", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger l'écran de scan: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/admin.fxml"));
            Parent root = loader.load();
            
            AdminController controller = loader.getController();
            controller.setUser(currentUser);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) adminButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion Tickets & Annonces - Mondial 2030");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'écran d'administration", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger l'écran d'administration: " + e.getMessage());
        }
    }

    @FXML
    private void handleBiometric() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/biometric.fxml"));
            Parent root = loader.load();
            
            BiometricController controller = loader.getController();
            controller.setUser(currentUser);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) biometricButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Biométrie & QR Code - Mondial 2030");
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'écran biométrique", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger l'écran biométrique: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        AuthenticationService authService = new AuthenticationService();
        authService.logout();
        
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion - Mondial 2030");
        } catch (IOException e) {
            logger.error("Erreur lors de la déconnexion", e);
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
