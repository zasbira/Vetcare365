import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Standalone version of VetCare360 application that doesn't rely on package structure
 */
public class StandaloneApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Test database connection first
        testDatabaseConnection();
        
        // Load the main view from a file directly
        File fxmlFile = new File("src/views/home.fxml");
        System.out.println("Loading FXML from: " + fxmlFile.getAbsolutePath());
        
        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();
        
        // Set up the primary stage
        primaryStage.setTitle("VetCare 360 - Gestion Clinique Vétérinaire");
        Scene scene = new Scene(root, 1000, 700);
        
        // Load CSS directly from file
        File cssFile = new File("resources/css/style.css");
        if (cssFile.exists()) {
            System.out.println("Loading CSS from: " + cssFile.getAbsolutePath());
            scene.getStylesheets().add(cssFile.toURI().toURL().toExternalForm());
        } else {
            System.out.println("CSS file not found at: " + cssFile.getAbsolutePath());
        }
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
        
        System.out.println("VetCare360 application window displayed!");
    }
    
    /**
     * Test database connection and setup
     */
    private void testDatabaseConnection() {
        System.out.println("Testing database connection...");
        
        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully");
            
            // Connect to MySQL server
            String url = "jdbc:mysql://localhost:3306/";
            String user = "root";
            String password = "";
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL server");
            
            // Create database if it doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS db");
            conn.close();
            
            // Connect to the specific database
            conn = DriverManager.getConnection(url + "db", user, password);
            System.out.println("Connected to 'db' database");
            
            // Create veterinaires table
            String createVetsTable = 
                "CREATE TABLE IF NOT EXISTS veterinaires (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nom VARCHAR(50) NOT NULL," +
                "  prenom VARCHAR(50) NOT NULL," +
                "  specialite VARCHAR(100)," +
                "  email VARCHAR(100)," +
                "  telephone VARCHAR(20)" +
                ")";
            
            stmt = conn.createStatement();
            stmt.execute(createVetsTable);
            System.out.println("Veterinaires table created or verified");
            
            // Check if veterinaires table is empty
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Add sample data
                String insertSampleVets = 
                    "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                    "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), " +
                    "('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), " +
                    "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01')";
                
                stmt.execute(insertSampleVets);
                System.out.println("Sample veterinarian data added");
            }
            
            // Create proprietaires table
            String createOwnersTable = 
                "CREATE TABLE IF NOT EXISTS proprietaires (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  nom VARCHAR(50) NOT NULL," +
                "  prenom VARCHAR(50) NOT NULL," +
                "  adresse VARCHAR(200)," +
                "  telephone VARCHAR(20)" +
                ")";
            
            stmt.execute(createOwnersTable);
            System.out.println("Proprietaires table created or verified");
            
            // Close connection
            conn.close();
            System.out.println("Database connection test completed successfully");
            
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting VetCare360 standalone application...");
        launch(args);
    }
}
