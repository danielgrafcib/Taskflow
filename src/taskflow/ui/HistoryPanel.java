package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import taskflow.models.User;
import taskflow.models.Task;
import taskflow.utils.DatabaseManager;
import taskflow.utils.NotificationManager;

public class HistoryPanel extends JPanel {
    private final User currentUser;
    private final DatabaseManager dbManager;
    private final NotificationManager notifManager;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    public HistoryPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.notifManager = NotificationManager.getInstance();
        initializeUI();
        loadHistory();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        
        // En-tête avec titre
        JPanel headerPanel = new JPanel(new MigLayout("fillx", "[]push[]"));
        JLabel titleLabel = new JLabel("Historique des Tâches");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        add(headerPanel, "cell 0 0, growx");
        
        // Table de l'historique
        setupHistoryTable();
        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, "cell 0 1, grow");
    }
    
    private void setupHistoryTable() {
        String[] columns = {"ID", "Projet", "Tâche", "Date de début", "Date de fin", "Statut", "Priorité"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Toutes les cellules sont non-éditables
            }
        };
        
        historyTable = new JTable(tableModel);
        
        // Personnalisation de l'apparence
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.setRowHeight(25);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(Color.LIGHT_GRAY);
        
        // Définir les largeurs de colonnes
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Projet
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Tâche
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date début
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date fin
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Statut
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Priorité
    }
    
    private void loadHistory() {
        tableModel.setRowCount(0);
        try {
            List<Task> tasks = dbManager.getUserTaskHistory(currentUser.getId());
            for (Task task : tasks) {
                Object[] row = {
                    task.getId(),
                    task.getProject().getNom(),
                    task.getNom(),
                    task.getDateDebut(),
                    task.getDateFin(),
                    task.getStatut(),
                    task.getPriorite()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            notifManager.showError("Erreur lors du chargement de l'historique: " + e.getMessage(), "Erreur");
        }
    }
    
    // Méthode pour rafraîchir l'historique
    public void refreshHistory() {
        loadHistory();
    }
}