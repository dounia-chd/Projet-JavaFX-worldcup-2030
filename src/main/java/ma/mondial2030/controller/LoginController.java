package ma.mondial2030.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.exception.AuthenticationException;
import ma.mondial2030.model.User;
import ma.mondial2030.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour l'écran de connexion
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;

    private AuthenticationService authService = new AuthenticationService();

    @FXML
    private void initialize() {
        // Focus sur le champ username au démarrage
        usernameField.requestFocus();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", 
                     "Veuillez remplir tous les champs");
            return;
        }

        try {
            User user = authService.login(username, password);
            if (user != null) {
                navigateToDashboard(user);
            }
        } catch (AuthenticationException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'authentification", e.getMessage());
            passwordField.clear();
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors de la connexion");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inscription - Mondial 2030");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de l'écran d'inscription", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger l'écran d'inscription");
        }
    }

    private void navigateToDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setUser(user);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de bord - Mondial 2030");
            stage.centerOnScreen();
        } catch (IOException e) {
            logger.error("Erreur lors de la navigation vers le dashboard", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger le tableau de bord");
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
