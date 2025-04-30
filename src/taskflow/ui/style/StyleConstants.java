package taskflow.ui.style;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class StyleConstants {
    // Couleurs principales
    public static final Color PRIMARY_COLOR = new Color(52, 73, 94);
    public static final Color PRIMARY_DARK = new Color(44, 62, 80);
    public static final Color ACCENT_COLOR = new Color(41, 128, 185);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    // Couleurs de fond
    public static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);
    public static final Color BACKGROUND_WHITE = Color.WHITE;
    public static final Color BACKGROUND_HOVER = new Color(236, 240, 241);
    
    // Couleurs de texte
    public static final Color TEXT_PRIMARY = new Color(52, 73, 94);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    public static final Color TEXT_WHITE = Color.WHITE;
    
    // Polices
    public static final Font TITLE_LARGE = new Font("Arial", Font.BOLD, 24);
    public static final Font TITLE_MEDIUM = new Font("Arial", Font.BOLD, 20);
    public static final Font TITLE_SMALL = new Font("Arial", Font.BOLD, 16);
    public static final Font TEXT_REGULAR = new Font("Arial", Font.PLAIN, 14);
    public static final Font TEXT_SMALL = new Font("Arial", Font.PLAIN, 12);
    
    // Bordures
    public static final Border BORDER_LIGHT = new LineBorder(new Color(189, 195, 199), 1);
    public static final Border BORDER_FOCUS = new LineBorder(ACCENT_COLOR, 2);
    
    // Padding
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    
    // Marges composées couramment utilisées
    public static final Border PANEL_BORDER = new CompoundBorder(
        BORDER_LIGHT,
        new EmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM)
    );
    
    // Dimensions
    public static final int BUTTON_HEIGHT = 35;
    public static final int INPUT_HEIGHT = 30;
    public static final int SIDEBAR_WIDTH = 250;
    
    private StyleConstants() {
        // Empêcher l'instanciation
    }
}