package org.example.dao;

import org.example.entites.Pets;
import org.example.entites.Visite;
import org.example.entites.Viterinaire;
import org.example.interfacee.IViterinaireimp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViterinaireDao implements IViterinaireimp {

    private static final Logger logger = Logger.getLogger(ViterinaireDao.class.getName());
    private static Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/db";
    private final String user = "root";
    private final String pass = "";

    // Constructor to initialize the connection
    public ViterinaireDao() {
        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion à la base réussie.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur de connexion à la base de données", e);
            System.out.println("Échec de connexion à la base de données.");
        }
    }

    // Create a new veterinarian
    public boolean createViterinaire(Viterinaire viterinaire) {
        String query = "INSERT INTO viterinaire (name, specialty) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, viterinaire.getName());
            ps.setString(2, viterinaire.getSpecialty());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout du vétérinaire", e);
            return false;
        }
    }

    // Update an existing veterinarian
    public boolean updateViterinaire(int id, Viterinaire updatedViterinaire) {
        String query = "UPDATE viterinaire SET name = ?, specialty = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, updatedViterinaire.getName());
            ps.setString(2, updatedViterinaire.getSpecialty());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la mise à jour du vétérinaire", e);
            return false;
        }
    }

    // Delete a veterinarian
    public boolean deleteViterinaire(int id) {
        String query = "DELETE FROM viterinaire WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression du vétérinaire", e);
            return false;
        }
    }

    // Get all veterinarians
    public static List<Viterinaire> getAllViterinaires() {
        List<Viterinaire> viterinaires = new ArrayList<>();
        String query = "SELECT * FROM viterinaire";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Viterinaire viterinaire = new Viterinaire(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialty")
                );
                viterinaires.add(viterinaire);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des vétérinaires", e);
        }
        return viterinaires;
    }

    // Get veterinarian by ID
    public Viterinaire getViterinaireById(int id) {
        String query = "SELECT * FROM viterinaire WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Viterinaire(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("specialty")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du vétérinaire par ID", e);
        }
        return null;
    }

    // Search veterinarians by name
    public List<Viterinaire> findViterinairesByName(String name) {
        List<Viterinaire> viterinaires = new ArrayList<>();
        String query = "SELECT * FROM viterinaire WHERE name LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Viterinaire viterinaire = new Viterinaire(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("specialty")
                    );
                    viterinaires.add(viterinaire);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la recherche de vétérinaires par nom", e);
        }
        return viterinaires;
    }

    // === Implémentation de l'interface IViterinaireimp ===

    @Override
    public boolean ajouterViterinaire(Visite an) {
        // À personnaliser selon le besoin (ici, fausse implémentation)
        return false;
    }

    @Override
    public boolean supprimerViterinaire(int id) {
        return deleteViterinaire(id);
    }

    @Override
    public boolean modifierViterinaire(Visite an, int id) {
        // À personnaliser si Visite est liée à Viterinaire
        return false;
    }

    @Override
    public Pets getViterinaireId(int id) {
        // À implémenter si nécessaire
        return null;
    }

    @Override
    public List<Pets> getViterinaires() {
        // À implémenter si nécessaire
        return new ArrayList<>();
    }

    @Override
    public List<Pets> afficherViterinaireNom(String nom) {
        // À implémenter si nécessaire
        return new ArrayList<>();
    }
}
