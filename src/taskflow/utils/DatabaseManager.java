package taskflow.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import taskflow.models.Project;
import taskflow.models.Role;
import taskflow.models.Task;
import taskflow.models.User;
import javax.swing.JOptionPane;
import java.awt.Component;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/taskflow?useSSL=false&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "";
    private Component parentComponent;
    
    private DatabaseManager() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Tentative de connexion à la base de données MySQL...");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion à la base de données établie avec succès !");
        } catch (ClassNotFoundException e) {
            String message = "Erreur : Driver MySQL introuvable";
            System.err.println(message);
            showError(message, "Erreur de configuration", e);
            this.connection = null;
        } catch (SQLException e) {
            String message = "Erreur de connexion à la base de données MySQL.\n" +
                           "Veuillez vérifier que :\n" +
                           "1. Le serveur MySQL est démarré\n" +
                           "2. Les informations de connexion sont correctes\n" +
                           "3. La base de données 'taskflow' existe";
            System.err.println(message);
            System.err.println("URL : " + url);
            System.err.println("Utilisateur : " + username);
            showError(message, "Erreur de connexion", e);
            this.connection = null;
        }
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        if (instance.connection == null) {
            instance.initializeConnection();
        }
        return instance;
    }
    
    public void setParentComponent(Component component) {
        this.parentComponent = component;
    }
    
    private void showError(String message, String title, Exception ex) {
        if (parentComponent != null) {
            JOptionPane.showMessageDialog(parentComponent, message + "\n" + ex.getMessage(),
                title, JOptionPane.ERROR_MESSAGE);
        }
        System.err.println(message + ": " + ex.getMessage());
        ex.printStackTrace();
    }

    private boolean tryReconnect() {
        System.out.println("Tentative de reconnexion à la base de données...");
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Reconnexion réussie !");
            return true;
        } catch (SQLException e) {
            System.err.println("Échec de la reconnexion : " + e.getMessage());
            return false;
        }
    }

    private void ensureConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            if (!tryReconnect()) {
                throw new SQLException("La connexion à la base de données n'est pas disponible");
            }
        }
    }
    
    // Méthodes pour les projets
    public void createProject(Project project) throws SQLException {
        ensureConnection();
        String query = "INSERT INTO projects (nom, description, date_debut, date_fin, statut) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, project.getNom());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, new java.sql.Date(project.getDateDebut().getTime()));
            stmt.setDate(4, new java.sql.Date(project.getDateFin().getTime()));
            stmt.setString(5, project.getStatut());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (tryReconnect()) {
                createProject(project); // Retry once after reconnection
            } else {
                throw e;
            }
        }
    }
    
    public void updateProject(Project project) throws SQLException {
        String query = "UPDATE projects SET nom = ?, description = ?, date_debut = ?, date_fin = ?, statut = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, project.getNom());
            stmt.setString(2, project.getDescription());
            stmt.setDate(3, new java.sql.Date(project.getDateDebut().getTime()));
            stmt.setDate(4, new java.sql.Date(project.getDateFin().getTime()));
            stmt.setString(5, project.getStatut());
            stmt.setInt(6, project.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteProject(int projectId) throws SQLException {
        String query = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            stmt.executeUpdate();
        }
    }
    
    public Project getProjectById(int projectId) throws SQLException {
        String query = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Project(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getString("statut"),
                    null // owner sera chargé séparément si nécessaire
                );
            }
        }
        return null;
    }
    
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM projects";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDate("date_debut"),
                    rs.getDate("date_fin"),
                    rs.getString("statut"),
                    null // owner sera chargé séparément si nécessaire
                ));
            }
        }
        return projects;
    }
    
    // Méthodes pour les tâches
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT t.*, p.* FROM tasks t LEFT JOIN projects p ON t.project_id = p.id";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Project project = new Project(
                    rs.getInt("p.id"),
                    rs.getString("p.nom"),
                    rs.getString("p.description"),
                    rs.getDate("p.date_debut"),
                    rs.getDate("p.date_fin"),
                    rs.getString("p.statut"),
                    null
                );
                
                Task task = new Task(
                    rs.getInt("t.id"),
                    rs.getString("t.nom"),
                    rs.getString("t.description"),
                    rs.getDate("t.date_debut"),
                    rs.getDate("t.date_fin"),
                    rs.getString("t.statut"),
                    rs.getString("t.priorite"),
                    project,
                    null
                );
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    public List<Task> getTasksByProject(int projectId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT t.*, p.* FROM tasks t " +
                      "JOIN projects p ON t.project_id = p.id " +
                      "WHERE t.project_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Project project = new Project(
                    rs.getInt("p.id"),
                    rs.getString("p.nom"),
                    rs.getString("p.description"),
                    rs.getDate("p.date_debut"),
                    rs.getDate("p.date_fin"),
                    rs.getString("p.statut"),
                    null
                );
                
                Task task = new Task(
                    rs.getInt("t.id"),
                    rs.getString("t.nom"),
                    rs.getString("t.description"),
                    rs.getDate("t.date_debut"),
                    rs.getDate("t.date_fin"),
                    rs.getString("t.statut"),
                    rs.getString("t.priorite"),
                    project,
                    null
                );
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    public Task getTaskById(int id) throws SQLException {
        String query = "SELECT t.*, p.* FROM tasks t " +
                      "LEFT JOIN projects p ON t.project_id = p.id " +
                      "WHERE t.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Project project = new Project(
                    rs.getInt("p.id"),
                    rs.getString("p.nom"),
                    rs.getString("p.description"),
                    rs.getDate("p.date_debut"),
                    rs.getDate("p.date_fin"),
                    rs.getString("p.statut"),
                    null
                );
                
                return new Task(
                    rs.getInt("t.id"),
                    rs.getString("t.nom"),
                    rs.getString("t.description"),
                    rs.getDate("t.date_debut"),
                    rs.getDate("t.date_fin"),
                    rs.getString("t.statut"),
                    rs.getString("t.priorite"),
                    project,
                    null
                );
            }
        }
        return null;
    }
    
    public void createTask(Task task) throws SQLException {
        String query = "INSERT INTO tasks (nom, description, date_debut, date_fin, statut, priorite, project_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, task.getNom());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, new java.sql.Date(task.getDateDebut().getTime()));
            stmt.setDate(4, new java.sql.Date(task.getDateFin().getTime()));
            stmt.setString(5, task.getStatut());
            stmt.setString(6, task.getPriorite());
            stmt.setInt(7, task.getProject().getId());
            stmt.executeUpdate();
        }
    }
    
    public void updateTask(Task task) throws SQLException {
        String query = "UPDATE tasks SET nom = ?, description = ?, date_debut = ?, date_fin = ?, " +
                      "statut = ?, priorite = ?, project_id = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, task.getNom());
            stmt.setString(2, task.getDescription());
            stmt.setDate(3, new java.sql.Date(task.getDateDebut().getTime()));
            stmt.setDate(4, new java.sql.Date(task.getDateFin().getTime()));
            stmt.setString(5, task.getStatut());
            stmt.setString(6, task.getPriorite());
            stmt.setInt(7, task.getProject().getId());
            stmt.setInt(8, task.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteTask(int id) throws SQLException {
        String query = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    
    // Méthodes pour les utilisateurs
    public Map<String, String> validateLogin(String username, String password) throws SQLException {
        Map<String, String> result = new HashMap<>();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result.put("status", "success");
                result.put("userId", String.valueOf(rs.getInt("id")));
                result.put("roleId", String.valueOf(rs.getInt("role_id")));
            } else {
                result.put("status", "error");
                result.put("message", "Invalid username or password");
            }
        }
        return result;
    }
    
    public Map<String, Object> getUserInfo(int userId) throws SQLException {
        Map<String, Object> userInfo = new HashMap<>();
        String query = "SELECT u.*, r.nom as role_nom FROM users u " +
                      "JOIN roles r ON u.role_id = r.id " +
                      "WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userInfo.put("id", rs.getInt("id"));
                userInfo.put("username", rs.getString("username"));
                userInfo.put("nom", rs.getString("nom"));
                userInfo.put("email", rs.getString("email"));
                userInfo.put("roleId", rs.getInt("role_id"));
                userInfo.put("roleName", rs.getString("role_nom"));
            }
        }
        return userInfo;
    }
    
    public List<Task> getUserTaskHistory(int userId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT t.*, p.* FROM tasks t " +
                      "JOIN projects p ON t.project_id = p.id " +
                      "JOIN task_assignments ta ON t.id = ta.task_id " +
                      "WHERE ta.user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Project project = new Project(
                    rs.getInt("p.id"),
                    rs.getString("p.nom"),
                    rs.getString("p.description"),
                    rs.getDate("p.date_debut"),
                    rs.getDate("p.date_fin"),
                    rs.getString("p.statut"),
                    null
                );
                
                Task task = new Task(
                    rs.getInt("t.id"),
                    rs.getString("t.nom"),
                    rs.getString("t.description"),
                    rs.getDate("t.date_debut"),
                    rs.getDate("t.date_fin"),
                    rs.getString("t.statut"),
                    rs.getString("t.priorite"),
                    project,
                    null
                );
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    // Méthodes pour les rôles
    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM roles";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description")
                ));
            }
        }
        return roles;
    }
    
    public Role getRoleById(int roleId) throws SQLException {
        String query = "SELECT * FROM roles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Role(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description")
                );
            }
        }
        return null;
    }
    
    public void createRole(Role role) throws SQLException {
        String query = "INSERT INTO roles (nom, description) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, role.getNom());
            stmt.setString(2, role.getDescription());
            stmt.executeUpdate();
        }
    }
    
    public void updateRole(Role role) throws SQLException {
        String query = "UPDATE roles SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, role.getNom());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteRole(int roleId) throws SQLException {
        String query = "DELETE FROM roles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, roleId);
            stmt.executeUpdate();
        }
    }
    
    // Méthodes pour les statistiques
    public Map<String, Integer> getTaskStatusStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT statut, COUNT(*) as count FROM tasks GROUP BY statut";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("statut"), rs.getInt("count"));
            }
        }
        return stats;
    }
    
    public Map<String, Integer> getUserTaskStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT u.nom, COUNT(ta.task_id) as task_count " +
                      "FROM users u " +
                      "LEFT JOIN task_assignments ta ON u.id = ta.user_id " +
                      "GROUP BY u.id, u.nom";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("nom"), rs.getInt("task_count"));
            }
        }
        return stats;
    }
    
    public int getGlobalCompletionRate() throws SQLException {
        String query = "SELECT " +
                      "(SELECT COUNT(*) FROM tasks WHERE statut = 'TERMINEE') * 100.0 / COUNT(*) as completion_rate " +
                      "FROM tasks";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) rs.getDouble("completion_rate");
            }
        }
        return 0;
    }
    
    public int getTotalTasksCount() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM tasks";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    public int getLateTasksCount() throws SQLException {
        String query = "SELECT COUNT(*) as late_count FROM tasks " +
                      "WHERE date_fin < CURRENT_DATE AND statut != 'TERMINEE'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("late_count");
            }
        }
        return 0;
    }
    
    public Object[][] getProjectStatistics() throws SQLException {
        String query = "SELECT p.nom as project_name, " +
                      "COUNT(t.id) as total_tasks, " +
                      "SUM(CASE WHEN t.statut = 'TERMINEE' THEN 1 ELSE 0 END) as completed_tasks, " +
                      "SUM(CASE WHEN t.date_fin < CURRENT_DATE AND t.statut != 'TERMINEE' THEN 1 ELSE 0 END) as late_tasks " +
                      "FROM projects p " +
                      "LEFT JOIN tasks t ON p.id = t.project_id " +
                      "GROUP BY p.id, p.nom";
        
        List<Object[]> stats = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("project_name"),
                    rs.getInt("total_tasks"),
                    rs.getInt("completed_tasks"),
                    rs.getInt("late_tasks")
                };
                stats.add(row);
            }
        }
        return stats.toArray(new Object[0][]);
    }
    
    public User authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT a.*, r.* FROM admin a " +
                      "LEFT JOIN roles r ON a.role_id = r.id " +
                      "WHERE a.email = ? AND a.mot_de_passe = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Role role = new Role(
                    rs.getInt("r.id"),
                    rs.getString("r.nom"),
                    rs.getString("r.description")
                );
                
                return new User(
                    rs.getInt("a.id"),
                    rs.getString("a.nom"),
                    rs.getString("a.email"),
                    rs.getString("a.mot_de_passe"),
                    role
                );
            }
        }
        return null;
    }

    public List<User> getAvailableUsersForTask(int taskId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.*, r.* FROM users u " +
                      "LEFT JOIN roles r ON u.role_id = r.id " +
                      "WHERE u.id NOT IN (SELECT user_id FROM task_assignments WHERE task_id = ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Role role = new Role(
                    rs.getInt("r.id"),
                    rs.getString("r.nom"),
                    rs.getString("r.description")
                );
                
                User user = new User(
                    rs.getInt("u.id"),
                    rs.getString("u.nom"),
                    rs.getString("u.email"),
                    rs.getString("u.mot_de_passe"),
                    role
                );
                users.add(user);
            }
        }
        return users;
    }

    public void assignUserToTask(int userId, int taskId) throws SQLException {
        String query = "INSERT INTO task_assignments (user_id, task_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
        }
    }

    public void removeTaskAssignment(int taskId, String userName) throws SQLException {
        String query = "DELETE FROM task_assignments WHERE task_id = ? AND user_id = (SELECT id FROM users WHERE nom = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            stmt.setString(2, userName);
            stmt.executeUpdate();
        }
    }

    public List<Object[]> getTaskAssignments(int taskId) throws SQLException {
        List<Object[]> assignments = new ArrayList<>();
        String query = "SELECT t.nom as task_nom, p.nom as project_nom, u.nom as user_nom, ta.date_assignation " +
                      "FROM task_assignments ta " +
                      "JOIN tasks t ON ta.task_id = t.id " +
                      "JOIN projects p ON t.project_id = p.id " +
                      "JOIN users u ON ta.user_id = u.id " +
                      "WHERE ta.task_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("task_nom"),
                    rs.getString("project_nom"),
                    rs.getString("user_nom"),
                    rs.getTimestamp("date_assignation")
                };
                assignments.add(row);
            }
        }
        return assignments;
    }
}