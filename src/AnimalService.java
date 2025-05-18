import models.Animal;
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
 * Service class to handle database operations for animals (pets)
 */
public class AnimalService {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";       // Change this to match your MySQL username
    private static final String DB_PASSWORD = "";  // Change this to match your MySQL password
    
    // SQL queries
    private static final String SELECT_ALL_ANIMALS = 
            "SELECT id, nom, espece, sexe, age, proprietaire_id FROM animaux";
    private static final String SELECT_ANIMAL_BY_ID = 
            "SELECT id, nom, espece, sexe, age, proprietaire_id FROM animaux WHERE id = ?";
    private static final String SELECT_ANIMALS_BY_PROPRIETAIRE = 
            "SELECT id, nom, espece, sexe, age, proprietaire_id FROM animaux WHERE proprietaire_id = ?";
    private static final String INSERT_ANIMAL = 
            "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_ANIMAL = 
            "UPDATE animaux SET nom = ?, espece = ?, sexe = ?, age = ? WHERE id = ?";
    private static final String DELETE_ANIMAL = 
            "DELETE FROM animaux WHERE id = ?";
    
    /**
     * Initialize the database table if it doesn't exist
     */
    public void initializeDatabase() {
        String createTableSQL = 
                "CREATE TABLE IF NOT EXISTS animaux (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nom VARCHAR(50) NOT NULL," +
                "  espece VARCHAR(50) NOT NULL," +
                "  sexe VARCHAR(20)," +
                "  age INT," +
                "  proprietaire_id INT," +
                "  FOREIGN KEY (proprietaire_id) REFERENCES proprietaires(id) ON DELETE CASCADE" +
                ")";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create the table if it doesn't exist
            stmt.execute(createTableSQL);
            
            // Check if there's any data, if not add sample data
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // First check if we have proprietaires to reference
                rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
                rs.next();
                int propCount = rs.getInt(1);
                
                if (propCount > 0) {
                    // Insert sample data (associate with the first 3 proprietaires)
                    String insertSampleSQL = "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES " +
                                            "('Rex', 'Chien', 'Mâle', 5, 1)," +
                                            "('Minette', 'Chat', 'Femelle', 3, 1)," +
                                            "('Nemo', 'Poisson', 'Mâle', 1, 2)," +
                                            "('Coco', 'Oiseau', 'Mâle', 2, 3)," +
                                            "('Bella', 'Chat', 'Femelle', 4, 2)";
                    stmt.execute(insertSampleSQL);
                    System.out.println("Sample animaux data added to database");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all animals from the database
     */
    public ObservableList<Animal> getAllAnimals() {
        // Initialize the database if needed
        initializeDatabase();
        
        // Get data from database
        List<Animal> animals = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ANIMALS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Animal animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("espece"),
                    rs.getString("sexe"),
                    rs.getInt("age"),
                    rs.getInt("proprietaire_id")
                );
                animals.add(animal);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching animals: " + e.getMessage());
            
            // If database access fails, return sample data as fallback
            animals.add(new Animal(1, "Rex", "Chien", "Mâle", 5, 1));
            animals.add(new Animal(2, "Minette", "Chat", "Femelle", 3, 1));
            animals.add(new Animal(3, "Nemo", "Poisson", "Mâle", 1, 2));
            animals.add(new Animal(4, "Coco", "Oiseau", "Mâle", 2, 3));
            animals.add(new Animal(5, "Bella", "Chat", "Femelle", 4, 2));
        }
        
        return FXCollections.observableArrayList(animals);
    }
    
    /**
     * Get an animal by ID
     */
    public Animal getAnimalById(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ANIMAL_BY_ID)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Animal(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("espece"),
                    rs.getString("sexe"),
                    rs.getInt("age"),
                    rs.getInt("proprietaire_id")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching animal by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get animals by proprietaire ID
     */
    public ObservableList<Animal> getAnimalsByProprietaire(int proprietaireId) {
        List<Animal> animals = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ANIMALS_BY_PROPRIETAIRE)) {
            
            stmt.setInt(1, proprietaireId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Animal animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("espece"),
                    rs.getString("sexe"),
                    rs.getInt("age"),
                    rs.getInt("proprietaire_id")
                );
                animals.add(animal);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching animals by proprietaire: " + e.getMessage());
        }
        
        return FXCollections.observableArrayList(animals);
    }
    
    /**
     * Add a new animal
     */
    public boolean addAnimal(Animal animal) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(INSERT_ANIMAL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, animal.getNom());
            stmt.setString(2, animal.getEspece());
            stmt.setString(3, animal.getSexe());
            stmt.setInt(4, animal.getAge());
            stmt.setInt(5, animal.getProprietaireId());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get the generated ID and set it to the animal object
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Added animal with ID: " + id);
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding animal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update an existing animal
     */
    public boolean updateAnimal(Animal animal) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ANIMAL)) {
            
            stmt.setString(1, animal.getNom());
            stmt.setString(2, animal.getEspece());
            stmt.setString(3, animal.getSexe());
            stmt.setInt(4, animal.getAge());
            stmt.setInt(5, animal.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating animal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an animal
     */
    public boolean deleteAnimal(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(DELETE_ANIMAL)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting animal: " + e.getMessage());
            return false;
        }
    }
}
