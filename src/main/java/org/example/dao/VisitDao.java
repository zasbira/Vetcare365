package org.example.dao;

import org.example.entites.Visite;
import org.example.entites.Pets;
import org.example.interfacee.IVisitimp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitDao implements IVisitimp {

    private static Connection connection;
    private String url = "jdbc:mysql://localhost:3306/db";
    private String user = "root";
    private String pass = "";

    // Constructor to initialize the connection
    public VisitDao() {
        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connexion réussie.");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }

    }

    // Create a new visit in the database
    public boolean createVisite(Visite visite) {
        String query = "INSERT INTO visites (date, description, pet_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, visite.getDate());  // Using int for date
            ps.setString(2, visite.getDescription());

            // Null check for pet
            if (visite.getPet() != null) {
                ps.setInt(3, visite.getPet().getId());  // Pass the pet's ID to the query
            } else {
                // Handle the case when the pet is null, maybe throw an exception or log an error
                System.out.println("No associated pet found for the visit.");
                return false;
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
            return false;
        }
    }

    // Update an existing visit in the database
    public boolean updateVisite(int id, Visite visite) {
        String query = "UPDATE visites SET date = ?, description = ?, pet_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, visite.getDate());  // Using int for date
            ps.setString(2, visite.getDescription());

            // Null check for pet
            if (visite.getPet() != null) {
                ps.setInt(3, visite.getPet().getId());  // Pass the pet's ID to the query
            } else {
                // Handle the case when the pet is null
                System.out.println("No associated pet found for the visit.");
                return false;
            }

            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur modification : " + e.getMessage());
            return false;
        }
    }

    // Delete a visit from the database
    public boolean deleteVisite(int id) {
        String query = "DELETE FROM visites WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur suppression : " + e.getMessage());
            return false;
        }
    }

    // Get a visit by its ID, along with the associated pet
    public Visite getVisiteById(int id) {
        String query = "SELECT v.id, v.date, v.description, v.pet_id, p.name AS pet_name, p.date_nais, p.type, p.owner_id " +
                "FROM visites v " +
                "JOIN pets p ON v.pet_id = p.id WHERE v.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Convert the int date to java.sql.Date
                    int dateInt = rs.getInt("date");
                    java.sql.Date date = new java.sql.Date(dateInt);  // Convert from int to Date

                    // Create the Pets object with the correct owner_id
                    Pets pet = new Pets(
                            rs.getInt("pet_id"),
                            rs.getString("pet_name"),
                            rs.getInt("date_nais"),
                            rs.getInt("owner_id"), // Correct owner_id here
                            rs.getString("type")
                    );

                    // Check if pet is null (though it shouldn't be, since it's retrieved from the database)
                    if (pet != null) {
                        // Create and return the Visite object with the associated Pets
                        return new Visite(
                                rs.getInt("id"),
                                dateInt,  // Use the converted Date
                                rs.getString("description"),
                                pet,
                                rs.getInt("pet_id")
                        );
                    } else {
                        // Handle case where pet is null (this should ideally never happen in your current setup)
                        System.out.println("No pet found for this visit.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche ID : " + e.getMessage());
        }
        return null;
    }

    // Get all visits from the database, along with the associated pets
    public List<Visite> getAllVisites() {
        List<Visite> visites = new ArrayList<>();
        String query = "SELECT v.id, v.date, v.description, v.pet_id, p.name AS pet_name, p.date_nais, p.type, p.owner_id " +
                "FROM visites v " +
                "JOIN pets p ON v.pet_id = p.id";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Convert the int date to java.sql.Date
                int dateInt = rs.getInt("date");
                java.sql.Date date = new java.sql.Date(dateInt);  // Convert from int to Date

                // Create the Pets object with the correct owner_id
                Pets pet = new Pets(
                        rs.getInt("pet_id"),
                        rs.getString("pet_name"),
                        rs.getInt("date_nais"),
                        rs.getInt("owner_id"), // Correct owner_id here
                        rs.getString("type")
                );

                // Check if pet is null (though it shouldn't be)
                if (pet != null) {
                    // Create and add the Visite object with the associated Pets
                    Visite visite = new Visite(
                            rs.getInt("id"),
                            dateInt,  // Use the converted Date
                            rs.getString("description"),
                            pet,
                            rs.getInt("pet_id")
                    );
                    visites.add(visite);
                } else {
                    // Handle case where pet is null (this should ideally never happen)
                    System.out.println("No pet found for this visit.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur récupération : " + e.getMessage());
        }
        return visites;
    }

    // Find visits by pet ID
    public List<Visite> findVisitesByPetId(int petId) {
        List<Visite> visites = new ArrayList<>();
        String query = "SELECT v.id, v.date, v.description, v.pet_id, p.name AS pet_name, p.date_nais, p.type, p.owner_id " +
                "FROM visites v " +
                "JOIN pets p ON v.pet_id = p.id WHERE v.pet_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, petId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Create the Pets object with the correct owner_id
                    Pets pet = new Pets(
                            rs.getInt("pet_id"),
                            rs.getString("pet_name"),
                            rs.getInt("date_nais"),
                            rs.getInt("owner_id"),
                            rs.getString("type")
                    );

                    // Check if pet is null
                    if (pet != null) {
                        // Create and add the Visite object with the associated Pets
                        Visite visite = new Visite(
                                rs.getInt("id"),
                                rs.getInt("date"),  // Use the converted Date
                                rs.getString("description"),
                                pet,
                                rs.getInt("pet_id")
                        );
                        visites.add(visite);
                    } else {
                        // Handle case where pet is null (this should ideally never happen)
                        System.out.println("No pet found for this visit.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche visites par ID de pet : " + e.getMessage());
        }
        return visites;
    }

    // Implementing methods from IVisitimp interface
    @Override
    public boolean ajouterVisite(Visite visite) {
        return createVisite(visite);
    }

    @Override
    public boolean supprimerVisite(int id) {
        return deleteVisite(id);
    }

    @Override
    public boolean modifierVisite(Visite visite, int id) {
        return updateVisite(id, visite);
    }

    @Override
    public Pets getVisiteId(int id) {
        return null;  // Modify if you want to fetch a pet for the visit
    }

    @Override
    public List<Pets> getVisites() {
        return new ArrayList<>();  // Modify if needed
    }

    @Override
    public List<Pets> afficherVisiteNom(String nom) {
        return new ArrayList<>();  // Modify if needed
    }
}
