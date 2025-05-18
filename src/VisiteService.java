import models.Visite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle database operations for visites (visits)
 */
public class VisiteService {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";       // Change this to match your MySQL username
    private static final String DB_PASSWORD = "";  // Change this to match your MySQL password
    
    // SQL queries
    private static final String SELECT_ALL_VISITES = 
            "SELECT id, date, motif, diagnostic, traitement, animal_id, veterinaire_id FROM visites";
    private static final String SELECT_VISITE_BY_ID = 
            "SELECT id, date, motif, diagnostic, traitement, animal_id, veterinaire_id FROM visites WHERE id = ?";
    private static final String SELECT_VISITES_BY_ANIMAL = 
            "SELECT id, date, motif, diagnostic, traitement, animal_id, veterinaire_id FROM visites WHERE animal_id = ?";
    private static final String SELECT_VISITES_BY_VETERINAIRE = 
            "SELECT id, date, motif, diagnostic, traitement, animal_id, veterinaire_id FROM visites WHERE veterinaire_id = ?";
    private static final String SELECT_VISITES_BY_DATE = 
            "SELECT id, date, motif, diagnostic, traitement, animal_id, veterinaire_id FROM visites WHERE date = ?";
    private static final String INSERT_VISITE = 
            "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_VISITE = 
            "UPDATE visites SET date = ?, motif = ?, diagnostic = ?, traitement = ?, animal_id = ?, veterinaire_id = ? WHERE id = ?";
    private static final String DELETE_VISITE = 
            "DELETE FROM visites WHERE id = ?";
    
    /**
     * Initialize the database table if it doesn't exist
     */
    public void initializeDatabase() {
        String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS visites (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  date DATE NOT NULL," +
                "  motif VARCHAR(100) NOT NULL," +
                "  diagnostic TEXT," +
                "  traitement TEXT," +
                "  animal_id INT," +
                "  veterinaire_id INT," +
                "  FOREIGN KEY (animal_id) REFERENCES animaux(id) ON DELETE CASCADE," +
                "  FOREIGN KEY (veterinaire_id) REFERENCES veterinaires(id) ON DELETE SET NULL" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create the table if it doesn't exist
            stmt.execute(createTableSQL);
            
            // Check if there's any data, if not add sample data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // First check if we have animaux and veterinaires to reference
                ResultSet rsAnimaux = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
                rsAnimaux.next();
                int animalCount = rsAnimaux.getInt(1);
                
                ResultSet rsVeterinaires = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
                rsVeterinaires.next();
                int vetCount = rsVeterinaires.getInt(1);
                
                if (animalCount > 0 && vetCount > 0) {
                    // Insert sample data (make sure to reference existing animal_id and veterinaire_id)
                    String insertSampleSQL = "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES " +
                                            "('2025-05-01', 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1)," +
                                            "('2025-05-02', 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1)," +
                                            "('2025-05-03', 'Contrôle dentaire', 'Tartre important', 'Détartrage programmé', 5, 1)," +
                                            "('2025-05-10', 'Troubles digestifs', 'Gastrite légère', 'Régime spécial 2 semaines', 3, 1)," +
                                            "('2025-05-15', 'Boiterie patte avant', 'Entorse légère', 'Repos et anti-inflammatoires', 4, 1)";
                    stmt.execute(insertSampleSQL);
                    System.out.println("Sample visites data added to database");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all visites from the database
     */
    public ObservableList<Visite> getAllVisites() {
        // Initialize the database if needed
        initializeDatabase();
        
        // Get data from database
        List<Visite> visites = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_VISITES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Visite visite = new Visite(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("motif"),
                    rs.getString("diagnostic"),
                    rs.getString("traitement"),
                    rs.getInt("animal_id"),
                    rs.getInt("veterinaire_id")
                );
                visites.add(visite);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching visites: " + e.getMessage());
            
            // If database access fails, return sample data as fallback
            LocalDate today = LocalDate.now();
            visites.add(new Visite(1, today.minusDays(17), "Vaccination annuelle", "Animal en bonne santé", "Vaccin polyvalent", 1, 1));
            visites.add(new Visite(2, today.minusDays(16), "Consultation de suivi", "Légère infection", "Antibiotiques 7 jours", 2, 1));
            visites.add(new Visite(3, today.minusDays(15), "Contrôle dentaire", "Tartre important", "Détartrage programmé", 5, 1));
            visites.add(new Visite(4, today.minusDays(8), "Troubles digestifs", "Gastrite légère", "Régime spécial 2 semaines", 3, 1));
            visites.add(new Visite(5, today.minusDays(3), "Boiterie patte avant", "Entorse légère", "Repos et anti-inflammatoires", 4, 1));
        }
        
        return FXCollections.observableArrayList(visites);
    }
    
    /**
     * Get a visite by ID
     */
    public Visite getVisiteById(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_VISITE_BY_ID)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Visite(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("motif"),
                    rs.getString("diagnostic"),
                    rs.getString("traitement"),
                    rs.getInt("animal_id"),
                    rs.getInt("veterinaire_id")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching visite by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get visites by animal ID
     */
    public ObservableList<Visite> getVisitesByAnimal(int animalId) {
        List<Visite> visites = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_VISITES_BY_ANIMAL)) {
            
            stmt.setInt(1, animalId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Visite visite = new Visite(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("motif"),
                    rs.getString("diagnostic"),
                    rs.getString("traitement"),
                    rs.getInt("animal_id"),
                    rs.getInt("veterinaire_id")
                );
                visites.add(visite);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching visites by animal: " + e.getMessage());
        }
        
        return FXCollections.observableArrayList(visites);
    }
    
    /**
     * Get visites by veterinaire ID
     */
    public ObservableList<Visite> getVisitesByVeterinaire(int veterinaireId) {
        List<Visite> visites = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_VISITES_BY_VETERINAIRE)) {
            
            stmt.setInt(1, veterinaireId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Visite visite = new Visite(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("motif"),
                    rs.getString("diagnostic"),
                    rs.getString("traitement"),
                    rs.getInt("animal_id"),
                    rs.getInt("veterinaire_id")
                );
                visites.add(visite);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching visites by veterinaire: " + e.getMessage());
        }
        
        return FXCollections.observableArrayList(visites);
    }
    
    /**
     * Get visites by date
     */
    public ObservableList<Visite> getVisitesByDate(LocalDate date) {
        List<Visite> visites = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_VISITES_BY_DATE)) {
            
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Visite visite = new Visite(
                    rs.getInt("id"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("motif"),
                    rs.getString("diagnostic"),
                    rs.getString("traitement"),
                    rs.getInt("animal_id"),
                    rs.getInt("veterinaire_id")
                );
                visites.add(visite);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching visites by date: " + e.getMessage());
        }
        
        return FXCollections.observableArrayList(visites);
    }
    
    /**
     * Add a new visite
     */
    public boolean addVisite(Visite visite) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(INSERT_VISITE, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(visite.getDate()));
            stmt.setString(2, visite.getMotif());
            stmt.setString(3, visite.getDiagnostic());
            stmt.setString(4, visite.getTraitement());
            stmt.setInt(5, visite.getAnimalId());
            stmt.setInt(6, visite.getVeterinaireId());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get the generated ID and set it to the visite object
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Added visite with ID: " + id);
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding visite: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing visite
     */
    public boolean updateVisite(Visite visite) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(UPDATE_VISITE)) {
            
            stmt.setDate(1, Date.valueOf(visite.getDate()));
            stmt.setString(2, visite.getMotif());
            stmt.setString(3, visite.getDiagnostic());
            stmt.setString(4, visite.getTraitement());
            stmt.setInt(5, visite.getAnimalId());
            stmt.setInt(6, visite.getVeterinaireId());
            stmt.setInt(7, visite.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating visite: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a visite
     */
    public boolean deleteVisite(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(DELETE_VISITE)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting visite: " + e.getMessage());
            return false;
        }
    }
}
