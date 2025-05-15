package org.example.dao;

import org.example.entites.Pets;
import org.example.interfacee.IPetsimp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetsDao implements IPetsimp {

    private static Connection connection;
    private String url = "jdbc:mysql://localhost:3306/db";
    private String user = "root";
    private String pass = "";

    // Constructor to initialize the connection
    public PetsDao() {
        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion réussie.");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }

    }

    // Create a new pet in the database

    public static boolean createPet(Pets pet) {
        String query = "INSERT INTO pets (name, date_nais, owner_id, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, pet.getName());
            ps.setInt(2, pet.getDateNais());  // Using int for date
            ps.setInt(3, pet.getOwnerId());
            ps.setString(4, pet.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
            return false;
        }
    }

    // Update an existing pet in the database

    public static boolean updatePet(int id, Pets pet) {
        String query = "UPDATE pets SET name = ?, date_nais = ?, owner_id = ?, type = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, pet.getName());
            ps.setInt(2, pet.getDateNais());  // Using int for date
            ps.setInt(3, pet.getOwnerId());
            ps.setString(4, pet.getType());
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur modification : " + e.getMessage());
            return false;
        }
    }

    // Delete a pet from the database

    public static boolean deletePet(int id) {
        String query = "DELETE FROM pets WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
            return false;
        }
    }

    // Get all pets from the database

    public static List<Pets> getAllPets() {
        List<Pets> pets = new ArrayList<>();
        String query = "SELECT * FROM pets";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Pets pet = new Pets(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("date_nais"),  // Using int for date
                        rs.getInt("owner_id"),
                        rs.getString("type")
                );
                pets.add(pet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération : " + e.getMessage());
        }
        return pets;
    }

    // Get a pet by its ID

    public  static Pets getPetById(int id) {
        String query = "SELECT * FROM pets WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Pets(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("date_nais"),  // Using int for date
                        rs.getInt("owner_id"),
                        rs.getString("type")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche ID : " + e.getMessage());
        }
        return null;
    }

    // Find pets by their name

    public static List<Pets> findPetsByName(String name) {
        List<Pets> pets = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE name LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pets pet = new Pets(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("date_nais"),  // Using int for date
                        rs.getInt("owner_id"),
                        rs.getString("type")
                );
                pets.add(pet);
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche nom : " + e.getMessage());
        }
        return pets;
    }

    // Implementing methods from IPetsimp interface
    @Override
    public boolean ajouterPet(Pets pet) {
        return createPet(pet);
    }

    @Override
    public boolean supprimerPet(int id) {
        return deletePet(id);
    }

    @Override
    public boolean modifierPet(Pets pet, int id) {
        return updatePet(id, pet);
    }

    @Override
    public Pets getPetsId(int id) {
        return getPetById(id);
    }

    @Override
    public List<Pets> getPets() {
        return getAllPets();
    }

    @Override
    public List<Pets> afficherPetsNom(String nom) {
        return findPetsByName(nom);
    }
}
