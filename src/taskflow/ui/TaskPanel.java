package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import taskflow.utils.DatabaseManager;
import taskflow.models.User;
import taskflow.models.Task;
import taskflow.models.Project;

public class TaskPanel extends JPanel {
    private final User currentUser;
    private final DatabaseManager dbManager;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<Project> projectComboBox;
    private JComboBox<String> statusFilter;
    
    public TaskPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.dbManager.setParentComponent(this);
        initializeUI();
        loadProjects();
        loadTasks();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][][grow]"));
        setBackground(Color.WHITE);
        
        // En-tête avec style simplifié
        JPanel headerPanel = new JPanel(new MigLayout("fillx", "[grow]push[]"));
        headerPanel.setBackground(new Color(255, 255, 255));
        JLabel titleLabel = new JLabel("Gestion des Tâches");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, "cell 0 0");
        
        // Bouton standard au lieu d'un bouton avec icône SVG
        JButton createButton = new JButton("Nouvelle Tâche");
        createButton.setBackground(new Color(70, 130, 180));
        createButton.setForeground(Color.WHITE);
        createButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> showTaskDialog(null));
        headerPanel.add(createButton, "cell 1 0");
        
        add(headerPanel, "growx, wrap");
        
        // Filtres
        JPanel filterPanel = new JPanel(new MigLayout("insets 0", "[][grow][][grow]"));
        filterPanel.setBackground(new Color(245, 245, 245));
        
        filterPanel.add(new JLabel("Projet :"));
        projectComboBox = new JComboBox<>();
        projectComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Project) {
                    setText(((Project) value).getNom());
                }
                return this;
            }
        });
        projectComboBox.addActionListener(e -> loadTasks());
        filterPanel.add(projectComboBox, "growx");
        
        filterPanel.add(new JLabel("Statut :"));
        statusFilter = new JComboBox<>(new String[]{"Tous", "A_FAIRE", "EN_COURS", "TERMINEE"});
        statusFilter.addActionListener(e -> loadTasks());
        filterPanel.add(statusFilter, "growx");
        
        add(filterPanel, "growx, wrap");
        
        // Table des tâches
        setupTaskTable();
        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, "grow");
    }
    
    private void setupTaskTable() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Titre", "Description", "Échéance", "Priorité", "Statut", "Projet", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Seule la colonne Actions est éditable
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setBackground(Color.WHITE);
        taskTable.setGridColor(new Color(230, 230, 230));
        taskTable.setSelectionBackground(new Color(135, 206, 250));
        
        // Ne pas accéder au parent de la table ici car il n'est pas encore défini
        
        taskTable.getColumnModel().getColumn(0).setMaxWidth(50);
        taskTable.getColumnModel().getColumn(7).setMinWidth(150);
        
        // Ajouter les boutons d'action dans la dernière colonne
        taskTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(7).setCellEditor(
            new ButtonEditor(new JCheckBox(), this::handleTaskAction)
        );
    }
    
    private void loadProjects() {
        try {
            projectComboBox.removeAllItems();
            projectComboBox.addItem(new Project(-1, "Tous les projets", "", null, null, "EN_COURS", null));
            
            List<Project> projects = dbManager.getAllProjects();
            for (Project project : projects) {
                projectComboBox.addItem(project);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des projets : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTasks() {
        tableModel.setRowCount(0);
        try {
            Project selectedProject = (Project) projectComboBox.getSelectedItem();
            String selectedStatus = statusFilter.getSelectedItem().toString();
            
            List<Task> tasks;
            if (selectedProject != null && selectedProject.getId() != -1) {
                tasks = dbManager.getTasksByProject(selectedProject.getId());
            } else {
                tasks = dbManager.getAllTasks();
            }
            
            for (Task task : tasks) {
                if (selectedStatus.equals("Tous") || task.getStatut().equals(selectedStatus)) {
                    Project project = dbManager.getProjectById(task.getProject().getId());
                    tableModel.addRow(new Object[]{
                        task.getId(),
                        task.getNom(),
                        task.getDescription(),
                        task.getDateFin(),
                        task.getPriorite(),
                        task.getStatut(),
                        project.getNom(),
                        "Actions"
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des tâches : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTaskDialog(Task task) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   task == null ? "Nouvelle Tâche" : "Modifier Tâche",
                                   true);
        dialog.setLayout(new MigLayout("fill, insets 20", "[][grow]", "[][][][][][][][]"));
        
        // Champs du formulaire
        JTextField titreField = new JTextField(task != null ? task.getNom() : "");
        JTextArea descriptionArea = new JTextArea(task != null ? task.getDescription() : "", 3, 20);
        JComboBox<String> prioriteCombo = new JComboBox<>(new String[]{"BASSE", "MOYENNE", "HAUTE"});
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"A_FAIRE", "EN_COURS", "TERMINEE"});
        JComboBox<Project> projetCombo = new JComboBox<>();
        
        try {
            List<Project> projects = dbManager.getAllProjects();
            for (Project project : projects) {
                projetCombo.addItem(project);
                if (task != null && task.getProject().getId() == project.getId()) {
                    projetCombo.setSelectedItem(project);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog,
                "Erreur lors du chargement des projets : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
        
        projetCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Project) {
                    setText(((Project) value).getNom());
                }
                return this;
            }
        });
        
        if (task != null) {
            prioriteCombo.setSelectedItem(task.getPriorite());
            statutCombo.setSelectedItem(task.getStatut());
        }
        
        // Ajout des composants
        dialog.add(new JLabel("Titre :"), "cell 0 0");
        dialog.add(titreField, "cell 1 0, growx, wrap");
        
        dialog.add(new JLabel("Description :"), "cell 0 1");
        dialog.add(new JScrollPane(descriptionArea), "cell 1 1, growx, wrap");
        
        dialog.add(new JLabel("Projet :"), "cell 0 2");
        dialog.add(projetCombo, "cell 1 2, growx, wrap");
        
        dialog.add(new JLabel("Priorité :"), "cell 0 3");
        dialog.add(prioriteCombo, "cell 1 3, growx, wrap");
        
        dialog.add(new JLabel("Statut :"), "cell 0 4");
        dialog.add(statutCombo, "cell 1 4, growx, wrap");
        
        // Boutons
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][][]"));
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.addActionListener(e -> {
            try {
                Task newTask = new Task();
                if (task != null) newTask.setId(task.getId());
                newTask.setNom(titreField.getText());
                newTask.setDescription(descriptionArea.getText());
                newTask.setPriorite((String) prioriteCombo.getSelectedItem());
                newTask.setStatut((String) statutCombo.getSelectedItem());
                Project selectedProject = (Project) projetCombo.getSelectedItem();
                newTask.setProject(dbManager.getProjectById(selectedProject.getId()));
                
                if (task == null) {
                    dbManager.createTask(newTask);
                } else {
                    dbManager.updateTask(newTask);
                }
                
                loadTasks();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'enregistrement : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, "cell 0 6 2 1, growx");
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void handleTaskAction(int row) {
        int taskId = (int) taskTable.getValueAt(row, 0);
        String[] options = {"Modifier", "Supprimer", "Assigner des utilisateurs", "Annuler"};
        
        int choice = JOptionPane.showOptionDialog(
            this,
            "Que souhaitez-vous faire avec cette tâche ?",
            "Actions sur la tâche",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        try {
            switch (choice) {
                case 0: // Modifier
                    Task task = dbManager.getTaskById(taskId);
                    showTaskDialog(task);
                    break;
                case 1: // Supprimer
                    if (JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer cette tâche ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        dbManager.deleteTask(taskId);
                        loadTasks();
                    }
                    break;
                case 2: // Assigner des utilisateurs
                    // TODO: Implémenter l'assignation des utilisateurs
                    break;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'opération : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Classes internes pour le rendu des boutons dans la table
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(70, 130, 180));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            setFocusPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Actions");
            return this;
        }
    }
    
    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final Consumer<Integer> actionHandler;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox, Consumer<Integer> actionHandler) {
            super(checkBox);
            this.actionHandler = actionHandler;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            button.setText("Actions");
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            actionHandler.accept(row);
            return "Actions";
        }
    }
}