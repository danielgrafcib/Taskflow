package taskflow.utils;

import javax.swing.*;
import java.awt.*;

public class NotificationManager {
    private static NotificationManager instance;
    private Component parentComponent;
    
    private NotificationManager() {}
    
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    public void setParentComponent(Component parent) {
        this.parentComponent = parent;
    }
    
    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(parentComponent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    public void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(parentComponent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(parentComponent,
            message,
            title,
            JOptionPane.WARNING_MESSAGE);
    }
    
    public boolean showConfirmation(String message, String title) {
        return JOptionPane.showConfirmDialog(parentComponent,
            message,
            title,
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
} 