package ma.mondial2030.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import ma.mondial2030.MainApp;
import ma.mondial2030.model.MatchEvent;
import ma.mondial2030.model.Ticket;
import ma.mondial2030.model.User;
import ma.mondial2030.service.MatchEventService;
import ma.mondial2030.service.TicketService;
import ma.mondial2030.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Contrôleur pour la gestion des tickets
 */
public class TicketsController {
    private static final Logger logger = LoggerFactory.getLogger(TicketsController.class);
    
    @FXML
    private TableView<Ticket> ticketsTable;
    
    @FXML
    private TableColumn<Ticket, String> ticketCodeColumn;
    
    @FXML
    private TableColumn<Ticket, String> matchColumn;
    
    @FXML
    private TableColumn<Ticket, String> seatColumn;
    
    @FXML
    private TableColumn<Ticket, String> statusColumn;
    
    @FXML
    private ImageView qrCodeImageView;
    
    @FXML
    private Label ticketDetailsLabel;
    
    @FXML
    private ComboBox<MatchEvent> matchComboBox;
    
    @FXML
    private TextField seatNumberField;
    
    @FXML
    private Button purchaseButton;
    
    @FXML
    private Button backButton;

    private User currentUser;
    private TicketService ticketService = new TicketService();
    private MatchEventService matchEventService = new MatchEventService();
    private ObservableList<Ticket> ticketsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configuration des colonnes
        ticketCodeColumn.setCellValueFactory(new PropertyValueFactory<>("ticketCode"));
        matchColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            if (ticket.getMatchEvent() != null) {
                return new javafx.beans.property.SimpleStringProperty(ticket.getMatchEvent().getMatchDisplay());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        seatColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        statusColumn.setCellValueFactory(cellData -> {
            Ticket ticket = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(ticket.getStatus().name());
        });

        ticketsTable.setItems(ticketsList);
        
        // Sélection d'un ticket
        ticketsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showTicketDetails(newValue)
        );
    }

    public void setUser(User user) {
        this.currentUser = user;
        loadUserTickets();
        loadAvailableMatches();
    }

    private void loadUserTickets() {
        if (currentUser != null) {
            ticketsList.clear();
            ticketsList.addAll(ticketService.getUserTickets(currentUser.getId()));
        }
    }

    private void loadAvailableMatches() {
        matchComboBox.getItems().clear();
        // Charger tous les matchs ajoutés par l'admin (sauf ceux annulés)
        java.util.List<MatchEvent> allMatches = matchEventService.getAllMatches();
        // Filtrer pour ne garder que les matchs non annulés et avec des tickets disponibles
        java.util.List<MatchEvent> availableMatches = allMatches.stream()
            .filter(match -> match.getStatus() != MatchEvent.Status.CANCELLED)
            .filter(match -> match.getAvailableTickets() > 0)
            .collect(java.util.stream.Collectors.toList());
        matchComboBox.getItems().addAll(availableMatches);
        
        // Configurer l'affichage dans le ComboBox
        matchComboBox.setCellFactory(param -> new ListCell<MatchEvent>() {
            @Override
            protected void updateItem(MatchEvent match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    setText(match.getMatchDisplay() + " - " + 
                           (match.getMatchDate() != null ? 
                            match.getMatchDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
                }
            }
        });
        
        matchComboBox.setButtonCell(new ListCell<MatchEvent>() {
            @Override
            protected void updateItem(MatchEvent match, boolean empty) {
                super.updateItem(match, empty);
                if (empty || match == null) {
                    setText(null);
                } else {
                    setText(match.getMatchDisplay() + " - " + 
                           (match.getMatchDate() != null ? 
                            match.getMatchDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
                }
            }
        });
    }

    @FXML
    private void handlePurchase() {
        MatchEvent selectedMatch = matchComboBox.getSelectionModel().getSelectedItem();
        String seatNumber = seatNumberField.getText().trim();

        if (selectedMatch == null) {
            showAlert(Alert.AlertType.WARNING, "Sélection requise", 
                     "Veuillez sélectionner un match");
            return;
        }

        if (seatNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ requis", 
                     "Veuillez saisir un numéro de siège");
            return;
        }

        if (selectedMatch.getAvailableTickets() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Tickets épuisés", 
                     "Il n'y a plus de tickets disponibles pour ce match");
            return;
        }

        Ticket newTicket = ticketService.purchaseTicket(currentUser, selectedMatch.getId(), seatNumber);
        if (newTicket != null) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Ticket acheté avec succès !");
            loadUserTickets();
            seatNumberField.clear();
            ticketsTable.getSelectionModel().select(newTicket);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'acheter le ticket");
        }
    }

    private void showTicketDetails(Ticket ticket) {
        if (ticket == null) {
            qrCodeImageView.setImage(null);
            ticketDetailsLabel.setText("");
            return;
        }

        ticketDetailsLabel.setText(
            "Code: " + ticket.getTicketCode() + "\n" +
            "Match: " + (ticket.getMatchEvent() != null ? ticket.getMatchEvent().getMatchDisplay() : "N/A") + "\n" +
            "Siège: " + ticket.getSeatNumber() + "\n" +
            "Statut: " + ticket.getStatus().name() + "\n" +
            "Date d'achat: " + (ticket.getPurchaseDate() != null ? 
                ticket.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A")
        );

        // Générer le QR Code
        if (ticket.getQrCodeData() != null) {
            javafx.scene.image.Image qrImage = QRCodeGenerator.generateQRCode(ticket.getQrCodeData());
            qrCodeImageView.setImage(qrImage);
        } else {
            qrCodeImageView.setImage(null);
        }
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
