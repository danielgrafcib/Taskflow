package taskflow.ui;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import taskflow.models.User;
import taskflow.utils.DatabaseManager;
import taskflow.ui.style.StyleConstants;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private final DatabaseManager dbManager;
    private JButton fermerButton;

    public LoginFrame() {
        this.setUndecorated(true); // Appelé avant que la fenêtre ne devienne "displayable"
        this.dbManager = DatabaseManager.getInstance();
        this.dbManager.setParentComponent(this);
        FlatLightLaf.setup();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("TaskFlow - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        
        // Panneau principal avec disposition en grille
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[50%][50%]", "[100%]"));
        mainPanel.setBackground(StyleConstants.BACKGROUND_WHITE);
        
        // Panneau de gauche avec dégradé
        JPanel leftPanel = new JPanel(new MigLayout("fill", "[center]", "[center]")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Créer un dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, StyleConstants.PRIMARY_COLOR,
                    getWidth(), getHeight(), StyleConstants.ACCENT_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        // Panneau de droite avec le formulaire
        JPanel rightPanel = new JPanel(new MigLayout("fill, insets 40", "[grow]", "[]25[][]30[]"));
        rightPanel.setBackground(StyleConstants.BACKGROUND_WHITE);

        // Bouton Fermer en haut à droite
        fermerButton = new JButton("X");
        fermerButton.setBackground(new Color(239, 83, 80));
        fermerButton.setForeground(Color.WHITE);
        fermerButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        fermerButton.setBorderPainted(false);
        fermerButton.setFocusPainted(false);
        fermerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fermerButton.addActionListener(e -> System.exit(0));
        rightPanel.add(fermerButton, "pos 370 10"); // Positionné en haut à droite

        // Contenu du panneau de gauche
        JLabel welcomeLabel = new JLabel("Bienvenue !");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel, "wrap");
        
        JLabel descriptionLabel = new JLabel("<html><div style='text-align: center;'>Gérez vos projets<br>efficacement avec TaskFlow</div></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionLabel.setForeground(new Color(255, 255, 255, 220));
        leftPanel.add(descriptionLabel, "wrap");
        
        mainPanel.add(leftPanel, "grow");
        
        // Logo et titre
        JPanel headerPanel = new JPanel(new MigLayout("wrap, center"));
        headerPanel.setBackground(StyleConstants.BACKGROUND_WHITE);
        
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(StyleConstants.TITLE_LARGE);
        titleLabel.setForeground(StyleConstants.PRIMARY_COLOR);
        JLabel subtitleLabel = new JLabel("Entrez vos identifiants pour continuer");
        subtitleLabel.setFont(StyleConstants.TEXT_REGULAR);
        subtitleLabel.setForeground(StyleConstants.TEXT_SECONDARY);
        
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        rightPanel.add(headerPanel, "center, wrap");
        
        // Champs de connexion
        JPanel formPanel = new JPanel(new MigLayout("fillx, wrap", "[grow]", "[]10[]20[]10[]"));
        formPanel.setBackground(StyleConstants.BACKGROUND_WHITE);
        
        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(StyleConstants.TEXT_SMALL);
        emailLabel.setForeground(StyleConstants.TEXT_SECONDARY);
        formPanel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.putClientProperty("JTextField.placeholderText", "Entrez votre email");
        emailField.putClientProperty("JComponent.roundRect", true);
        emailField.setPreferredSize(new Dimension(0, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        formPanel.add(emailField, "growx");
        
        // Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(StyleConstants.TEXT_SMALL);
        passwordLabel.setForeground(StyleConstants.TEXT_SECONDARY);
        formPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Entrez votre mot de passe");
        passwordField.putClientProperty("JComponent.roundRect", true);
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        formPanel.add(passwordField, "growx");
        
        rightPanel.add(formPanel, "growx, wrap");
        
        // Bouton de connexion
        JButton loginButton = new JButton("Se connecter");
        loginButton.setBackground(StyleConstants.ACCENT_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(StyleConstants.TEXT_REGULAR);
        loginButton.putClientProperty("JButton.buttonType", "roundRect");
        loginButton.setPreferredSize(new Dimension(0, 45));
        loginButton.addActionListener(e -> handleLogin());
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        
        rightPanel.add(loginButton, "growx");
        mainPanel.add(rightPanel, "grow");
        
        add(mainPanel);
        setLocationRelativeTo(null);
        setResizable(false);
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validation des champs
        if (!validateFields(email, password)) {
            return;
        }
        
        try {
            User user = dbManager.authenticateUser(email, password);
            if (user != null) {
                // Ouvrir le tableau de bord
                MainDashboard dashboard = new MainDashboard(user);
                dashboard.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            handleDatabaseError(ex);
        }
    }
    
    private boolean validateFields(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validation du format de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Format d'email invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validation de la longueur du mot de passe
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void handleDatabaseError(SQLException ex) {
        String errorMessage = "Erreur de connexion à la base de données";
        String errorDetails = ex.getMessage().toLowerCase();
        
        if (errorDetails.contains("communications link failure")) {
            errorMessage = "Impossible de se connecter à la base de données. Vérifiez que MySQL est démarré.";
        } else if (errorDetails.contains("unknown database")) {
            errorMessage = "La base de données 'taskflow' n'existe pas. Veuillez l'initialiser.";
        } else if (errorDetails.contains("access denied")) {
            errorMessage = "Accès refusé à la base de données. Vérifiez les identifiants MySQL.";
        }
        
        JOptionPane.showMessageDialog(this, errorMessage, "Erreur", JOptionPane.ERROR_MESSAGE);
        System.err.println("Erreur SQL: " + ex.getMessage());
        ex.printStackTrace();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}