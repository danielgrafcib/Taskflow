package taskflow;

import com.formdev.flatlaf.FlatLightLaf;
import taskflow.ui.LoginFrame;
import javax.swing.*;

public class TaskFlowApp {
    public static void main(String[] args) {
        // Appliquer le thème FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Impossible de charger le thème FlatLaf");
        }

        // Lancer l'application dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Créer et afficher la page de connexion
                LoginFrame loginPage = new LoginFrame();
                loginPage.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Erreur lors du démarrage de l'application: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 