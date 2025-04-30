package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import taskflow.models.User;
import taskflow.models.Project;
import taskflow.utils.DatabaseManager;
import taskflow.utils.NotificationManager;

public class ProjectPanel extends JPanel {
    private final User currentUser;
    private final DatabaseManager dbManager;
    private final NotificationManager notifManager;
    private JTable projectTable;
    private DefaultTableModel tableModel;
    
    public ProjectPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.notifManager = NotificationManager.getInstance();
        initializeUI();
        loadProjects();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        
        // En-tête avec titre et bouton d'ajout
        JPanel headerPanel = new JPanel(new MigLayout("fillx", "[][push][]"));
        JLabel titleLabel = new JLabel("Gestion des Projets");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        
        JButton addButton = new JButton("Nouveau Projet");
        addButton.putClientProperty("JButton.buttonType", "roundRect");
        addButton.addActionListener(e -> showAddProjectDialog());
        headerPanel.add(addButton, "right");
        
        add(headerPanel, "cell 0 0, growx");
        
        // Table des projets
        setupProjectTable();
        JScrollPane scrollPane = new JScrollPane(projectTable);
        add(scrollPane, "cell 0 1, grow");
    }
    
    private void setupProjectTable() {
        String[] columns = {"ID", "Nom", "Description", "Date Début", "Date Fin", "Statut", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Seule la colonne Actions est éditable
            }
        };
        
        projectTable = new JTable(tableModel);
        projectTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        projectTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor());
    }
    
    private void loadProjects() {
        tableModel.setRowCount(0);
        try {
            List<Project> projects = dbManager.getAllProjects();
            for (Project project : projects) {
                Object[] row = {
                    project.getId(),
                    project.getNom(),
                    project.getDescription(),
                    project.getDateDebut(),
                    project.getDateFin(),
                    project.getStatut(),
                    "Actions"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            notifManager.showError("Erreur lors du chargement des projets: " + e.getMessage(), "Erreur");
        }
    }
    
    private void showAddProjectDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nouveau Projet", true);
        dialog.setLayout(new MigLayout("fillx, wrap 2", "[][grow]", "[]10[]"));
        
        JTextField nomField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        JSpinner dateDebutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner dateFinSpinner = new JSpinner(new SpinnerDateModel());
        
        dialog.add(new JLabel("Nom:"));
        dialog.add(nomField, "growx");
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(descriptionArea), "growx");
        dialog.add(new JLabel("Date de début:"));
        dialog.add(dateDebutSpinner, "growx");
        dialog.add(new JLabel("Date de fin:"));
        dialog.add(dateFinSpinner, "growx");
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            try {
                Project newProject = new Project();
                newProject.setNom(nomField.getText());
                newProject.setDescription(descriptionArea.getText());
                newProject.setDateDebut((java.util.Date) dateDebutSpinner.getValue());
                newProject.setDateFin((java.util.Date) dateFinSpinner.getValue());
                newProject.setStatut("En cours");
                
                dbManager.createProject(newProject);
                notifManager.showInfo("Projet créé avec succès", "Succès");
                loadProjects();
                dialog.dispose();
            } catch (Exception ex) {
                notifManager.showError("Erreur lors de la création du projet: " + ex.getMessage(), "Erreur");
            }
        });
        
        dialog.add(saveButton, "span 2, align center");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Actions");
            return this;
        }
    }
    
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor() {
            super(new JTextField());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = projectTable.getSelectedRow();
                int projectId = (int) projectTable.getValueAt(row, 0);
                showProjectActions(projectId);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void showProjectActions(int projectId) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Modifier");
        JMenuItem deleteItem = new JMenuItem("Supprimer");
        
        editItem.addActionListener(e -> editProject(projectId));
        deleteItem.addActionListener(e -> deleteProject(projectId));
        
        popup.add(editItem);
        popup.add(deleteItem);
        
        Point p = projectTable.getLocationOnScreen();
        int row = projectTable.getSelectedRow();
        Rectangle r = projectTable.getCellRect(row, 6, true);
        popup.show(projectTable, r.x, r.y + r.height);
    }
    
    private void editProject(int projectId) {
        try {
            Project project = dbManager.getProjectById(projectId);
            if (project != null) {
                // Implémenter la logique de modification
                showEditProjectDialog(project);
            }
        } catch (Exception e) {
            notifManager.showError("Erreur lors de la récupération du projet: " + e.getMessage(), "Erreur");
        }
    }
    
    private void showEditProjectDialog(Project project) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modifier Projet", true);
        dialog.setLayout(new MigLayout("fillx, wrap 2", "[][grow]", "[]10[]"));
        
        JTextField nomField = new JTextField(project.getNom(), 20);
        JTextArea descriptionArea = new JTextArea(project.getDescription(), 3, 20);
        JSpinner dateDebutSpinner = new JSpinner(new SpinnerDateModel(project.getDateDebut(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner dateFinSpinner = new JSpinner(new SpinnerDateModel(project.getDateFin(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"En cours", "Terminé", "En pause"});
        statutCombo.setSelectedItem(project.getStatut());
        
        dialog.add(new JLabel("Nom:"));
        dialog.add(nomField, "growx");
        dialog.add(new JLabel("Description:"));
        dialog.add(new JScrollPane(descriptionArea), "growx");
        dialog.add(new JLabel("Date de début:"));
        dialog.add(dateDebutSpinner, "growx");
        dialog.add(new JLabel("Date de fin:"));
        dialog.add(dateFinSpinner, "growx");
        dialog.add(new JLabel("Statut:"));
        dialog.add(statutCombo, "growx");
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            try {
                project.setNom(nomField.getText());
                project.setDescription(descriptionArea.getText());
                project.setDateDebut((java.util.Date) dateDebutSpinner.getValue());
                project.setDateFin((java.util.Date) dateFinSpinner.getValue());
                project.setStatut((String) statutCombo.getSelectedItem());
                
                dbManager.updateProject(project);
                notifManager.showInfo("Projet modifié avec succès", "Succès");
                loadProjects();
                dialog.dispose();
            } catch (Exception ex) {
                notifManager.showError("Erreur lors de la modification du projet: " + ex.getMessage(), "Erreur");
            }
        });
        
        dialog.add(saveButton, "span 2, align center");
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteProject(int projectId) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Êtes-vous sûr de vouloir supprimer ce projet ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dbManager.deleteProject(projectId);
                notifManager.showInfo("Projet supprimé avec succès", "Succès");
                loadProjects();
            } catch (Exception e) {
                notifManager.showError("Erreur lors de la suppression du projet: " + e.getMessage(), "Erreur");
            }
        }
    }
}