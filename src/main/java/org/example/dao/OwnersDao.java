package org.example.dao;

import org.example.entites.Owner;
import org.example.entites.Pets;
import org.example.interfacee.IOwnersimp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OwnersDao implements IOwnersimp {

    private static final Logger logger = Logger.getLogger(OwnersDao.class.getName());
    private Connection connection;
    private String url = "jdbc:mysql://localhost:3306/db";
    private String user = "root";
    private String pass = "";

    // Constructor to initialize the connection
    public OwnersDao() {
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

    // Create a new owner
    public boolean createOwner(Owner owner) {
        String query = "INSERT INTO owners (firstname, lastname, city, address, telephone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, owner.getFirstName());
            ps.setString(2, owner.getLastName());
            ps.setString(3, owner.getCity());
            ps.setString(4, owner.getAdress()); // Corrected 'adress' to 'address'
            ps.setInt(5, owner.getTelephone()); // Assuming 'telephone' is a string in SQL

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while adding the owner", e);
            System.out.println("Erreur lors de l'ajout du propriétaire.");
            return false;
        }
    }

    // Update an existing owner by their ID
    public boolean updateOwner(int id, Owner updatedOwner) {
        String query = "UPDATE owners SET firstname = ?, lastname = ?, city = ?, address = ?, telephone = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, updatedOwner.getFirstName());
            ps.setString(2, updatedOwner.getLastName());
            ps.setString(3, updatedOwner.getCity());
            ps.setString(4, updatedOwner.getAdress()); // Corrected 'adress' to 'address'
            ps.setInt(5, updatedOwner.getTelephone()); // Assuming 'telephone' is a string in SQL
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while updating the owner", e);
            System.out.println("Erreur lors de la mise à jour du propriétaire.");
            return false;
        }
    }

    // Delete an owner by their ID
    public boolean deleteOwner(int id) {
        // First, delete all pets associated with the owner
        String deletePetsQuery = "DELETE FROM pets WHERE owner_id = ?";
        try (PreparedStatement psPets = connection.prepareStatement(deletePetsQuery)) {
            psPets.setInt(1, id);
            psPets.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while deleting pets", e);
            System.out.println("Erreur lors de la suppression des animaux.");
            return false;
        }

        // Then, delete the owner
        String query = "DELETE FROM owners WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while deleting the owner", e);
            System.out.println("Erreur lors de la suppression du propriétaire.");
            return false;
        }
    }

    // Fetch all owners
    public List<Owner> getAllOwners() {
        List<Owner> owners = new ArrayList<>();
        String query = "SELECT * FROM owners";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Owner owner = new Owner(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("city"),
                        rs.getString("address"), // Corrected 'adress' to 'address'
                        rs.getInt("telephone"), // Assuming 'telephone' is a string in SQL
                        null // Placeholder for pets
                );
                // Load pets for this owner
                owner.setPets(loadPetsForOwner(owner.getId())); // Correctly set pets as a list
                owners.add(owner);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching all owners", e);
            System.out.println("Erreur lors de la récupération des propriétaires.");
        }
        return owners;
    }

    // Get an owner by ID
    public Owner getOwnerById(int id) {
        String query = "SELECT * FROM owners WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Owner owner = new Owner(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("city"),
                            rs.getString("address"), // Corrected 'adress' to 'address'
                            rs.getInt("telephone"), // Assuming 'telephone' is a string in SQL
                            null // Placeholder for pets
                    );
                    // Load pets for this owner
                    owner.setPets(loadPetsForOwner(owner.getId())); // Correctly set pets as a list
                    return owner;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching owner by ID", e);
            System.out.println("Erreur lors de la recherche par ID.");
        }
        return null;
    }

    // Find owners by name (either first name or last name)
    public List<Owner> findOwnersByName(String name) {
        List<Owner> owners = new ArrayList<>();
        String query = "SELECT * FROM owners WHERE firstname LIKE ? OR lastname LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Owner owner = new Owner(
                            rs.getInt("id"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("city"),
                            rs.getString("address"), // Corrected 'adress' to 'address'
                            rs.getInt("telephone"), // Assuming 'telephone' is a string in SQL
                            null // Placeholder for pets
                    );
                    // Load pets for this owner
                    owner.setPets(loadPetsForOwner(owner.getId())); // Correctly set pets as a list
                    owners.add(owner);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching owners by name", e);
            System.out.println("Erreur lors de la recherche par nom.");
        }
        return owners;
    }

    // Helper method to load pets for a given owner
    private List<Pets> loadPetsForOwner(int ownerId) {
        List<Pets> petsList = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE owner_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pets pet = new Pets(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("date_nais"),
                            rs.getInt("owner_id"),
                            rs.getString("type")
                    );
                    petsList.add(pet);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching pets for owner", e);
            System.out.println("Erreur lors de la récupération des animaux.");
        }
        return petsList;
    }

    @Override
    public boolean ajouterOwner(Owner et) {
        return false;
    }

    @Override
    public boolean supprimerOwner(int id) {
        return false;
    }

    @Override
    public boolean modifierOwnert(Owner et, int id) {
        return false;
    }

    @Override
    public Owner getOwnerId(int id) {
        return null;
    }

    @Override
    public List<Owner> getOwners() {
        return List.of();
    }

    @Override
    public List<Owner> afficherOwnersNom(String nom) {
        return List.of();
    }
}
