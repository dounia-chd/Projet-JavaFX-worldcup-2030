package ma.mondial2030.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données MySQL
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mondial2030_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Connexion à la base de données établie avec succès");
        } catch (ClassNotFoundException e) {
            logger.error("Driver MySQL non trouvé", e);
            throw new RuntimeException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            logger.error("Erreur de connexion à la base de données", e);
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    /**
     * Obtient l'instance singleton de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtient la connexion à la base de données
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération de la connexion", e);
            throw new RuntimeException("Erreur lors de la récupération de la connexion", e);
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la fermeture de la connexion", e);
        }
    }
}
