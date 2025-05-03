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
    private String url = "jdbc:mysql://localhost:3306/db";
    private String user = "root";
    private String pass = "";

    // Constructor to initialize the connection
    public ViterinaireDao() {
        try {
            connection = DriverManager.getConnection(url, user, pass); // Initialize connection here
            if (connection != null) {
                System.out.println("Database connection established successfully.");
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to the database", e); // Log the error
            System.out.println("Erreur de connexion à la base de données. Veuillez vérifier votre configuration.");
        }
    }

    // Create a new Viterinaire
    public boolean createViterinaire(Viterinaire viterinaire) {
        String query = "INSERT INTO viterinaire (name, specialty) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, viterinaire.getName());
            ps.setString(2, viterinaire.getSpecialty());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while adding the viterinaire", e); // Log the error
            System.out.println("Erreur lors de l'ajout du vétérinaire.");
            return false;
        }
    }

    // Update an existing Viterinaire by their ID
    public boolean updateViterinaire(int id, Viterinaire updatedViterinaire) {
        String query = "UPDATE viterinaire SET name = ?, specialty = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, updatedViterinaire.getName());
            ps.setString(2, updatedViterinaire.getSpecialty());
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while updating the viterinaire", e); // Log the error
            System.out.println("Erreur lors de la mise à jour du vétérinaire.");
            return false;
        }
    }

    // Delete a Viterinaire by their ID
    public boolean deleteViterinaire(int id) {
        String query = "DELETE FROM viterinaire WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while deleting the viterinaire", e); // Log the error
            System.out.println("Erreur lors de la suppression du vétérinaire.");
            return false;
        }
    }

    // Fetch all veterinarians
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
            logger.log(Level.SEVERE, "Error while fetching all viterinaires", e); // Log the error
            System.out.println("Erreur lors de la récupération des vétérinaires.");
        }
        return viterinaires;
    }

    // Get a Viterinaire by ID
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
            logger.log(Level.SEVERE, "Error while fetching viterinaire by ID", e); // Log the error
            System.out.println("Erreur lors de la recherche par ID.");
        }
        return null;
    }

    // Find veterinarians by name (either first or last name)
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
            logger.log(Level.SEVERE, "Error while fetching viterinaires by name", e); // Log the error
            System.out.println("Erreur lors de la recherche par nom.");
        }
        return viterinaires;
    }

    @Override
    public boolean ajouterViterinaire(Visite an) {
        return false;
    }

    @Override
    public boolean supprimerViterinaire(int id) {
        return false;
    }

    @Override
    public boolean modifierViterinaire(Visite an, int id) {
        return false;
    }

    @Override
    public Pets getViterinaireId(int id) {
        return null;
    }

    @Override
    public List<Pets> getViterinaires() {
        return List.of();
    }

    @Override
    public List<Pets> afficherViterinaireNom(String nom) {
        return List.of();
    }
}
