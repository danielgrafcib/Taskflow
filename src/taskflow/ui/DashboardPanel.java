package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import taskflow.utils.DatabaseManager;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class DashboardPanel extends JPanel {
    private final DatabaseManager dbManager;

    public DashboardPanel() {
        this.dbManager = DatabaseManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow,fill][grow,fill][grow,fill]", "[100!]"));
        setBackground(new Color(245, 247, 251));

        try {
            // Nombre total de tâches
            int totalTasks = dbManager.getTotalTasksCount();
            FlatSVGIcon chartIcon = new FlatSVGIcon("taskflow/ui/icons/chart-bar.svg");
            chartIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
                @Override
                public Color filter(Color color) {
                    return new Color(63, 81, 181);
                }
            });
            add(createMetricPanel("Total des tâches", String.valueOf(totalTasks), Color.WHITE, chartIcon, "↑ +6.5%", new Color(63, 81, 181)), "growx");

            // Tâches en retard
            int lateTasks = dbManager.getLateTasksCount();
            FlatSVGIcon alertIcon = new FlatSVGIcon("taskflow/ui/icons/alert-triangle.svg");
            alertIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
                @Override
                public Color filter(Color color) {
                    return new Color(244, 67, 54);
                }
            });
            add(createMetricPanel("Tâches en retard", String.valueOf(lateTasks), Color.WHITE, alertIcon, "↓ -0.10%", new Color(244, 67, 54)), "growx");

            // Taux d'achèvement global
            int completionRate = dbManager.getGlobalCompletionRate();
            FlatSVGIcon trendIcon = new FlatSVGIcon("taskflow/ui/icons/trending-up.svg");
            trendIcon.setColorFilter(new FlatSVGIcon.ColorFilter() {
                @Override
                public Color filter(Color color) {
                    return new Color(76, 175, 80);
                }
            });
            add(createMetricPanel("Taux d'achèvement", completionRate + "%", Color.WHITE, trendIcon, "↓ -0.2%", new Color(76, 175, 80)), "growx");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des statistiques : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createMetricPanel(String title, String value, Color bgColor, FlatSVGIcon icon, String change, Color accentColor) {
        JPanel panel = new JPanel(new MigLayout("fill, insets 15", "[grow]", "[][][][]"));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Icône avec fond coloré
        JPanel iconContainer = new JPanel(new MigLayout("center"));
        iconContainer.setBackground(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 30));
        iconContainer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel iconLabel = new JLabel(icon);
        iconContainer.add(iconLabel);
        panel.add(iconContainer, "wrap, left");

        // Titre
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        panel.add(titleLabel, "wrap, gaptop 10");

        // Valeur
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(33, 33, 33));
        panel.add(valueLabel, "wrap, gaptop 5");

        // Indicateur de changement
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        Color changeColor = change.startsWith("↑") ? new Color(76, 175, 80) : new Color(244, 67, 54);
        changeLabel.setForeground(changeColor);
        panel.add(changeLabel, "wrap, gaptop 5");

        return panel;
    }
}