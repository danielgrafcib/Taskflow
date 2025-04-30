package taskflow.ui.components;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import taskflow.ui.style.StyleConstants;

public class SidebarPanel extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private JPanel selectedButton;
    
    public SidebarPanel(CardLayout cardLayout, JPanel contentPanel) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new MigLayout("fillx, wrap, insets 0", "[fill]", "[]0[]"));
        setPreferredSize(new Dimension(StyleConstants.SIDEBAR_WIDTH, 0));
        setBackground(StyleConstants.PRIMARY_COLOR);
        
        // Logo ou titre de l'application
        JLabel logoLabel = new JLabel("TaskFlow");
        logoLabel.setFont(StyleConstants.TITLE_MEDIUM);
        logoLabel.setForeground(StyleConstants.TEXT_WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(logoLabel, "h 60!, center");
        
        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(StyleConstants.PRIMARY_DARK);
        add(separator, "growx");
    }
    
    public void addNavigationButton(String text, String cardName, String iconPath) {
        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0 10"));
        buttonPanel.setBackground(StyleConstants.PRIMARY_COLOR);
        buttonPanel.setName(cardName);
        
        // Icône (si fournie)
        if (iconPath != null) {
            JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource(iconPath)));
            buttonPanel.add(iconLabel, "gapright 10");
        }
        
        // Texte du bouton
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(StyleConstants.TEXT_WHITE);
        textLabel.setFont(StyleConstants.TEXT_REGULAR);
        buttonPanel.add(textLabel, "push");
        
        // Gestion des événements
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(contentPanel, cardName);
                updateSelection(buttonPanel);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (buttonPanel != selectedButton) {
                    buttonPanel.setBackground(StyleConstants.PRIMARY_DARK);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (buttonPanel != selectedButton) {
                    buttonPanel.setBackground(StyleConstants.PRIMARY_COLOR);
                }
            }
        });
        
        add(buttonPanel, "h 45!");
        
        // Sélectionner le premier bouton par défaut
        if (selectedButton == null) {
            selectedButton = buttonPanel;
            buttonPanel.setBackground(StyleConstants.PRIMARY_DARK);
        }
    }
    
    private void updateSelection(JPanel newSelection) {
        if (selectedButton != null) {
            selectedButton.setBackground(StyleConstants.PRIMARY_COLOR);
        }
        selectedButton = newSelection;
        selectedButton.setBackground(StyleConstants.PRIMARY_DARK);
    }
}