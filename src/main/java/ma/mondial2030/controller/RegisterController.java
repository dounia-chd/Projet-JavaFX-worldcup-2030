package ma.mondial2030.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Contrôleur pour l'inscription
 */
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private javafx.scene.control.PasswordField passwordField;
    
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button backButton;

    private UserService userService = new UserService();

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("STAFF", "SUPPORTER");
        roleComboBox.getSelectionModel().selectFirst();
        // Les utilisateurs peuvent s'inscrire avec le rôle STAFF ou SUPPORTER
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleComboBox.getSelectionModel().getSelectedItem();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || 
            firstName.isEmpty() || lastName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs requis", 
                     "Veuillez remplir tous les champs obligatoires");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Mot de passe faible", 
                     "Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        boolean success = userService.registerUser(username, email, password, 
                                                   firstName, lastName, phone, role);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", 
                     "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter.");
            handleBack();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur d'inscription", 
                     "L'inscription a échoué. Le nom d'utilisateur ou l'email existe peut-être déjà.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion - Mondial 2030");
        } catch (IOException e) {
            logger.error("Erreur lors du retour à l'écran de connexion", e);
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
