package taskflow.models;

import java.util.Date;

public class Task {
    private int id;
    private String nom;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String statut;
    private String priorite;
    private Project project;
    private User assignedUser;

    public Task() {
    }

    public Task(int id, String nom, String description, Date dateDebut, Date dateFin, 
                String statut, String priorite, Project project, User assignedUser) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.priorite = priorite;
        this.project = project;
        this.assignedUser = assignedUser;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public String getPriorite() {
        return priorite;
    }

    public Project getProject() {
        return project;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", priorite='" + priorite + '\'' +
                ", project=" + (project != null ? project.getNom() : "null") +
                ", assignedUser=" + (assignedUser != null ? assignedUser.getNom() : "null") +
                '}';
    }
} 