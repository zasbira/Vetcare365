import models.Veterinaire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle database operations for veterinarians
 */
public class VeterinaireService {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";       // Change this to match your MySQL username
    private static final String DB_PASSWORD = "";  // Change this to match your MySQL password
    
    // SQL queries
    private static final String SELECT_ALL_VETERINAIRES = 
            "SELECT id, nom, prenom, specialite, email, telephone FROM veterinaires";
    private static final String SELECT_VETERINAIRES_BY_SPECIALITE = 
            "SELECT id, nom, prenom, specialite, email, telephone FROM veterinaires WHERE specialite = ?";
    private static final String INSERT_VETERINAIRE = 
            "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_VETERINAIRE = 
            "UPDATE veterinaires SET nom = ?, prenom = ?, specialite = ?, email = ?, telephone = ? WHERE id = ?";
    private static final String DELETE_VETERINAIRE = 
            "DELETE FROM veterinaires WHERE id = ?";
    
    /**
     * Initialize the database table if it doesn't exist
     */
    public void initializeDatabase() {
        String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS veterinaires (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nom VARCHAR(50) NOT NULL," +
                "  prenom VARCHAR(50) NOT NULL," +
                "  specialite VARCHAR(100)," +
                "  email VARCHAR(100)," +
                "  telephone VARCHAR(20)" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create the table if it doesn't exist
            stmt.execute(createTableSQL);
            
            // Check if there's any data, if not add sample data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Insert sample data
                String insertSampleSQL = "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                                        "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89')," +
                                        "('Martin', 'Jean', 'Chirurgie', 'j.martin@vetcare360.fr', '01 23 45 67 90')," +
                                        "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 23 45 67 91')," +
                                        "('Petit', 'Pierre', 'Médecine générale', 'p.petit@vetcare360.fr', '01 23 45 67 92')," +
                                        "('Robert', 'Claire', 'Ophtalmologie', 'c.robert@vetcare360.fr', '01 23 45 67 93')";
                stmt.execute(insertSampleSQL);
                System.out.println("Sample veterinaires data added to database");
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all veterinarians from the database
     */
    public ObservableList<Veterinaire> getAllVeterinaires() {
        // Initialize the database if needed
        initializeDatabase();
        
        // Get data from database
        List<Veterinaire> veterinaires = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_VETERINAIRES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Veterinaire veterinaire = new Veterinaire(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialite"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                veterinaires.add(veterinaire);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching veterinaires: " + e.getMessage());
            
            // If database access fails, return sample data as fallback
            veterinaires.add(new Veterinaire(1, "Dubois", "Marie", "Médecine générale", "m.dubois@vetcare360.fr", "01 23 45 67 89"));
            veterinaires.add(new Veterinaire(2, "Martin", "Jean", "Chirurgie", "j.martin@vetcare360.fr", "01 23 45 67 90"));
            veterinaires.add(new Veterinaire(3, "Bernard", "Sophie", "Dermatologie", "s.bernard@vetcare360.fr", "01 23 45 67 91"));
            veterinaires.add(new Veterinaire(4, "Petit", "Pierre", "Médecine générale", "p.petit@vetcare360.fr", "01 23 45 67 92"));
            veterinaires.add(new Veterinaire(5, "Robert", "Claire", "Ophtalmologie", "c.robert@vetcare360.fr", "01 23 45 67 93"));
        }
        
        return FXCollections.observableArrayList(veterinaires);
    }
    
    /**
     * Get veterinarians filtered by specialty
     */
    public ObservableList<Veterinaire> getVeterinairesBySpecialite(String specialite) {
        // Return all veterinaires if no speciality is specified
        if (specialite == null || specialite.equals("Toutes les spécialités")) {
            return getAllVeterinaires();
        }
        
        List<Veterinaire> veterinaires = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_VETERINAIRES_BY_SPECIALITE)) {
            
            stmt.setString(1, specialite);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Veterinaire veterinaire = new Veterinaire(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("specialite"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                veterinaires.add(veterinaire);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching veterinaires by specialite: " + e.getMessage());
            
            // If database access fails, filter the sample data as fallback
            ObservableList<Veterinaire> allVeterinaires = getAllVeterinaires();
            for (Veterinaire v : allVeterinaires) {
                if (v.getSpecialite().equals(specialite)) {
                    veterinaires.add(v);
                }
            }
        }
        
        return FXCollections.observableArrayList(veterinaires);
    }
    
    /**
     * Add a new veterinarian
     */
    public boolean addVeterinaire(Veterinaire veterinaire) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(INSERT_VETERINAIRE)) {
            
            stmt.setString(1, veterinaire.getNom());
            stmt.setString(2, veterinaire.getPrenom());
            stmt.setString(3, veterinaire.getSpecialite());
            stmt.setString(4, veterinaire.getEmail());
            stmt.setString(5, veterinaire.getTelephone());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Added veterinarian: " + veterinaire.getPrenom() + " " + veterinaire.getNom());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding veterinaire: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing veterinarian
     */
    public boolean updateVeterinaire(Veterinaire veterinaire) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(UPDATE_VETERINAIRE)) {
            
            stmt.setString(1, veterinaire.getNom());
            stmt.setString(2, veterinaire.getPrenom());
            stmt.setString(3, veterinaire.getSpecialite());
            stmt.setString(4, veterinaire.getEmail());
            stmt.setString(5, veterinaire.getTelephone());
            stmt.setInt(6, veterinaire.getId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Updated veterinarian: " + veterinaire.getPrenom() + " " + veterinaire.getNom());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating veterinaire: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a veterinarian
     */
    public boolean deleteVeterinaire(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(DELETE_VETERINAIRE)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Deleted veterinarian with ID: " + id);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting veterinaire: " + e.getMessage());
            return false;
        }
    }
}
