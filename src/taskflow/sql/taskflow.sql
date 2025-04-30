-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 24 avr. 2025 à 10:37
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `taskflow`
--

-- --------------------------------------------------------

--
-- Structure de la table `admin`
--

DROP TABLE IF EXISTS `admin`;
CREATE TABLE IF NOT EXISTS `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `date_creation` datetime DEFAULT CURRENT_TIMESTAMP,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_admin_role` (`role_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `admin`
--

INSERT INTO `admin` (`id`, `nom`, `email`, `mot_de_passe`, `date_creation`, `role_id`) VALUES
(1, 'KAWAYA Gaston', 'admin@taskflow.com', 'admin123', '2025-04-24 12:08:04', 1);

-- --------------------------------------------------------

--
-- Structure de la table `historique_action`
--

DROP TABLE IF EXISTS `historique_action`;
CREATE TABLE IF NOT EXISTS `historique_action` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int DEFAULT NULL,
  `action` text NOT NULL,
  `date_action` datetime DEFAULT CURRENT_TIMESTAMP,
  `projet_id` int DEFAULT NULL,
  `tache_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `utilisateur_id` (`utilisateur_id`),
  KEY `projet_id` (`projet_id`),
  KEY `tache_id` (`tache_id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `historique_action`
--

INSERT INTO `historique_action` (`id`, `utilisateur_id`, `action`, `date_action`, `projet_id`, `tache_id`) VALUES
(1, 1, 'Création du projet Plateforme E-learning', '2025-04-01 08:00:00', 1, NULL),
(2, 2, 'Ajout de la tâche Conception de la base de données', '2025-04-02 09:00:00', 1, 1),
(3, 3, 'Début du développement front-end', '2025-04-10 10:30:00', 1, 2),
(4, 5, 'Ajout de la tâche Déploiement sur serveur', '2025-04-15 14:00:00', 2, 3),
(5, 4, 'Assignée à la rédaction de la documentation', '2025-04-16 11:20:00', 1, 4);

-- --------------------------------------------------------

--
-- Structure de la table `projects`
--

DROP TABLE IF EXISTS `projects`;
CREATE TABLE IF NOT EXISTS `projects` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `description` text,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'EN_COURS',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `projects`
--

INSERT INTO `projects` (`id`, `nom`, `description`, `date_debut`, `date_fin`, `statut`) VALUES
(1, 'Plateforme E-learning', 'Plateforme d’apprentissage en ligne pour le Togo', '2025-04-01', '2025-06-15', 'EN_COURS'),
(2, 'Système de Gestion RH', 'Système RH pour entreprises togolaises', '2025-03-20', '2025-05-30', 'EN_ATTENTE'),
(3, 'Développement Application Mobile', 'Développement d\'une application mobile pour la gestion des tâches.', '2023-01-01', '2023-12-31', 'EN_COURS'),
(4, 'Site Web E-commerce', 'Création d\'un site web e-commerce pour une entreprise locale.', '2023-02-15', '2023-11-30', 'TERMINE'),
(5, 'Refonte UI/UX', 'Refonte de l\'interface utilisateur et de l\'expérience utilisateur d\'une application existante.', '2023-03-01', '2023-09-30', 'EN_COURS'),
(6, 'Développement Application Mobile', 'Développement d\'une application mobile pour la gestion des tâches.', '2023-01-01', '2023-12-31', 'EN_COURS'),
(7, 'Site Web E-commerce', 'Création d\'un site web e-commerce pour une entreprise locale.', '2023-02-15', '2023-11-30', 'TERMINE'),
(8, 'Refonte UI/UX', 'Refonte de l\'interface utilisateur et de l\'expérience utilisateur d\'une application existante.', '2023-03-01', '2023-09-30', 'EN_COURS');

-- --------------------------------------------------------

--
-- Structure de la table `project_history`
--

DROP TABLE IF EXISTS `project_history`;
CREATE TABLE IF NOT EXISTS `project_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `project_name` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `action` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `project_history`
--

INSERT INTO `project_history` (`id`, `project_name`, `user_name`, `action`, `date`) VALUES
(1, 'Plateforme E-learning', 'Kossi Agbéko', 'Création du projet', '2025-04-01 08:00:00'),
(2, 'Plateforme E-learning', 'Akouélé Mensah', 'Ajout d’une tâche importante', '2025-04-02 09:00:00'),
(3, 'Plateforme E-learning', 'Yao Komlan', 'Début du développement front-end', '2025-04-10 10:30:00'),
(4, 'Système de Gestion RH', 'Kodjo Tchalla', 'Préparation pour le déploiement', '2025-04-15 14:00:00'),
(5, 'Plateforme E-learning', 'Adjovi Lawson', 'Tâche documentation assignée', '2025-04-16 11:20:00');

-- --------------------------------------------------------

--
-- Structure de la table `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nom` (`nom`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `roles`
--

INSERT INTO `roles` (`id`, `nom`, `description`) VALUES
(1, 'ADMIN', 'Administrateur du système'),
(2, 'CHEF_PROJET', 'Responsable de la gestion des projets'),
(3, 'COLLABORATEUR', 'Utilisateur exécutant les tâches'),
(4, 'GESTIONNAIRE', 'Gestionnaire de projet'),
(5, 'DEVELOPPEUR', 'Développeur de logiciel'),
(6, 'TESTEUR', 'Testeur de qualité'),
(7, 'DESIGNER', 'Designer UI/UX');

-- --------------------------------------------------------

--
-- Structure de la table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `description` text,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'A_FAIRE',
  `priorite` varchar(50) DEFAULT 'MOYENNE',
  `project_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_id` (`project_id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `tasks`
--

INSERT INTO `tasks` (`id`, `nom`, `description`, `date_debut`, `date_fin`, `statut`, `priorite`, `project_id`) VALUES
(1, 'Conception de la base de données', 'Modélisation de la base', '2025-04-02', '2025-04-10', 'A_FAIRE', 'HAUTE', 1),
(2, 'Développement du front-end', 'Interface apprenants', '2025-04-10', '2025-04-25', 'EN_COURS', 'MOYENNE', 1),
(3, 'Déploiement sur serveur', 'Mise en ligne du système', '2025-05-10', '2025-05-20', 'A_FAIRE', 'MOYENNE', 2),
(4, 'Rédaction documentation', 'Guide utilisateur', '2025-04-15', '2025-04-22', 'A_FAIRE', 'BASSE', 1),
(5, 'Conception de la base de données', 'Concevoir la base de données pour l\'application mobile.', '2023-01-10', '2023-01-20', 'TERMINE', 'HAUTE', 3),
(6, 'Développement du backend', 'Développer le backend de l\'application mobile.', '2023-02-01', '2023-03-01', 'EN_COURS', 'HAUTE', 3),
(7, 'Tests unitaires', 'Écrire et exécuter des tests unitaires pour le site web e-commerce.', '2023-03-15', '2023-04-15', 'TERMINE', 'MOYENNE', 4),
(8, 'Design des maquettes', 'Créer les maquettes pour la refonte UI/UX.', '2023-03-10', '2023-04-10', 'EN_COURS', 'HAUTE', 5);

-- --------------------------------------------------------

--
-- Structure de la table `task_assignments`
--

DROP TABLE IF EXISTS `task_assignments`;
CREATE TABLE IF NOT EXISTS `task_assignments` (
  `task_id` int NOT NULL,
  `user_id` int NOT NULL,
  `date_assignation` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`task_id`,`user_id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `task_assignments`
--

INSERT INTO `task_assignments` (`task_id`, `user_id`, `date_assignation`) VALUES
(1, 1, '2025-04-23 19:49:38'),
(2, 3, '2025-04-23 19:49:38'),
(3, 5, '2025-04-23 19:49:38'),
(4, 4, '2025-04-23 19:49:38'),
(1, 2, '2025-04-24 09:47:56');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `role_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `role_id` (`role_id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `nom`, `email`, `mot_de_passe`, `role_id`) VALUES
(1, 'Admin', 'admin@taskflow.com', 'admin123', 1),
(2, 'Kossi Agbéko', 'kossi.agbeko@mail.tg', 'pass123', 1),
(3, 'Akouélé Mensah', 'akouele.mensah@mail.tg', 'pass123', 2),
(4, 'Yao Komlan', 'yao.komlan@mail.tg', 'pass123', 3),
(5, 'Adjovi Lawson', 'adjovi.lawson@mail.tg', 'pass123', 3),
(6, 'Kodjo Tchalla', 'kodjo.tchalla@mail.tg', 'pass123', 2),
(7, 'Amévi Sodokin', 'amevi.sodokin@mail.tg', 'pass123', 3),
(8, 'Komi AMEGAN', 'komi.amegan@example.com', 'password1', 4),
(9, 'Afiwa KOSSI', 'afiwa.kossi@example.com', 'password2', 5),
(10, 'Kodjo AMEWOU', 'kodjo.amewou@example.com', 'password3', 6),
(11, 'Amah ADJOVI', 'amah.adjovi@example.com', 'password4', 7);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
