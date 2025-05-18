import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Simple standalone application to view veterinarians from the database
 */
public class VetApp extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // UI components
    private TableView<Vet> table = new TableView<>();
    private Label statusLabel = new Label("Status: Ready");
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up the main layout
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));
            
            // Create title
            Label titleLabel = new Label("VetCare 360 - Liste des Vétérinaires");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #008080;");
            
            // Initialize database
            initializeDatabase();
            
            // Set up table columns
            TableColumn<Vet, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            TableColumn<Vet, String> lastNameCol = new TableColumn<>("Nom");
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
            
            TableColumn<Vet, String> firstNameCol = new TableColumn<>("Prénom");
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            
            TableColumn<Vet, String> specialtyCol = new TableColumn<>("Spécialité");
            specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
            
            TableColumn<Vet, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            
            TableColumn<Vet, String> phoneCol = new TableColumn<>("Téléphone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
            
            table.getColumns().addAll(idCol, lastNameCol, firstNameCol, specialtyCol, emailCol, phoneCol);
            
            // Load data
            loadVeterinaires();
            
            // Set column widths
            idCol.setPrefWidth(50);
            lastNameCol.setPrefWidth(150);
            firstNameCol.setPrefWidth(150);
            specialtyCol.setPrefWidth(200);
            emailCol.setPrefWidth(200);
            phoneCol.setPrefWidth(150);
            
            // Create buttons
            Button refreshButton = new Button("Rafraîchir");
            refreshButton.setOnAction(e -> loadVeterinaires());
            
            // Add components to layout
            VBox topBox = new VBox(10, titleLabel);
            topBox.setPadding(new Insets(0, 0, 10, 0));
            
            VBox bottomBox = new VBox(10, refreshButton, statusLabel);
            bottomBox.setPadding(new Insets(10, 0, 0, 0));
            
            root.setTop(topBox);
            root.setCenter(table);
            root.setBottom(bottomBox);
            
            // Set up the scene
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css") != null ? 
                    getClass().getResource("/resources/css/style.css").toExternalForm() : "");
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Initialize the database with tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First connect to MySQL server to create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("CREATE DATABASE IF NOT EXISTS db");
                statusLabel.setText("Status: Database 'db' verified");
            }
            
            // Now connect to the db database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Create veterinaires table if it doesn't exist
                String createTableSQL = 
                        "CREATE TABLE IF NOT EXISTS veterinaires (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  nom VARCHAR(50) NOT NULL," +
                        "  prenom VARCHAR(50) NOT NULL," +
                        "  specialite VARCHAR(100)," +
                        "  email VARCHAR(100)," +
                        "  telephone VARCHAR(20)" +
                        ")";
                
                stmt.execute(createTableSQL);
                statusLabel.setText("Status: Table 'veterinaires' verified");
                
                // Check if table is empty
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
                rs.next();
                int count = rs.getInt(1);
                
                if (count == 0) {
                    // Add sample data
                    String insertSampleSQL = 
                            "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                            "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), " +
                            "('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), " +
                            "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01'), " +
                            "('Robert', 'Thomas', 'Cardiologie', 't.robert@vetcare360.fr', '01 56 78 90 12'), " +
                            "('Petit', 'Julie', 'Ophtalmologie', 'j.petit@vetcare360.fr', '01 67 89 01 23')";
                    
                    stmt.execute(insertSampleSQL);
                    statusLabel.setText("Status: Sample data added to 'veterinaires' table");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }
    
    /**
     * Load veterinarians from the database
     */
    private void loadVeterinaires() {
        try {
            ObservableList<Vet> vets = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM veterinaires")) {
                
                while (rs.next()) {
                    Vet vet = new Vet(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getString("email"),
                            rs.getString("telephone")
                    );
                    vets.add(vet);
                }
            }
            
            // Update the table
            table.setItems(vets);
            statusLabel.setText("Status: Loaded " + vets.size() + " veterinaires");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading data: " + e.getMessage());
        }
    }
    
    /**
     * Show an error message
     */
    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Simple class to represent a veterinarian
     */
    public static class Vet {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String specialite;
        private final String email;
        private final String telephone;
        
        public Vet(int id, String nom, String prenom, String specialite, String email, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.specialite = specialite;
            this.email = email;
            this.telephone = telephone;
        }
        
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getSpecialite() { return specialite; }
        public String getEmail() { return email; }
        public String getTelephone() { return telephone; }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
