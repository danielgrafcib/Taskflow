package taskflow.ui;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import taskflow.utils.DatabaseManager;
import taskflow.models.Role;

public class RoleManagementPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JTable roleTable;
    private DefaultTableModel tableModel;
    
    public RoleManagementPanel() {
        this.dbManager = DatabaseManager.getInstance();
        initializeUI();
        loadRoles();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);
        
        // En-tête avec style amélioré
        JPanel headerPanel = new JPanel(new MigLayout("fillx", "[grow]push[]"));
        headerPanel.setBackground(new Color(255, 255, 255));
        JLabel titleLabel = new JLabel("Gestion des Rôles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel, "cell 0 0");
        
        JButton createButton = new JButton("Nouveau Rôle");
        createButton.setBackground(new Color(70, 130, 180));
        createButton.setForeground(Color.WHITE);
        createButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> showRoleDialog(null));
        headerPanel.add(createButton, "cell 1 0");
        
        add(headerPanel, "growx, wrap");
        
        // Table des rôles avec style amélioré
        setupRoleTable();
        JScrollPane scrollPane = new JScrollPane(roleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, "grow");
    }
    
    private void setupRoleTable() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nom", "Description", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        
        roleTable = new JTable(tableModel);
        roleTable.setBackground(Color.WHITE);
        roleTable.setGridColor(new Color(230, 230, 230));
        roleTable.setSelectionBackground(new Color(135, 206, 250));
        roleTable.setRowHeight(35);
        roleTable.setIntercellSpacing(new Dimension(0, 0));
        roleTable.setShowGrid(true);
        roleTable.getColumnModel().getColumn(0).setMaxWidth(50);
        roleTable.getColumnModel().getColumn(3).setMinWidth(150);
        
        // Style des en-têtes de colonnes
        roleTable.getTableHeader().setBackground(new Color(245, 245, 245));
        roleTable.getTableHeader().setForeground(Color.BLACK);
        roleTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Ajouter les boutons d'action dans la dernière colonne
        roleTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        roleTable.getColumnModel().getColumn(3).setCellEditor(
            new ButtonEditor(new JCheckBox(), this::handleRoleAction)
        );
    }
    
    private void loadRoles() {
        tableModel.setRowCount(0);
        try {
            List<Role> roles = dbManager.getAllRoles();
            for (Role role : roles) {
                tableModel.addRow(new Object[]{
                    role.getId(),
                    role.getNom(),
                    role.getDescription(),
                    "Actions"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des rôles : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showRoleDialog(Role role) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                                   role == null ? "Nouveau Rôle" : "Modifier Rôle",
                                   true);
        dialog.setLayout(new MigLayout("fill, insets 20", "[][grow]", "[][][][]"));
        dialog.setBackground(Color.WHITE);
        
        // Champs du formulaire avec style amélioré
        JTextField nomField = new JTextField(role != null ? role.getNom() : "");
        nomField.setPreferredSize(new Dimension(200, 30));
        nomField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        
        JTextArea descriptionArea = new JTextArea(role != null ? role.getDescription() : "", 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Labels stylisés
        JLabel nomLabel = new JLabel("Nom :");
        nomLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel descLabel = new JLabel("Description :");
        descLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Ajout des composants
        dialog.add(nomLabel, "cell 0 0");
        dialog.add(nomField, "cell 1 0, growx, wrap");
        
        dialog.add(descLabel, "cell 0 1");
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        dialog.add(scrollPane, "cell 1 1, growx, wrap");
        
        // Boutons stylisés
        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "push[][][]"));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Enregistrer");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        saveButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        cancelButton.setForeground(new Color(70, 130, 180));
        cancelButton.setFocusPainted(false);
        
        saveButton.addActionListener(e -> {
            try {
                Role newRole = new Role();
                if (role != null) newRole.setId(role.getId());
                newRole.setNom(nomField.getText());
                newRole.setDescription(descriptionArea.getText());
                
                if (role == null) {
                    dbManager.createRole(newRole);
                } else {
                    dbManager.updateRole(newRole);
                }
                
                loadRoles();
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
        dialog.add(buttonPanel, "cell 0 3 2 1, growx");
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void handleRoleAction(int row) {
        int roleId = (int) roleTable.getValueAt(row, 0);
        String[] options = {"Modifier", "Supprimer", "Annuler"};
        
        int choice = JOptionPane.showOptionDialog(
            this,
            "Que souhaitez-vous faire avec ce rôle ?",
            "Actions sur le rôle",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        try {
            switch (choice) {
                case 0: // Modifier
                    Role role = dbManager.getRoleById(roleId);
                    showRoleDialog(role);
                    break;
                case 1: // Supprimer
                    if (JOptionPane.showConfirmDialog(this,
                        "Êtes-vous sûr de vouloir supprimer ce rôle ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        dbManager.deleteRole(roleId);
                        loadRoles();
                    }
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
            setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
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