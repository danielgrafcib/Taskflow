package taskflow;

import java.util.Date;

public class Entities {
    public static class Role {
        private int id;
        private String nom;
        private String description;

        // Getters and setters
    }

    public static class Utilisateur {
        private int id;
        private String nom;
        private String email;
        private String motDePasse;
        private Role role;

        // Getters and setters
    }

    public static class Projet {
        private int id;
        private String nom;
        private String description;
        private Date dateDebut;
        private Date dateFin;
        private String statut;

        // Getters and setters
    }

    public static class Tache {
        private int id;
        private String titre;
        private String description;
        private Date dateEcheance;
        private String priorite;
        private String statut;
        private Projet projet;

        // Getters and setters
    }

    public static class TacheUtilisateur {
        private int id;
        private Tache tache;
        private Utilisateur utilisateur;
        private Date dateAssignation;

        // Getters and setters
    }

    public static class HistoriqueAction {
        private int id;
        private Utilisateur utilisateur;
        private Projet projet;
        private Tache tache;
        private String action;
        private Date dateAction;

        // Getters and setters
    }
}