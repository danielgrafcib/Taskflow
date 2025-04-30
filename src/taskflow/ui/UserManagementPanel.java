package taskflow.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;
import net.miginfocom.swing.MigLayout;
import taskflow.models.Role;
import taskflow.models.User;
import taskflow.utils.DatabaseManager;

public class UserManagementPanel extends JPanel {
    private final DatabaseManager dbManager;
    private JTextField nomField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JTable userTable;
    private JTextField roleNomField;
    private JTextArea roleDescriptionArea;
    
    public UserManagementPanel() {
        dbManager = DatabaseManager.getInstance();
        initializeUI();
        loadRoles();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[grow]"));
        
        // Panel principal avec deux onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Gestion des Utilisateurs", createUserPanel());
        tabbedPane.addTab("Gestion des Rôles", createRolePanel());
        
        add(tabbedPane, "grow");
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        
        // Formulaire d'ajout d'utilisateur
        JPanel formPanel = new JPanel(new MigLayout("fillx, wrap 2", "[][grow]", "[][][][]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un nouvel utilisateur"));
        
        formPanel.add(new JLabel("Nom:"));
        nomField = new JTextField(20);
        formPanel.add(nomField, "growx");
        
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        formPanel.add(emailField, "growx");
        
        formPanel.add(new JLabel("Mot de passe:"));
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, "growx");
        
        formPanel.add(new JLabel("Rôle:"));
        roleComboBox = new JComboBox<>();
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Role) {
                    value = ((Role) value).getNom();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        formPanel.add(roleComboBox, "growx");
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> addUser());
        formPanel.add(addButton, "span 2, align center");
        
        panel.add(formPanel, "growx, wrap");
        
        // Table des utilisateurs
        userTable = new JTable(); // À implémenter: modèle de table personnalisé
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, "grow");
        
        return panel;
    }
    
    private JPanel createRolePanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        
        // Formulaire d'ajout de rôle
        JPanel formPanel = new JPanel(new MigLayout("fillx, wrap 2", "[][grow]", "[][]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ajouter un nouveau rôle"));
        
        formPanel.add(new JLabel("Nom:"));
        roleNomField = new JTextField(20);
        formPanel.add(roleNomField, "growx");
        
        formPanel.add(new JLabel("Description:"));
        roleDescriptionArea = new JTextArea(3, 20);
        roleDescriptionArea.setLineWrap(true);
        roleDescriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(roleDescriptionArea);
        formPanel.add(descScrollPane, "growx");
        
        JButton addRoleButton = new JButton("Ajouter");
        addRoleButton.addActionListener(e -> addRole());
        formPanel.add(addRoleButton, "span 2, align center");
        
        panel.add(formPanel, "growx, wrap");
        
        // Table des rôles
        JTable roleTable = new JTable(); // À implémenter: modèle de table personnalisé
        JScrollPane scrollPane = new JScrollPane(roleTable);
        panel.add(scrollPane, "grow");
        
        return panel;
    }
    
    private void loadRoles() {
        try {
            List<Role> roles = dbManager.getAllRoles();
            roleComboBox.removeAllItems();
            for (Role role : roles) {
                roleComboBox.addItem(role);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des rôles: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addUser() {
        String nom = nomField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        
        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole == null) {
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs",
                "Erreur",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Implémenter l'ajout d'utilisateur via DatabaseManager
        // Réinitialiser les champs après l'ajout
        clearUserFields();
    }
    
    private void addRole() {
        String nom = roleNomField.getText().trim();
        String description = roleDescriptionArea.getText().trim();
        
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Le nom du rôle est obligatoire",
                "Erreur",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Role newRole = new Role(0, nom, description); // L'ID sera généré par la base de données
            dbManager.createRole(newRole);
            JOptionPane.showMessageDialog(this,
                "Rôle ajouté avec succès",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            clearRoleFields();
            loadRoles(); // Recharger la liste des rôles
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout du rôle: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearUserFields() {
        nomField.setText("");
        emailField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(-1);
    }
    
    private void clearRoleFields() {
        roleNomField.setText("");
        roleDescriptionArea.setText("");
    }
}