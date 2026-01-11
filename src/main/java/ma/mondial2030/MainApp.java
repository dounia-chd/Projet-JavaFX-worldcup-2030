package ma.mondial2030;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application principale JavaFX pour le système de billetterie et accréditation
 * du Mondial 2030
 */
public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Démarrage de l'application Mondial 2030");
            
            // Charger l'écran de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            primaryStage.setTitle("Mondial 2030 - Système de Billetterie");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            logger.info("Application démarrée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors du démarrage de l'application", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        logger.info("Arrêt de l'application");
        // Fermer la connexion à la base de données si nécessaire
        // DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
