package ma.mondial2030.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.model.AccessLog;
import ma.mondial2030.model.MatchEvent;
import ma.mondial2030.model.User;
import ma.mondial2030.service.AccessControlService;
import ma.mondial2030.service.MatchEventService;
import ma.mondial2030.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Contrôleur pour l'administration
 */
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private TableView<User> usersTable;
    
    @FXML
    private TableColumn<User, String> usernameColumn;
    
    @FXML
    private TableColumn<User, String> nameColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, String> roleColumn;
    
    @FXML
    private TableView<AccessLog> logsTable;
    
    @FXML
    private TableColumn<AccessLog, String> logUserColumn;
    
    @FXML
    private TableColumn<AccessLog, String> logTypeColumn;
    
    @FXML
    private TableColumn<AccessLog, String> logResultColumn;
    
    @FXML
    private TableColumn<AccessLog, String> logTimestampColumn;
    
    @FXML
    private TableView<MatchEvent> matchesTable;
    
    @FXML
    private TableColumn<MatchEvent, String> matchNameColumn;
    
    @FXML
    private TableColumn<MatchEvent, String> matchDateColumn;
    
    @FXML
    private TableColumn<MatchEvent, String> teamsColumn;
    
    @FXML
    private TableColumn<MatchEvent, String> venueColumn;
    
    @FXML
    private TableColumn<MatchEvent, String> availableTicketsColumn;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button addMatchButton;

    private User currentUser;
    private UserService userService = new UserService();
    private AccessControlService accessControlService = new AccessControlService();
    private MatchEventService matchEventService = new MatchEventService();
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private ObservableList<AccessLog> logsList = FXCollections.observableArrayList();
    private ObservableList<MatchEvent> matchesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configuration des colonnes utilisateurs
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getFullName());
        });
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                user.getRole() != null ? user.getRole().getName() : "N/A"
            );
        });

        usersTable.setItems(usersList);

        // Configuration des colonnes logs
        logUserColumn.setCellValueFactory(cellData -> {
            AccessLog log = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                log.getUser() != null ? log.getUser().getUsername() : "N/A"
            );
        });
        logTypeColumn.setCellValueFactory(cellData -> {
            AccessLog log = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                log.getAccessType() != null ? log.getAccessType().name() : "N/A"
            );
        });
        logResultColumn.setCellValueFactory(cellData -> {
            AccessLog log = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                log.getAccessResult() != null ? log.getAccessResult().name() : "N/A"
            );
        });
        logTimestampColumn.setCellValueFactory(cellData -> {
            AccessLog log = cellData.getValue();
            if (log.getAccessTimestamp() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    log.getAccessTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        logsTable.setItems(logsList);

        // Configuration des colonnes matchs
        matchNameColumn.setCellValueFactory(new PropertyValueFactory<>("matchName"));
        matchDateColumn.setCellValueFactory(cellData -> {
            MatchEvent match = cellData.getValue();
            if (match.getMatchDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    match.getMatchDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        teamsColumn.setCellValueFactory(cellData -> {
            MatchEvent match = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(match.getMatchDisplay());
        });
        venueColumn.setCellValueFactory(new PropertyValueFactory<>("venue"));
        availableTicketsColumn.setCellValueFactory(cellData -> {
            MatchEvent match = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                match.getAvailableTickets() + " / " + match.getTotalCapacity()
            );
        });

        matchesTable.setItems(matchesList);
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadUsers();
        loadAccessLogs();
        loadMatches();
    }

    private void loadUsers() {
        usersList.clear();
        usersList.addAll(userService.getAllUsers());
    }

    private void loadAccessLogs() {
        logsList.clear();
        logsList.addAll(accessControlService.getAllAccessLogs());
    }

    private void loadMatches() {
        matchesList.clear();
        matchesList.addAll(matchEventService.getAllMatches());
    }

    @FXML
    private void handleAddMatch() {
        // Créer un dialogue pour saisir les informations du match
        Dialog<MatchEvent> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un match");
        dialog.setHeaderText("Saisissez les informations du match");

        // Créer les champs du formulaire
        TextField matchNameField = new TextField();
        matchNameField.setPromptText("Nom du match");
        
        DatePicker matchDatePicker = new DatePicker();
        matchDatePicker.setPromptText("Date du match");
        
        TextField matchTimeField = new TextField();
        matchTimeField.setPromptText("Heure (HH:mm)");
        
        TextField venueField = new TextField();
        venueField.setPromptText("Lieu");
        
        TextField teamAField = new TextField();
        teamAField.setPromptText("Équipe A");
        
        TextField teamBField = new TextField();
        teamBField.setPromptText("Équipe B");
        
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacité totale");
        
        TextField priceField = new TextField();
        priceField.setPromptText("Prix du ticket");

        // Organiser les champs dans une grille
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Nom du match:"), 0, 0);
        grid.add(matchNameField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(matchDatePicker, 1, 1);
        grid.add(new Label("Heure (HH:mm):"), 0, 2);
        grid.add(matchTimeField, 1, 2);
        grid.add(new Label("Lieu:"), 0, 3);
        grid.add(venueField, 1, 3);
        grid.add(new Label("Équipe A:"), 0, 4);
        grid.add(teamAField, 1, 4);
        grid.add(new Label("Équipe B:"), 0, 5);
        grid.add(teamBField, 1, 5);
        grid.add(new Label("Capacité:"), 0, 6);
        grid.add(capacityField, 1, 6);
        grid.add(new Label("Prix:"), 0, 7);
        grid.add(priceField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        // Ajouter les boutons
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Convertir le résultat en MatchEvent
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    LocalDate date = matchDatePicker.getValue();
                    LocalTime time = LocalTime.parse(matchTimeField.getText());
                    LocalDateTime matchDateTime = LocalDateTime.of(date, time);
                    
                    MatchEvent match = new MatchEvent();
                    match.setMatchName(matchNameField.getText());
                    match.setMatchDate(matchDateTime);
                    match.setVenue(venueField.getText());
                    match.setTeamA(teamAField.getText());
                    match.setTeamB(teamBField.getText());
                    match.setTotalCapacity(Integer.parseInt(capacityField.getText()));
                    match.setAvailableTickets(Integer.parseInt(capacityField.getText()));
                    match.setTicketPrice(new BigDecimal(priceField.getText()));
                    match.setStatus(MatchEvent.Status.UPCOMING);
                    
                    return match;
                } catch (Exception e) {
                    logger.error("Erreur lors de la création du match", e);
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                             "Veuillez remplir tous les champs correctement");
                    return null;
                }
            }
            return null;
        });

        // Afficher le dialogue et traiter le résultat
        dialog.showAndWait().ifPresent(match -> {
            if (matchEventService.createMatch(match)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "Match créé avec succès");
                loadMatches();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Impossible de créer le match");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}
