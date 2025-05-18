import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Diagnostic application that tests key components and reports issues
 */
public class TroubleshootApp extends Application {

    private TextArea logArea;
    
    @Override
    public void start(Stage primaryStage) {
        // Create UI
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20px;");
        
        Label titleLabel = new Label("VetCare360 Diagnostic Tool");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        logArea = new TextArea();
        logArea.setPrefHeight(500);
        logArea.setEditable(false);
        
        root.getChildren().addAll(titleLabel, logArea);
        
        // Create scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("VetCare360 Diagnostics");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Run diagnostics
        runDiagnostics();
    }
    
    private void runDiagnostics() {
        log("Starting diagnostics...");
        log("System Information:");
        log("  Java Version: " + System.getProperty("java.version"));
        log("  JavaFX Version: " + System.getProperty("javafx.version", "Unknown"));
        log("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Check JavaFX
        log("\nJavaFX Status:");
        log("  JavaFX UI is working correctly (this window is proof)");
        
        // Check Java Modules
        log("\nJava Module Information:");
        try {
            ModuleLayer.boot().modules().forEach(m -> log("  Module: " + m.getName()));
        } catch (Exception e) {
            log("  Error getting module information: " + e.getMessage());
        }
        
        // Test MySQL connection
        log("\nTesting MySQL Connectivity:");
        testMySQLConnection();
        
        // Check classpath
        log("\nClasspath Information:");
        log("  " + System.getProperty("java.class.path"));
        
        // Check for VetCare360 main class
        log("\nLooking for VetCare360 classes:");
        checkClassExists("VetCare360Final");
        checkClassExists("VetCare360Unified");
        checkClassExists("AnimalCRUD");
        checkClassExists("PropCRUD");
        checkClassExists("VetCRUD");
        checkClassExists("VisiteCRUD");
        
        log("\nDiagnostics complete. Please check the information above for potential issues.");
    }
    
    private void testMySQLConnection() {
        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            log("  MySQL Driver loaded successfully");
            
            // Test connection
            String dbUrl = "jdbc:mysql://localhost:3306/";
            Properties props = new Properties();
            props.setProperty("user", "root");
            props.setProperty("password", "");
            
            log("  Attempting to connect to MySQL server...");
            Connection conn = DriverManager.getConnection(dbUrl, props);
            log("  Connected to MySQL server successfully!");
            
            // Check for 'db' database
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE 'db'");
                if (rs.next()) {
                    log("  'db' database exists");
                    
                    // Test connection to 'db' database
                    Connection dbConn = DriverManager.getConnection(dbUrl + "db", props);
                    log("  Connected to 'db' database successfully!");
                    
                    // Check for tables
                    try (Statement dbStmt = dbConn.createStatement()) {
                        log("  Checking for required tables:");
                        checkTableExists(dbStmt, "veterinaires");
                        checkTableExists(dbStmt, "proprietaires");
                        checkTableExists(dbStmt, "animaux");
                        checkTableExists(dbStmt, "visites");
                    }
                    
                    dbConn.close();
                } else {
                    log("  'db' database does not exist - this will be created when the app runs");
                }
            }
            
            conn.close();
        } catch (Exception e) {
            log("  MySQL Connection Error: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void checkTableExists(Statement stmt, String tableName) {
        try {
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
            if (rs.next()) {
                log("    Table '" + tableName + "' exists");
            } else {
                log("    Table '" + tableName + "' does not exist - this will be created when the app runs");
            }
        } catch (Exception e) {
            log("    Error checking for table '" + tableName + "': " + e.getMessage());
        }
    }
    
    private void checkClassExists(String className) {
        try {
            Class.forName(className);
            log("  Found class: " + className);
        } catch (ClassNotFoundException e) {
            log("  Class not found: " + className);
        }
    }
    
    private void log(String message) {
        logArea.appendText(message + "\n");
        System.out.println(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
