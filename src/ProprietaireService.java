import models.Proprietaire;
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
 * Service class to handle database operations for proprietaires (owners)
 */
public class ProprietaireService {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";       // Change this to match your MySQL username
    private static final String DB_PASSWORD = "";  // Change this to match your MySQL password
    
    // SQL queries
    private static final String SELECT_ALL_PROPRIETAIRES = 
            "SELECT id, nom, prenom, adresse, telephone FROM proprietaires";
    private static final String SELECT_PROPRIETAIRE_BY_ID = 
            "SELECT id, nom, prenom, adresse, telephone FROM proprietaires WHERE id = ?";
    private static final String SELECT_PROPRIETAIRES_BY_NOM = 
            "SELECT id, nom, prenom, adresse, telephone FROM proprietaires WHERE nom LIKE ?";
    private static final String INSERT_PROPRIETAIRE = 
            "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_PROPRIETAIRE = 
            "UPDATE proprietaires SET nom = ?, prenom = ?, adresse = ?, telephone = ? WHERE id = ?";
    private static final String DELETE_PROPRIETAIRE = 
            "DELETE FROM proprietaires WHERE id = ?";
    
    /**
     * Initialize the database table if it doesn't exist
     */
    public void initializeDatabase() {
        String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS proprietaires (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nom VARCHAR(50) NOT NULL," +
                "  prenom VARCHAR(50) NOT NULL," +
                "  adresse VARCHAR(200)," +
                "  telephone VARCHAR(20)" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create the table if it doesn't exist
            stmt.execute(createTableSQL);
            
            // Check if there's any data, if not add sample data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Insert sample data
                String insertSampleSQL = "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES " +
                                        "('Martin', 'Jean', '15 Rue des Fleurs, 75001 Paris', '01 23 45 67 89')," +
                                        "('Dubois', 'Marie', '27 Avenue Victor Hugo, 69002 Lyon', '04 56 78 90 12')," +
                                        "('Petit', 'Sophie', '8 Rue du Commerce, 44000 Nantes', '02 34 56 78 90')," +
                                        "('Bernard', 'Thomas', '5 Place de la Libération, 33000 Bordeaux', '05 67 89 01 23')," +
                                        "('Robert', 'Claire', '12 Boulevard des Alpes, 38000 Grenoble', '04 76 54 32 10')";
                stmt.execute(insertSampleSQL);
                System.out.println("Sample proprietaires data added to database");
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all proprietaires from the database
     */
    public ObservableList<Proprietaire> getAllProprietaires() {
        // Initialize the database if needed
        initializeDatabase();
        
        // Get data from database
        List<Proprietaire> proprietaires = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_PROPRIETAIRES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Proprietaire proprietaire = new Proprietaire(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
                proprietaires.add(proprietaire);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching proprietaires: " + e.getMessage());
            
            // If database access fails, return sample data as fallback
            proprietaires.add(new Proprietaire(1, "Martin", "Jean", "15 Rue des Fleurs, 75001 Paris", "01 23 45 67 89"));
            proprietaires.add(new Proprietaire(2, "Dubois", "Marie", "27 Avenue Victor Hugo, 69002 Lyon", "04 56 78 90 12"));
            proprietaires.add(new Proprietaire(3, "Petit", "Sophie", "8 Rue du Commerce, 44000 Nantes", "02 34 56 78 90"));
            proprietaires.add(new Proprietaire(4, "Bernard", "Thomas", "5 Place de la Libération, 33000 Bordeaux", "05 67 89 01 23"));
            proprietaires.add(new Proprietaire(5, "Robert", "Claire", "12 Boulevard des Alpes, 38000 Grenoble", "04 76 54 32 10"));
        }
        
        return FXCollections.observableArrayList(proprietaires);
    }
    
    /**
     * Get a proprietaire by ID
     */
    public Proprietaire getProprietaireById(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_PROPRIETAIRE_BY_ID)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Proprietaire(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching proprietaire by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Search proprietaires by name
     */
    public ObservableList<Proprietaire> searchProprietairesByNom(String nom) {
        List<Proprietaire> proprietaires = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_PROPRIETAIRES_BY_NOM)) {
            
            stmt.setString(1, "%" + nom + "%");  // Using LIKE for partial matches
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Proprietaire proprietaire = new Proprietaire(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adresse"),
                    rs.getString("telephone")
                );
                proprietaires.add(proprietaire);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching proprietaires: " + e.getMessage());
        }
        
        return FXCollections.observableArrayList(proprietaires);
    }
    
    /**
     * Add a new proprietaire
     */
    public boolean addProprietaire(Proprietaire proprietaire) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(INSERT_PROPRIETAIRE, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, proprietaire.getNom());
            stmt.setString(2, proprietaire.getPrenom());
            stmt.setString(3, proprietaire.getAdresse());
            stmt.setString(4, proprietaire.getTelephone());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get the generated ID and set it to the proprietaire object
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // Using reflection to set the ID (since it's a final field in SimpleIntegerProperty)
                    // In a real application, consider redesigning the model class to allow ID updates
                    System.out.println("Added proprietaire with ID: " + id);
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding proprietaire: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing proprietaire
     */
    public boolean updateProprietaire(Proprietaire proprietaire) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(UPDATE_PROPRIETAIRE)) {
            
            stmt.setString(1, proprietaire.getNom());
            stmt.setString(2, proprietaire.getPrenom());
            stmt.setString(3, proprietaire.getAdresse());
            stmt.setString(4, proprietaire.getTelephone());
            stmt.setInt(5, proprietaire.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating proprietaire: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a proprietaire
     */
    public boolean deleteProprietaire(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(DELETE_PROPRIETAIRE)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting proprietaire: " + e.getMessage());
            return false;
        }
    }
}
