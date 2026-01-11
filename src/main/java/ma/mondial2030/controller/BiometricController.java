package ma.mondial2030.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import ma.mondial2030.MainApp;
import ma.mondial2030.model.Ticket;
import ma.mondial2030.model.User;
import ma.mondial2030.service.BiometricService;
import ma.mondial2030.service.TicketService;
import ma.mondial2030.util.QRCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Contrôleur pour la gestion biométrique (SUPPORTER)
 */
public class BiometricController {
    private static final Logger logger = LoggerFactory.getLogger(BiometricController.class);
    
    @FXML
    private ImageView faceImageView;
    
    @FXML
    private ImageView qrCodeImageView;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button uploadImageButton;
    
    @FXML
    private Button generateFacePrintButton;
    
    @FXML
    private Button assignBiometricButton;
    
    @FXML
    private Button generateQRButton;
    
    @FXML
    private Button verifyAccessButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button backButtonTop;

    private User currentUser;
    private BiometricService biometricService = new BiometricService();
    private TicketService ticketService = new TicketService();
    private Image uploadedFaceImage;
    private String qrCodeData;

    @FXML
    private void initialize() {
        statusLabel.setText("Prêt");
        updateButtons();
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateButtons();
    }

    private void updateButtons() {
        if (currentUser != null) {
            // Vérifier si l'utilisateur a déjà une empreinte faciale
            boolean hasBiometric = biometricService.hasBiometricData(currentUser.getId());
            assignBiometricButton.setDisable(hasBiometric);
            generateFacePrintButton.setDisable(uploadedFaceImage == null);
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        File selectedFile = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                uploadedFaceImage = image;
                faceImageView.setImage(image);
                statusLabel.setText("Image chargée avec succès");
                generateFacePrintButton.setDisable(false);
                logger.info("Image chargée pour l'utilisateur: {}", currentUser.getUsername());
            } catch (Exception e) {
                logger.error("Erreur lors du chargement de l'image", e);
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Impossible de charger l'image");
            }
        }
    }

    @FXML
    private void handleGenerateFacePrint() {
        if (uploadedFaceImage == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune image", 
                     "Veuillez d'abord charger une image");
            return;
        }

        try {
            // Simuler la création d'empreinte faciale
            boolean success = biometricService.createFaceEmbedding(currentUser.getId(), uploadedFaceImage);
            
            if (success) {
                statusLabel.setText("✓ Empreinte faciale créée avec succès");
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "Empreinte faciale créée et enregistrée");
                updateButtons();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Impossible de créer l'empreinte faciale");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'empreinte faciale", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors de la création de l'empreinte");
        }
    }

    @FXML
    private void handleAssignBiometric() {
        if (uploadedFaceImage == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune image", 
                     "Veuillez d'abord charger et créer une empreinte faciale");
            return;
        }

        try {
            boolean success = biometricService.assignBiometricToUser(currentUser.getId());
            if (success) {
                statusLabel.setText("✓ Biométrie assignée avec succès");
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                         "Biométrie assignée à votre compte");
                updateButtons();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                         "Impossible d'assigner la biométrie");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'assignation de la biométrie", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue");
        }
    }

    @FXML
    private void handleGenerateQR() {
        if (currentUser == null) {
            return;
        }

        try {
            // Générer un QR Code pour l'utilisateur
            qrCodeData = biometricService.generateUserQRCode(currentUser.getId());
            Image qrImage = QRCodeGenerator.generateQRCode(qrCodeData);
            qrCodeImageView.setImage(qrImage);
            statusLabel.setText("✓ QR Code généré avec succès");
            showAlert(Alert.AlertType.INFORMATION, "QR Code généré", 
                     "Votre QR Code a été généré avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du QR Code", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de générer le QR Code");
        }
    }

    @FXML
    private void handleVerifyAccess() {
        if (currentUser == null) {
            return;
        }

        try {
            // Vérifier l'accès avec ticket + QR Code + biométrie
            boolean hasTicket = biometricService.hasValidTicket(currentUser.getId());
            boolean hasQRCode = qrCodeData != null;
            boolean hasBiometric = biometricService.hasBiometricData(currentUser.getId());

            StringBuilder result = new StringBuilder();
            result.append("Vérification d'accès:\n");
            result.append("✓ Ticket: ").append(hasTicket ? "Valide" : "Aucun ticket valide").append("\n");
            result.append("✓ QR Code: ").append(hasQRCode ? "Généré" : "Non généré").append("\n");
            result.append("✓ Biométrie: ").append(hasBiometric ? "Enregistrée" : "Non enregistrée").append("\n");

            boolean accessGranted = hasTicket && hasQRCode && hasBiometric;
            result.append("\nRésultat: ").append(accessGranted ? "✓ ACCÈS AUTORISÉ" : "✗ ACCÈS REFUSÉ");

            statusLabel.setText(accessGranted ? "✓ Accès autorisé" : "✗ Accès refusé");
            
            if (accessGranted) {
                // Afficher le ticket avec QR code et photo
                showTicketDialog();
            } else {
                Alert.AlertType alertType = Alert.AlertType.WARNING;
                showAlert(alertType, "Contrôle d'accès", result.toString());
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification d'accès", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors de la vérification");
        }
    }

    private void showTicketDialog() {
        try {
            // Récupérer le premier ticket valide de l'utilisateur
            java.util.List<Ticket> tickets = ticketService.getUserTickets(currentUser.getId());
            Ticket validTicket = tickets.stream()
                .filter(Ticket::isValid)
                .findFirst()
                .orElse(null);

            if (validTicket == null) {
                showAlert(Alert.AlertType.WARNING, "Aucun ticket", 
                         "Aucun ticket valide trouvé");
                return;
            }

            // Créer un dialogue pour afficher le ticket
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Ticket d'accès validé");
            dialog.setHeaderText("Votre accès a été validé avec succès");

            // Créer le contenu du dialogue
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
            content.setPadding(new javafx.geometry.Insets(20));

            // Informations du ticket
            Label ticketInfoLabel = new Label();
            ticketInfoLabel.setText(
                "Code: " + validTicket.getTicketCode() + "\n" +
                "Match: " + (validTicket.getMatchEvent() != null ? 
                    validTicket.getMatchEvent().getMatchDisplay() : "N/A") + "\n" +
                "Siège: " + validTicket.getSeatNumber() + "\n" +
                "Utilisateur: " + currentUser.getFullName()
            );
            ticketInfoLabel.setStyle("-fx-font-size: 14px;");

            // Image de la photo
            ImageView photoView = new ImageView();
            if (uploadedFaceImage != null) {
                photoView.setImage(uploadedFaceImage);
            } else {
                photoView.setImage(null);
            }
            photoView.setFitHeight(150);
            photoView.setFitWidth(150);
            photoView.setPreserveRatio(true);
            photoView.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px;");

            // QR Code
            ImageView qrView = new ImageView();
            if (validTicket.getQrCodeData() != null) {
                javafx.scene.image.Image qrImage = QRCodeGenerator.generateQRCode(validTicket.getQrCodeData());
                qrView.setImage(qrImage);
            }
            qrView.setFitHeight(150);
            qrView.setFitWidth(150);
            qrView.setPreserveRatio(true);
            qrView.setStyle("-fx-border-color: #ccc; -fx-border-width: 2px;");

            // Organiser les éléments
            javafx.scene.layout.HBox imagesBox = new javafx.scene.layout.HBox(20);
            imagesBox.setAlignment(javafx.geometry.Pos.CENTER);
            javafx.scene.layout.VBox photoBox = new javafx.scene.layout.VBox(5);
            photoBox.setAlignment(javafx.geometry.Pos.CENTER);
            photoBox.getChildren().addAll(new Label("Photo"), photoView);
            
            javafx.scene.layout.VBox qrBox = new javafx.scene.layout.VBox(5);
            qrBox.setAlignment(javafx.geometry.Pos.CENTER);
            qrBox.getChildren().addAll(new Label("QR Code"), qrView);
            
            imagesBox.getChildren().addAll(photoBox, qrBox);

            content.getChildren().addAll(ticketInfoLabel, imagesBox);

            dialog.getDialogPane().setContent(content);
            
            // Ajouter les boutons OK et Enregistrer
            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.APPLY);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);
            dialog.setResizable(true);

            // Gérer le bouton Enregistrer
            javafx.scene.control.Button saveButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setOnAction(e -> {
                try {
                    saveTicketAsImage(validTicket, photoView, qrView, ticketInfoLabel);
                } catch (Exception ex) {
                    logger.error("Erreur lors de l'enregistrement du ticket", ex);
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                             "Impossible d'enregistrer le ticket");
                }
            });

            dialog.showAndWait();
        } catch (Exception e) {
            logger.error("Erreur lors de l'affichage du ticket", e);
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'afficher le ticket");
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
            Stage stage = null;
            if (backButton != null && backButton.getScene() != null) {
                stage = (Stage) backButton.getScene().getWindow();
            } else if (backButtonTop != null && backButtonTop.getScene() != null) {
                stage = (Stage) backButtonTop.getScene().getWindow();
            } else if (verifyAccessButton != null && verifyAccessButton.getScene() != null) {
                stage = (Stage) verifyAccessButton.getScene().getWindow();
            }
            if (stage != null) {
                stage.setScene(scene);
            }
            stage.setTitle("Tableau de bord - Mondial 2030");
        } catch (IOException e) {
            logger.error("Erreur lors du retour au dashboard", e);
        }
    }

    private void saveTicketAsImage(Ticket ticket, ImageView photoView, ImageView qrView, Label ticketInfoLabel) {
        try {
            // Créer un FileChooser pour choisir l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le ticket");
            fileChooser.setInitialFileName("ticket_" + ticket.getTicketCode() + ".png");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images PNG", "*.png")
            );

            File file = fileChooser.showSaveDialog(backButton.getScene().getWindow());
            if (file == null) {
                return; // L'utilisateur a annulé
            }

            // Créer une scène temporaire pour capturer le ticket
            javafx.scene.layout.VBox ticketContainer = new javafx.scene.layout.VBox(15);
            ticketContainer.setPadding(new javafx.geometry.Insets(30));
            ticketContainer.setStyle("-fx-background-color: white;");

            // Titre
            Label titleLabel = new Label("TICKET D'ACCÈS VALIDÉ");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            // Informations du ticket
            Label infoLabel = new Label(ticketInfoLabel.getText());
            infoLabel.setStyle("-fx-font-size: 14px;");

            // Conteneur pour les images
            javafx.scene.layout.HBox imagesContainer = new javafx.scene.layout.HBox(30);
            imagesContainer.setAlignment(javafx.geometry.Pos.CENTER);

            // Photo
            ImageView savedPhotoView = new ImageView(photoView.getImage());
            savedPhotoView.setFitHeight(200);
            savedPhotoView.setFitWidth(200);
            savedPhotoView.setPreserveRatio(true);
            javafx.scene.layout.VBox photoContainer = new javafx.scene.layout.VBox(5);
            photoContainer.setAlignment(javafx.geometry.Pos.CENTER);
            Label photoLabel = new Label("Photo");
            photoLabel.setStyle("-fx-font-weight: bold;");
            photoContainer.getChildren().addAll(photoLabel, savedPhotoView);

            // QR Code
            ImageView savedQrView = new ImageView(qrView.getImage());
            savedQrView.setFitHeight(200);
            savedQrView.setFitWidth(200);
            savedQrView.setPreserveRatio(true);
            javafx.scene.layout.VBox qrContainer = new javafx.scene.layout.VBox(5);
            qrContainer.setAlignment(javafx.geometry.Pos.CENTER);
            Label qrLabel = new Label("QR Code");
            qrLabel.setStyle("-fx-font-weight: bold;");
            qrContainer.getChildren().addAll(qrLabel, savedQrView);

            imagesContainer.getChildren().addAll(photoContainer, qrContainer);

            ticketContainer.getChildren().addAll(titleLabel, infoLabel, imagesContainer);

            // Créer une scène et capturer l'image
            javafx.scene.Scene scene = new javafx.scene.Scene(ticketContainer);
            scene.setFill(Color.WHITE);
            
            // Prendre un snapshot
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.WHITE);
            WritableImage image = ticketContainer.snapshot(params, null);

            // Sauvegarder l'image
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Ticket enregistré avec succès: " + file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement du ticket", e);
            throw new RuntimeException(e);
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
