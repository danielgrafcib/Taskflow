package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.block.BlockBorder;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;
import taskflow.models.User;
import taskflow.utils.DatabaseManager;

public class KPIPanel extends JPanel {
    private final User currentUser;
    private final DatabaseManager dbManager;
    
    public KPIPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.dbManager.setParentComponent(this);
        initializeUI();
        loadKPIs();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, wrap 2, insets 20", "[grow][grow]", "[pref][grow][grow]"));
        setBackground(new Color(245, 247, 250));
        
        // En-tête avec style moderne
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 0"));
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Indicateurs de Performance (KPIs)");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        headerPanel.add(titleLabel, "gapleft 10");
        
        add(headerPanel, "span 2, growx, wrap 20");
    }
    
    private JPanel createStyledPanel(Component content, String title) {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow,fill]", "[pref][grow,fill]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(15, 20, 20, 20)
        ));
        
        // Titre du panel
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        titleLabel.setForeground(new Color(30, 41, 59));
        panel.add(titleLabel, "wrap");
        
        // Contenu
        panel.add(content, "grow");
        
        return panel;
    }
    
    private JPanel createMetricPanel(String label, String value, Color accentColor, String icon) {
        JPanel panel = new JPanel(new MigLayout("wrap, insets 15", "[grow]", "[]0[]5[]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        
        // Icône
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        panel.add(iconLabel);
        
        // Valeur
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        panel.add(valueLabel);
        
        // Label
        JLabel metricLabel = new JLabel(label);
        metricLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        metricLabel.setForeground(new Color(71, 85, 105));
        panel.add(metricLabel);
        
        return panel;
    }
    
    private void loadKPIs() {
        try {
            System.out.println("Début du chargement des KPIs...");
            
            // Graphique de répartition des tâches par statut
            DefaultPieDataset taskStatusDataset = new DefaultPieDataset();
            Map<String, Integer> taskStatusStats = dbManager.getTaskStatusStatistics();
            System.out.println("Statistiques des tâches récupérées : " + taskStatusStats);
            
            if (taskStatusStats.isEmpty()) {
                System.out.println("Aucune donnée de tâche trouvée, ajout de données par défaut");
                taskStatusDataset.setValue("À faire", 1);
                taskStatusDataset.setValue("En cours", 1);
                taskStatusDataset.setValue("Terminée", 1);
            } else {
                for (Map.Entry<String, Integer> entry : taskStatusStats.entrySet()) {
                    taskStatusDataset.setValue(entry.getKey(), entry.getValue());
                }
            }
            
            JFreeChart taskStatusChart = ChartFactory.createPieChart(
                "Répartition des Tâches par Statut",
                taskStatusDataset,
                true,
                true,
                false
            );
            
            // Personnalisation du graphique
            taskStatusChart.setBackgroundPaint(Color.WHITE);
            taskStatusChart.getPlot().setBackgroundPaint(Color.WHITE);
            taskStatusChart.getTitle().setFont(new Font("Roboto", Font.BOLD, 16));
            taskStatusChart.getLegend().setFrame(BlockBorder.NONE);
            
            ChartPanel taskStatusPanel = new ChartPanel(taskStatusChart);
            taskStatusPanel.setMinimumDrawWidth(0);
            taskStatusPanel.setMinimumDrawHeight(0);
            taskStatusPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
            taskStatusPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
            taskStatusPanel.setPreferredSize(new Dimension(400, 300));
            add(createStyledPanel(taskStatusPanel, "Répartition des Tâches"), "grow");
            
            System.out.println("Graphique des tâches créé avec succès");
            
            // Graphique des tâches par utilisateur
            DefaultCategoryDataset userTasksDataset = new DefaultCategoryDataset();
            Map<String, Integer> userTaskStats = dbManager.getUserTaskStatistics();
            System.out.println("Statistiques des utilisateurs récupérées : " + userTaskStats);
            
            if (userTaskStats.isEmpty()) {
                System.out.println("Aucune donnée utilisateur trouvée, ajout de données par défaut");
                userTasksDataset.addValue(1, "Tâches", "Utilisateur 1");
                userTasksDataset.addValue(1, "Tâches", "Utilisateur 2");
            } else {
                for (Map.Entry<String, Integer> entry : userTaskStats.entrySet()) {
                    userTasksDataset.addValue(entry.getValue(), "Tâches", entry.getKey());
                }
            }
            
            JFreeChart userTasksChart = ChartFactory.createBarChart(
                "Tâches par Utilisateur",
                "Utilisateur",
                "Nombre de Tâches",
                userTasksDataset
            );
            
            userTasksChart.setBackgroundPaint(Color.WHITE);
            userTasksChart.getPlot().setBackgroundPaint(Color.WHITE);
            userTasksChart.getTitle().setFont(new Font("Roboto", Font.BOLD, 16));
            userTasksChart.getLegend().setFrame(BlockBorder.NONE);
            
            ChartPanel userTasksPanel = new ChartPanel(userTasksChart);
            userTasksPanel.setMinimumDrawWidth(0);
            userTasksPanel.setMinimumDrawHeight(0);
            userTasksPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
            userTasksPanel.setMaximumDrawHeight(Integer.MAX_VALUE);
            userTasksPanel.setPreferredSize(new Dimension(400, 300));
            add(createStyledPanel(userTasksPanel, "Tâches par Utilisateur"), "grow");
            
            System.out.println("Graphique des utilisateurs créé avec succès");
            
            // Panel des statistiques globales
            JPanel globalStatsPanel = new JPanel(new MigLayout("wrap 2, fill, insets 20", "[grow][grow]", "[][]"));
            globalStatsPanel.setBackground(Color.WHITE);
            
            // Taux d'achèvement global
            int completionRate = dbManager.getGlobalCompletionRate();
            JPanel completionPanel = createMetricPanel(
                "Taux d'achèvement",
                completionRate + "%",
                new Color(34, 197, 94),
                "📈"
            );
            globalStatsPanel.add(completionPanel, "grow");
            
            // Nombre total de tâches
            int totalTasks = dbManager.getTotalTasksCount();
            JPanel totalTasksPanel = createMetricPanel(
                "Total des tâches",
                String.valueOf(totalTasks),
                new Color(59, 130, 246),
                "📋"
            );
            globalStatsPanel.add(totalTasksPanel, "grow");
            
            // Tâches en retard
            int lateTasks = dbManager.getLateTasksCount();
            JPanel lateTasksPanel = createMetricPanel(
                "Tâches en retard",
                String.valueOf(lateTasks),
                new Color(239, 68, 68),
                "⚠️"
            );
            globalStatsPanel.add(lateTasksPanel, "grow");
            
            add(createStyledPanel(globalStatsPanel, "Statistiques Globales"), "grow");
            
            // Panel des performances par projet
            JPanel projectStatsPanel = new JPanel(new MigLayout("fill, insets 0"));
            projectStatsPanel.setBackground(Color.WHITE);
            
            // Table des statistiques par projet
            String[] columns = {"Projet", "Total", "Complétées", "En retard"};
            Object[][] data = dbManager.getProjectStatistics();
            
            // Créer un modèle de tableau personnalisé avec le nombre exact de colonnes
            JTable projectStatsTable = new JTable(data, columns) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    if (!isRowSelected(row)) {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                    }
                    return c;
                }
            };
            
            // Personnalisation de la table
            projectStatsTable.setFont(new Font("Roboto", Font.PLAIN, 13));
            projectStatsTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 13));
            projectStatsTable.getTableHeader().setBackground(new Color(243, 244, 246));
            projectStatsTable.setRowHeight(35);
            projectStatsTable.setShowGrid(false);
            projectStatsTable.setIntercellSpacing(new Dimension(0, 0));
            
            JScrollPane scrollPane = new JScrollPane(projectStatsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
            
            projectStatsPanel.add(scrollPane, "grow");
            
            add(createStyledPanel(projectStatsPanel, "Performance par Projet"), "grow");
            
        } catch (SQLException ex) {
            System.err.println("Erreur SQL lors du chargement des KPIs : " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des KPIs : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            System.err.println("Erreur inattendue lors du chargement des KPIs : " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue lors du chargement des KPIs : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Forcer le rafraîchissement du panel
        revalidate();
        repaint();
    }
}