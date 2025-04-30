package taskflow.ui;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import taskflow.models.User;
import taskflow.utils.DatabaseManager;
import taskflow.utils.NotificationManager;
import taskflow.ui.ProjectPanel;
import taskflow.ui.HistoryPanel;
import taskflow.ui.DashboardPanel;

public class MainDashboard extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Panels de l'application
    private ProjectPanel projectPanel;
    private TaskPanel taskPanel;
    private KPIPanel kpiPanel;
    private HistoryPanel historyPanel;
    private AssignmentPanel assignmentPanel;
    private RoleManagementPanel rolePanel;
    private DashboardPanel dashboardPanel;
    private UserManagementPanel userManagementPanel;
    
    public MainDashboard(User user) {
        this.currentUser = user;
        FlatLightLaf.setup();
        initializeUI();
        setupPanels();
    }
    
    private void initializeUI() {
        setTitle("TaskFlow - Tableau de bord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Mettre en plein écran
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setMinimumSize(new Dimension(1000, 600));
        
        // Layout principal avec MigLayout pour plus de flexibilité
        setLayout(new MigLayout("fill, insets 0", "[200!][grow]", "[60!][grow]"));
        
        // Panel de navigation (sidebar) avec style moderne
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, "cell 0 0 1 2, grow");
        
        // En-tête avec informations utilisateur et recherche
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, "cell 1 0, growx");
        
        // Panel de contenu principal avec CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));
        add(contentPanel, "cell 1 1, grow");
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 15 25", "[]30[grow]push[]"));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        // Titre de la page
        JLabel pageTitle = new JLabel("Tableau de bord");
        pageTitle.setFont(new Font("Roboto", Font.BOLD, 20));
        headerPanel.add(pageTitle);
        
        // Barre de recherche
        JTextField searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher...");
        searchField.putClientProperty("JTextField.padding", new Insets(8, 10, 8, 10));
        headerPanel.add(searchField, "width 300!");
        
        // Panel utilisateur
        JPanel userPanel = new JPanel(new MigLayout("insets 0"));
        userPanel.setOpaque(false);
        
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userPanel.add(userIcon);
        
        JLabel welcomeLabel = new JLabel(currentUser.getNom());
        welcomeLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        userPanel.add(welcomeLabel);
        
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.putClientProperty("JButton.buttonType", "roundRect");
        logoutButton.setFont(new Font("Roboto", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> handleLogout());
        userPanel.add(logoutButton, "gap 10");
        
        headerPanel.add(userPanel);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new MigLayout("fillx, wrap, insets 0 0 20 0", "[fill]", "[80!][]10[]"));
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBackground(new Color(67, 56, 202));
        
        // Logo et titre de l'application
        JPanel logoPanel = new JPanel(new MigLayout("insets 20"));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("TaskFlow");
        logoLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        sidebarPanel.add(logoPanel, "growx");
        
        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 50));
        sidebarPanel.add(separator, "growx, gap 10 10 20 10");
        
        // Boutons de navigation avec icônes
        addNavigationButton(sidebarPanel, "📊 Tableau de bord", "DASHBOARD");
        addNavigationButton(sidebarPanel, "📁 Projets", "PROJECTS");
        addNavigationButton(sidebarPanel, "✓ Tâches", "TASKS");
        addNavigationButton(sidebarPanel, "👥 Assignations", "ASSIGNMENTS");
        addNavigationButton(sidebarPanel, "📈 KPIs", "KPIS");
        addNavigationButton(sidebarPanel, "📋 Historique", "HISTORY");
        
        // Ajout des boutons d'administration pour tous les utilisateurs
        addNavigationButton(sidebarPanel, "👤 Gestion des utilisateurs", "USERS");
        addNavigationButton(sidebarPanel, "⚙️ Gestion des rôles", "ROLES");
        
        
        return sidebarPanel;
    }
    
    private void addNavigationButton(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.PLAIN, 14));
        button.setForeground(new Color(255, 255, 255, 220));
        button.setBackground(new Color(67, 56, 202));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.putClientProperty("JButton.buttonType", "roundRect");
        
        // Ajouter un padding
        button.setMargin(new Insets(10, 20, 10, 20));
        
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            // Mettre à jour l'apparence de tous les boutons
            for (Component c : panel.getComponents()) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c;
                    if (b == button) {
                        b.setBackground(new Color(79, 70, 229));
                        b.setForeground(Color.WHITE);
                    } else {
                        b.setBackground(new Color(67, 56, 202));
                        b.setForeground(new Color(255, 255, 255, 220));
                    }
                }
            }
        });
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.getBackground().equals(new Color(67, 56, 202))) {
                    button.setBackground(new Color(79, 70, 229));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!contentPanel.getLayout().toString().contains(cardName)) {
                    button.setBackground(new Color(67, 56, 202));
                }
            }
        });
        
        panel.add(button, "growx, gap 10 10 0 0");
    }
    
    private void setupPanels() {
        // Initialisation des différents panels avec style uniforme
        projectPanel = new ProjectPanel(currentUser);
        taskPanel = new TaskPanel(currentUser);
        kpiPanel = new KPIPanel(currentUser);
        historyPanel = new HistoryPanel(currentUser);
        assignmentPanel = new AssignmentPanel(currentUser);
        dashboardPanel = new DashboardPanel();
        userManagementPanel = new UserManagementPanel();
        rolePanel = new RoleManagementPanel();
        
        // Configuration du style commun pour tous les panels
        JPanel[] allPanels = {
            projectPanel, taskPanel, kpiPanel, historyPanel,
            assignmentPanel, dashboardPanel, userManagementPanel, rolePanel
        };
        
        for (JPanel panel : allPanels) {
            panel.setBackground(new Color(245, 247, 250));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }
        
        // Ajout des panels au CardLayout avec une organisation cohérente
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(projectPanel, "PROJECTS");
        contentPanel.add(taskPanel, "TASKS");
        contentPanel.add(assignmentPanel, "ASSIGNMENTS");
        contentPanel.add(kpiPanel, "KPIS");
        contentPanel.add(historyPanel, "HISTORY");
        contentPanel.add(userManagementPanel, "USERS");
        contentPanel.add(rolePanel, "ROLES");
        
        // Afficher le panel du tableau de bord par défaut
        cardLayout.show(contentPanel, "DASHBOARD");

        // Forcer le rafraîchissement initial
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}