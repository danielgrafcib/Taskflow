package taskflow.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/taskflow?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Tentative de connexion à la base de données...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion établie avec succès !");
            } catch (ClassNotFoundException e) {
                System.err.println("Erreur : Driver MySQL introuvable");
                System.err.println("Message : " + e.getMessage());
                throw new SQLException("Driver MySQL introuvable", e);
            } catch (SQLException e) {
                System.err.println("Erreur de connexion à la base de données");
                System.err.println("URL : " + URL);
                System.err.println("Utilisateur : " + USER);
                System.err.println("Message : " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connexion fermée avec succès");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion");
                System.err.println("Message : " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}