package taskflow.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private int id;
    private String nom;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String statut;
    private List<Task> tasks;
    private User owner;

    public Project() {
        this.tasks = new ArrayList<>();
    }

    public Project(int id, String nom, String description, Date dateDebut, Date dateFin, String statut, User owner) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.owner = owner;
        this.tasks = new ArrayList<>();
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

    public List<Task> getTasks() {
        return tasks;
    }

    public User getOwner() {
        return owner;
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

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    // Méthodes de gestion des tâches
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", statut='" + statut + '\'' +
                ", owner=" + (owner != null ? owner.getNom() : "null") +
                ", tasks=" + tasks.size() +
                '}';
    }
} 