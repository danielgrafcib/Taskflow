package taskflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import taskflow.utils.DatabaseConnection;
import taskflow.ui.entities.TaskFlowEntities.*;

public class DatabaseConfig {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT * FROM projects";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("id"));
                project.setNom(rs.getString("nom"));
                project.setDescription(rs.getString("description"));
                project.setDateDebut(rs.getDate("date_debut"));
                project.setDateFin(rs.getDate("date_fin"));
                project.setStatut(rs.getString("statut"));
                projects.add(project);
            }
        }
        return projects;
    }

    public Project getProjectById(int id) throws SQLException {
        String query = "SELECT * FROM projects WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Project project = new Project();
                    project.setId(rs.getInt("id"));
                    project.setNom(rs.getString("nom"));
                    project.setDescription(rs.getString("description"));
                    project.setDateDebut(rs.getDate("date_debut"));
                    project.setDateFin(rs.getDate("date_fin"));
                    project.setStatut(rs.getString("statut"));
                    return project;
                }
            }
        }
        return null;
    }

    public void createProject(Project project) throws SQLException {
        String query = "INSERT INTO projects (nom, description, date_debut, date_fin, statut) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, project.getNom());
            pstmt.setString(2, project.getDescription());
            pstmt.setDate(3, new java.sql.Date(project.getDateDebut().getTime()));
            pstmt.setDate(4, new java.sql.Date(project.getDateFin().getTime()));
            pstmt.setString(5, project.getStatut());
            pstmt.executeUpdate();
        }
    }

    public void updateProject(Project project) throws SQLException {
        String query = "UPDATE projects SET nom = ?, description = ?, date_debut = ?, date_fin = ?, statut = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, project.getNom());
            pstmt.setString(2, project.getDescription());
            pstmt.setDate(3, new java.sql.Date(project.getDateDebut().getTime()));
            pstmt.setDate(4, new java.sql.Date(project.getDateFin().getTime()));
            pstmt.setString(5, project.getStatut());
            pstmt.setInt(6, project.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProject(int id) throws SQLException {
        String query = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Task task = new Task();
                task.setId(rs.getInt("id"));
                task.setTitre(rs.getString("titre"));
                task.setDescription(rs.getString("description"));
                task.setDateEcheance(rs.getDate("date_echeance"));
                task.setPriorite(rs.getString("priorite"));
                task.setStatut(rs.getString("statut"));
                task.setProjetId(rs.getInt("projet_id"));
                tasks.add(task);
            }
        }
        return tasks;
    }

    public List<Task> getTasksByProject(int projectId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE projet_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitre(rs.getString("titre"));
                    task.setDescription(rs.getString("description"));
                    task.setDateEcheance(rs.getDate("date_echeance"));
                    task.setPriorite(rs.getString("priorite"));
                    task.setStatut(rs.getString("statut"));
                    task.setProjetId(rs.getInt("projet_id"));
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public Task getTaskById(int id) throws SQLException {
        String query = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setTitre(rs.getString("titre"));
                    task.setDescription(rs.getString("description"));
                    task.setDateEcheance(rs.getDate("date_echeance"));
                    task.setPriorite(rs.getString("priorite"));
                    task.setStatut(rs.getString("statut"));
                    task.setProjetId(rs.getInt("projet_id"));
                    return task;
                }
            }
        }
        return null;
    }

    public void createTask(Task task) throws SQLException {
        String query = "INSERT INTO tasks (titre, description, date_echeance, priorite, statut, projet_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, task.getTitre());
            pstmt.setString(2, task.getDescription());
            pstmt.setDate(3, new java.sql.Date(task.getDateEcheance().getTime()));
            pstmt.setString(4, task.getPriorite());
            pstmt.setString(5, task.getStatut());
            pstmt.setInt(6, task.getProjetId());
            pstmt.executeUpdate();
        }
    }

    public void updateTask(Task task) throws SQLException {
        String query = "UPDATE tasks SET titre = ?, description = ?, date_echeance = ?, priorite = ?, statut = ?, projet_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, task.getTitre());
            pstmt.setString(2, task.getDescription());
            pstmt.setDate(3, new java.sql.Date(task.getDateEcheance().getTime()));
            pstmt.setString(4, task.getPriorite());
            pstmt.setString(5, task.getStatut());
            pstmt.setInt(6, task.getProjetId());
            pstmt.setInt(7, task.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTask(int id) throws SQLException {
        String query = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Méthodes pour les utilisateurs
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.*, r.nom as role_nom, r.description as role_description FROM users u LEFT JOIN roles r ON u.role_id = r.id";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Role role = new Role(
                    rs.getInt("role_id"),
                    rs.getString("role_nom"),
                    rs.getString("role_description")
                );
                
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("email"),
                    rs.getString("mot_de_passe"),
                    role
                );
                users.add(user);
            }
        }
        return users;
    }

    public User authenticateUser(String email, String password) throws SQLException {
        String query = "SELECT u.*, r.nom as role_nom, r.description as role_description FROM users u LEFT JOIN roles r ON u.role_id = r.id WHERE u.email = ? AND u.mot_de_passe = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password); // Note: Dans une vraie application, le mot de passe devrait être haché
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_nom"),
                        rs.getString("role_description")
                    );
                    
                    return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        role
                    );
                }
            }
        }
        return null;
    }

    // Méthodes pour les assignations
    public List<User> getAvailableUsersForTask(int taskId) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.*, r.nom as role_nom, r.description as role_description FROM users u " +
                      "LEFT JOIN roles r ON u.role_id = r.id " +
                      "WHERE u.id NOT IN (SELECT user_id FROM task_assignments WHERE task_id = ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role(
                        rs.getInt("role_id"),
                        rs.getString("role_nom"),
                        rs.getString("role_description")
                    );
                    
                    User user = new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        role
                    );
                    users.add(user);
                }
            }
        }
        return users;
    }

    public void assignUserToTask(int userId, int taskId) throws SQLException {
        String query = "INSERT INTO task_assignments (user_id, task_id, date_assignation) VALUES (?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        }
    }

    public void removeTaskAssignment(int taskId, String userName) throws SQLException {
        String query = "DELETE FROM task_assignments WHERE task_id = ? AND user_id = (SELECT id FROM users WHERE nom = ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        }
    }

    public List<Object[]> getTaskAssignments(int taskId) throws SQLException {
        List<Object[]> assignments = new ArrayList<>();
        String query = "SELECT t.titre, p.nom as projet_nom, u.nom as user_nom, ta.date_assignation " +
                      "FROM task_assignments ta " +
                      "JOIN tasks t ON ta.task_id = t.id " +
                      "JOIN projects p ON t.projet_id = p.id " +
                      "JOIN users u ON ta.user_id = u.id " +
                      "WHERE ta.task_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, taskId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] assignment = {
                        rs.getString("titre"),
                        rs.getString("projet_nom"),
                        rs.getString("user_nom"),
                        rs.getTimestamp("date_assignation")
                    };
                    assignments.add(assignment);
                }
            }
        }
        return assignments;
    }

    // Méthodes pour les statistiques
    public Map<String, Integer> getTaskStatusStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT statut, COUNT(*) as count FROM tasks GROUP BY statut";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stats.put(rs.getString("statut"), rs.getInt("count"));
            }
        }
        return stats;
    }

    public Map<String, Integer> getUserTaskStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT u.nom, COUNT(ta.task_id) as count " +
                      "FROM users u " +
                      "LEFT JOIN task_assignments ta ON u.id = ta.user_id " +
                      "GROUP BY u.id, u.nom";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stats.put(rs.getString("nom"), rs.getInt("count"));
            }
        }
        return stats;
    }

    public int getGlobalCompletionRate() throws SQLException {
        String query = "SELECT (COUNT(CASE WHEN statut = 'TERMINEE' THEN 1 END) * 100 / COUNT(*)) as completion_rate FROM tasks";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("completion_rate");
            }
        }
        return 0;
    }

    public int getTotalTasksCount() throws SQLException {
        String query = "SELECT COUNT(*) as total FROM tasks";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public int getLateTasksCount() throws SQLException {
        String query = "SELECT COUNT(*) as late FROM tasks WHERE date_echeance < CURRENT_DATE AND statut != 'TERMINEE'";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("late");
            }
        }
        return 0;
    }

    public Object[][] getProjectStatistics() throws SQLException {
        List<Object[]> stats = new ArrayList<>();
        String query = "SELECT p.nom, " +
                      "COUNT(t.id) as total_tasks, " +
                      "COUNT(CASE WHEN t.statut = 'TERMINEE' THEN 1 END) as completed_tasks, " +
                      "COUNT(CASE WHEN t.date_echeance < CURRENT_DATE AND t.statut != 'TERMINEE' THEN 1 END) as late_tasks, " +
                      "(COUNT(CASE WHEN t.statut = 'TERMINEE' THEN 1 END) * 100 / COUNT(t.id)) as completion_rate " +
                      "FROM projects p " +
                      "LEFT JOIN tasks t ON p.id = t.projet_id " +
                      "GROUP BY p.id, p.nom";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("nom"),
                    rs.getInt("total_tasks"),
                    rs.getInt("completed_tasks"),
                    rs.getInt("late_tasks"),
                    rs.getInt("completion_rate") + "%"
                };
                stats.add(row);
            }
        }
        return stats.toArray(new Object[0][]);
    }

    // Méthodes pour les rôles
    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM roles";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Role role = new Role(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description")
                );
                roles.add(role);
            }
        }
        return roles;
    }

    public Role getRoleById(int id) throws SQLException {
        String query = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Role(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description")
                    );
                }
            }
        }
        return null;
    }

    public void createRole(Role role) throws SQLException {
        String query = "INSERT INTO roles (nom, description) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, role.getNom());
            pstmt.setString(2, role.getDescription());
            pstmt.executeUpdate();
        }
    }

    public void updateRole(Role role) throws SQLException {
        String query = "UPDATE roles SET nom = ?, description = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, role.getNom());
            pstmt.setString(2, role.getDescription());
            pstmt.setInt(3, role.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteRole(int id) throws SQLException {
        String query = "DELETE FROM roles WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
