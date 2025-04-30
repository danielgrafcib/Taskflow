package taskflow.ui.components;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import taskflow.ui.style.StyleConstants;

public class DashboardCard extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel iconLabel;
    
    public DashboardCard(String title, String iconPath) {
        setLayout(new MigLayout("fill, insets 15", "[grow]push[]", "[]5[]push"));
        setBorder(StyleConstants.PANEL_BORDER);
        setBackground(StyleConstants.BACKGROUND_WHITE);
        
        // Configuration du titre
        titleLabel = new JLabel(title);
        titleLabel.setFont(StyleConstants.TEXT_REGULAR);
        titleLabel.setForeground(StyleConstants.TEXT_SECONDARY);
        
        // Configuration de la valeur
        valueLabel = new JLabel("0");
        valueLabel.setFont(StyleConstants.TITLE_LARGE);
        valueLabel.setForeground(StyleConstants.TEXT_PRIMARY);
        
        // Configuration de l'icône
        iconLabel = new JLabel();
        if (iconPath != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            iconLabel.setIcon(icon);
        }
        
        // Ajout des composants
        add(titleLabel, "cell 0 0");
        add(iconLabel, "cell 1 0 1 2, w 32!, h 32!");
        add(valueLabel, "cell 0 1");
        
        // Effet de survol
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(StyleConstants.BACKGROUND_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(StyleConstants.BACKGROUND_WHITE);
            }
        });
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setValue(int value) {
        setValue(String.valueOf(value));
    }
    
    public void setValueColor(Color color) {
        valueLabel.setForeground(color);
    }
    
    public void setIcon(String iconPath) {
        if (iconPath != null) {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            iconLabel.setIcon(icon);
        }
    }
}