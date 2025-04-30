package taskflow.ui.entities;

import java.util.Date;

public class TaskFlowEntities {
    public static class Role {
        private int id;
        private String nom;
        private String description;
        
        public Role() {}
        
        public Role(int id, String nom, String description) {
            this.id = id;
            this.nom = nom;
            this.description = description;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class User {
        private int id;
        private String nom;
        private String email;
        private String motDePasse;
        private Role role;
        
        public User() {}
        
        public User(int id, String nom, String email, String motDePasse, Role role) {
            this.id = id;
            this.nom = nom;
            this.email = email;
            this.motDePasse = motDePasse;
            this.role = role;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getMotDePasse() { return motDePasse; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
        
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }

    public static class Project {
        private int id;
        private String nom;
        private String description;
        private Date dateDebut;
        private Date dateFin;
        private String statut;
        
        public Project() {}
        
        public Project(int id, String nom, String description, Date dateDebut, Date dateFin, String statut) {
            this.id = id;
            this.nom = nom;
            this.description = description;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.statut = statut;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Date getDateDebut() { return dateDebut; }
        public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
        
        public Date getDateFin() { return dateFin; }
        public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
        
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }

    public static class Task {
        private int id;
        private String titre;
        private String description;
        private Date dateEcheance;
        private String priorite;
        private String statut;
        private int projetId;
        
        public Task() {}
        
        public Task(int id, String titre, String description, Date dateEcheance, String priorite, String statut, int projetId) {
            this.id = id;
            this.titre = titre;
            this.description = description;
            this.dateEcheance = dateEcheance;
            this.priorite = priorite;
            this.statut = statut;
            this.projetId = projetId;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Date getDateEcheance() { return dateEcheance; }
        public void setDateEcheance(Date dateEcheance) { this.dateEcheance = dateEcheance; }
        
        public String getPriorite() { return priorite; }
        public void setPriorite(String priorite) { this.priorite = priorite; }
        
        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
        
        public int getProjetId() { return projetId; }
        public void setProjetId(int projetId) { this.projetId = projetId; }
    }

    public static class TaskUser {
        private int id;
        private Task tache;
        private User utilisateur;
        private Date dateAssignation;
        
        public TaskUser() {}
        
        public TaskUser(int id, Task tache, User utilisateur, Date dateAssignation) {
            this.id = id;
            this.tache = tache;
            this.utilisateur = utilisateur;
            this.dateAssignation = dateAssignation;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public Task getTache() { return tache; }
        public void setTache(Task tache) { this.tache = tache; }
        
        public User getUtilisateur() { return utilisateur; }
        public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
        
        public Date getDateAssignation() { return dateAssignation; }
        public void setDateAssignation(Date dateAssignation) { this.dateAssignation = dateAssignation; }
    }

    public static class HistoryAction {
        private int id;
        private User utilisateur;
        private Project projet;
        private Task tache;
        private String action;
        private Date dateAction;
        
        public HistoryAction() {}
        
        public HistoryAction(int id, User utilisateur, Project projet, Task tache, String action, Date dateAction) {
            this.id = id;
            this.utilisateur = utilisateur;
            this.projet = projet;
            this.tache = tache;
            this.action = action;
            this.dateAction = dateAction;
        }
        
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public User getUtilisateur() { return utilisateur; }
        public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }
        
        public Project getProjet() { return projet; }
        public void setProjet(Project projet) { this.projet = projet; }
        
        public Task getTache() { return tache; }
        public void setTache(Task tache) { this.tache = tache; }
        
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public Date getDateAction() { return dateAction; }
        public void setDateAction(Date dateAction) { this.dateAction = dateAction; }
    }
}