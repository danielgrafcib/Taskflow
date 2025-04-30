package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import taskflow.models.User;
import taskflow.models.Task;
import taskflow.models.Project;
import taskflow.utils.DatabaseManager;

public class AssignmentPanel extends JPanel {
    private final User currentUser;
    private final DatabaseManager dbManager;
    private JComboBox<Project> projectComboBox;
    private JComboBox<Task> taskComboBox;
    private JList<User> userList;
    private DefaultTableModel assignmentTableModel;
    private JTable assignmentTable;
    
    public AssignmentPanel(User user) {
        this.currentUser = user;
        this.dbManager = DatabaseManager.getInstance();
        this.dbManager.setParentComponent(this);
        try {
            initializeUI();
            loadProjects();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion à la base de données : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow][grow]", "[][grow]"));
        
        // Panel de sélection
        JPanel selectionPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][][][grow][]"));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Assignation"));
        
        // Sélection du projet
        selectionPanel.add(new JLabel("Projet :"), "wrap");
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
        projectComboBox.addActionListener(e -> loadTasksForProject());
        selectionPanel.add(projectComboBox, "growx, wrap");
        
        // Sélection de la tâche
        selectionPanel.add(new JLabel("Tâche :"), "wrap");
        taskComboBox = new JComboBox<>();
        taskComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task) {
                    setText(((Task) value).getNom());
                }
                return this;
            }
        });
        taskComboBox.addActionListener(e -> loadUsersForTask());
        selectionPanel.add(taskComboBox, "growx, wrap");
        
        // Liste des utilisateurs disponibles
        selectionPanel.add(new JLabel("Utilisateurs disponibles :"), "wrap");
        userList = new JList<>();
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        selectionPanel.add(userScrollPane, "grow, wrap");
        
        // Bouton d'assignation
        JButton assignButton = new JButton("Assigner les utilisateurs sélectionnés");
        assignButton.addActionListener(e -> assignUsers());
        selectionPanel.add(assignButton, "growx");
        
        add(selectionPanel, "grow");
        
        // Panel d'historique des assignations
        JPanel historyPanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        historyPanel.setBorder(BorderFactory.createTitledBorder("Historique des assignations"));
        
        setupAssignmentTable();
        JScrollPane tableScrollPane = new JScrollPane(assignmentTable);
        historyPanel.add(tableScrollPane, "grow");
        
        add(historyPanel, "grow");
    }
    
    private void setupAssignmentTable() {
        assignmentTableModel = new DefaultTableModel(
            new String[]{"Tâche", "Projet", "Utilisateur", "Date d'assignation", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        assignmentTable = new JTable(assignmentTableModel);
        assignmentTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        assignmentTable.getColumnModel().getColumn(4).setCellEditor(
            new ButtonEditor(new JCheckBox(), this::handleAssignmentAction)
        );
    }
    
    private void loadProjects() {
        try {
            projectComboBox.removeAllItems();
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
    
    private void loadTasksForProject() {
        Project selectedProject = (Project) projectComboBox.getSelectedItem();
        if (selectedProject == null) return;
        
        try {
            taskComboBox.removeAllItems();
            List<Task> tasks = dbManager.getTasksByProject(selectedProject.getId());
            for (Task task : tasks) {
                taskComboBox.addItem(task);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des tâches : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadUsersForTask() {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) return;
        
        try {
            DefaultListModel<User> userListModel = new DefaultListModel<>();
            List<User> availableUsers = dbManager.getAvailableUsersForTask(selectedTask.getId());
            for (User user : availableUsers) {
                userListModel.addElement(user);
            }
            userList.setModel(userListModel);
            
            // Mettre à jour l'historique des assignations pour cette tâche
            loadAssignmentHistory(selectedTask.getId());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des utilisateurs : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAssignmentHistory(int taskId) {
        assignmentTableModel.setRowCount(0);
        try {
            List<Object[]> assignments = dbManager.getTaskAssignments(taskId);
            for (Object[] assignment : assignments) {
                assignmentTableModel.addRow(assignment);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement de l'historique : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void assignUsers() {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        List<User> selectedUsers = userList.getSelectedValuesList();
        
        if (selectedTask == null || selectedUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une tâche et au moins un utilisateur",
                "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            for (User user : selectedUsers) {
                dbManager.assignUserToTask(user.getId(), selectedTask.getId());
            }
            
            // Rafraîchir les listes
            loadUsersForTask();
            JOptionPane.showMessageDialog(this,
                "Assignation effectuée avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'assignation : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAssignmentAction(int row) {
        String taskName = (String) assignmentTable.getValueAt(row, 0);
        String userName = (String) assignmentTable.getValueAt(row, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Voulez-vous supprimer l'assignation de " + userName + " à la tâche " + taskName + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Task selectedTask = (Task) taskComboBox.getSelectedItem();
                dbManager.removeTaskAssignment(selectedTask.getId(), userName);
                loadUsersForTask();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression de l'assignation : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Classes internes pour le rendu des boutons dans la table
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Supprimer");
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
            button.setText("Supprimer");
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            actionHandler.accept(row);
            return "Supprimer";
        }
    }
}