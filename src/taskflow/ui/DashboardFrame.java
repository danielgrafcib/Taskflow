package taskflow.ui;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import taskflow.DatabaseConfig;
import taskflow.ui.entities.TaskFlowEntities.User;
import taskflow.ui.entities.TaskFlowEntities.Project;

public class DashboardFrame extends JFrame {
    private JLabel welcomeLabel;
    private JTable projectTable;
    private JProgressBar globalProgress;
    private JLabel progressLabel;
    private DefaultTableModel tableModel;
    private final User currentUser;
    
    public DashboardFrame(User user) {
        this.currentUser = user;
        FlatLightLaf.setup();
        initializeUI();
        setupProjectTable();
        loadProjects();
        updateProgress();
    }
    
    private void initializeUI() {
        setTitle("TaskFlow - Tableau de bord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][][][grow][]"));
        
        // En-tête avec message de bienvenue
        welcomeLabel = new JLabel("Bienvenue, " + currentUser.getNom());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(welcomeLabel, "wrap");
        
        // Barre de progression globale
        JPanel progressPanel = new JPanel(new MigLayout("insets 0", "[][grow][]"));
        progressPanel.add(new JLabel("Progression Globale :"));
        globalProgress = new JProgressBar(0, 100);
        progressPanel.add(globalProgress, "grow");
        progressLabel = new JLabel("0%");
        progressPanel.add(progressLabel);
        mainPanel.add(progressPanel, "grow, wrap");
        
        // Table des projets
        JPanel projectPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        projectPanel.add(new JLabel("Mes Projets"), "wrap");
        projectTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(projectTable);
        projectPanel.add(scrollPane, "grow");
        mainPanel.add(projectPanel, "grow, wrap");
        
        // Boutons de navigation
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow][grow][grow]"));
        buttonPanel.add(createNavigationButton("Nouveau Projet", this::handleCreateProject), "grow");
        buttonPanel.add(createNavigationButton("Tâches", this::handleViewTasks), "grow");
        buttonPanel.add(createNavigationButton("KPIs", this::handleViewKPIs), "grow");
        buttonPanel.add(createNavigationButton("Historique", this::handleViewHistory), "grow");
        mainPanel.add(buttonPanel, "grow");
        
        add(mainPanel);
        setLocationRelativeTo(null);
    }
    
    private JButton createNavigationButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> action.run());
        return button;
    }
    
    private void setupProjectTable() {
        tableModel = new DefaultTableModel(new String[]{"Nom", "Statut"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        projectTable.setModel(tableModel);
    }
    
    private void loadProjects() {
        // TODO: Implémenter le chargement des projets depuis la base de données
    }
    
    private void updateProgress() {
        // TODO: Implémenter le calcul de la progression globale
    }
    
    private void handleCreateProject() {
        // TODO: Implémenter la création d'un nouveau projet
    }
    
    private void handleViewTasks() {
        // TODO: Implémenter la vue des tâches
    }
    
    private void handleViewKPIs() {
        // TODO: Implémenter la vue des KPIs
    }
    
    private void handleViewHistory() {
        // TODO: Implémenter la vue de l'historique
    }
}