import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

/**
 * Simplified VetCare360 application with Bootstrap styling
 * Avoids package structure and resource loading issues
 */
public class VetCare360Final extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // UI components
    private BorderPane mainLayout;
    private TabPane tabPane;
    private Label statusLabel;
    
    // Tables
    private TableView<String[]> vetsTable;
    private TableView<String[]> ownersTable;
    private TableView<String[]> petsTable;
    private TableView<String[]> visitsTable;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            initializeDatabase();
            
            // Create main layout
            mainLayout = new BorderPane();
            
            // Create header
            HBox header = createHeader();
            mainLayout.setTop(header);
            
            // Create tab pane for main content
            createTabPane();
            mainLayout.setCenter(tabPane);
            
            // Create status bar
            HBox statusBar = createStatusBar();
            mainLayout.setBottom(statusBar);
            
            // Load data
            loadAllData();
            
            // Set up the scene
            Scene scene = new Scene(mainLayout, 1200, 700);
            
            // Apply inline styles since we're avoiding resource loading
            applyStyles(scene);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Status message
            statusLabel.setText("Application démarrée - " + LocalDate.now());
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Initialize the database and create tables if needed
     */
    private void initializeDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First connect to MySQL server to create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS db");
            }
            
            // Now connect to the db database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Create veterinaires table if it doesn't exist
                String createVetsTable = 
                        "CREATE TABLE IF NOT EXISTS veterinaires (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  nom VARCHAR(50) NOT NULL," +
                        "  prenom VARCHAR(50) NOT NULL," +
                        "  specialite VARCHAR(100)," +
                        "  email VARCHAR(100)," +
                        "  telephone VARCHAR(20)" +
                        ")";
                stmt.execute(createVetsTable);
                
                // Create proprietaires table if it doesn't exist
                String createPropsTable = 
                        "CREATE TABLE IF NOT EXISTS proprietaires (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  nom VARCHAR(50) NOT NULL," +
                        "  prenom VARCHAR(50) NOT NULL," +
                        "  adresse VARCHAR(200)," +
                        "  telephone VARCHAR(20)" +
                        ")";
                stmt.execute(createPropsTable);
                
                // Create animaux table if it doesn't exist
                String createAnimauxTable = 
                        "CREATE TABLE IF NOT EXISTS animaux (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  nom VARCHAR(50) NOT NULL," +
                        "  espece VARCHAR(50) NOT NULL," +
                        "  sexe VARCHAR(20)," +
                        "  age INT," +
                        "  proprietaire_id INT," +
                        "  FOREIGN KEY (proprietaire_id) REFERENCES proprietaires(id) ON DELETE CASCADE" +
                        ")";
                stmt.execute(createAnimauxTable);
                
                // Create visites table if it doesn't exist
                String createVisitesTable = 
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
                stmt.execute(createVisitesTable);
                
                // Add sample data if tables are empty
                addSampleDataIfNeeded(conn);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }
    
    /**
     * Add sample data to tables if they are empty
     */
    private void addSampleDataIfNeeded(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Check veterinaires
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
        rs.next();
        if (rs.getInt(1) == 0) {
            // Add sample data
            String insertVets = 
                    "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                    "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), " +
                    "('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), " +
                    "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01')";
            stmt.execute(insertVets);
        }
        
        // Check proprietaires
        rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
        rs.next();
        if (rs.getInt(1) == 0) {
            // Add sample data
            String insertProps = 
                    "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES " +
                    "('Martin', 'Jean', '15 Rue des Fleurs, 75001 Paris', '01 23 45 67 89'), " +
                    "('Dubois', 'Marie', '27 Avenue Victor Hugo, 69002 Lyon', '04 56 78 90 12'), " +
                    "('Petit', 'Sophie', '8 Rue du Commerce, 44000 Nantes', '02 34 56 78 90')";
            stmt.execute(insertProps);
        }
        
        // Check animaux
        rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
        rs.next();
        if (rs.getInt(1) == 0) {
            // Add sample data
            String insertAnimaux = 
                    "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES " +
                    "('Rex', 'Chien', 'Mâle', 5, 1), " +
                    "('Minette', 'Chat', 'Femelle', 3, 1), " +
                    "('Nemo', 'Poisson', 'Mâle', 1, 2)";
            stmt.execute(insertAnimaux);
        }
        
        // Check visites
        rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
        rs.next();
        if (rs.getInt(1) == 0) {
            // Add sample data
            String insertVisites = 
                    "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES " +
                    "(CURDATE(), 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1), " +
                    "(CURDATE(), 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1)";
            stmt.execute(insertVisites);
        }
    }
    
    /**
     * Create the header with title
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #167e74;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Title
        Label titleLabel = new Label("VetCare 360");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Subtitle
        Label subtitleLabel = new Label("Gestion de Clinique Vétérinaire");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        // Put title and subtitle in a VBox
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    /**
     * Create status bar for the bottom of the layout
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #f0f0f0;");
        
        statusLabel = new Label("Prêt");
        statusLabel.setStyle("-fx-text-fill: #555555;");
        
        Label versionLabel = new Label("VetCare 360 v1.0");
        versionLabel.setStyle("-fx-text-fill: #555555;");
        HBox.setHgrow(versionLabel, Priority.ALWAYS);
        versionLabel.setAlignment(Pos.CENTER_RIGHT);
        
        statusBar.getChildren().addAll(statusLabel, versionLabel);
        
        return statusBar;
    }
    
    /**
     * Create the tab pane with all entity tabs
     */
    private void createTabPane() {
        tabPane = new TabPane();
        
        // Create tabs for each entity
        Tab dashboardTab = new Tab("📊 Tableau de bord");
        dashboardTab.setClosable(false);
        dashboardTab.setContent(createDashboardPane());
        
        Tab vetsTab = new Tab("🩺 Vétérinaires");
        vetsTab.setClosable(false);
        vetsTab.setContent(createVeterinarianPane());
        
        Tab ownersTab = new Tab("👤 Propriétaires");
        ownersTab.setClosable(false);
        ownersTab.setContent(createOwnerPane());
        
        Tab petsTab = new Tab("🐾 Animaux");
        petsTab.setClosable(false);
        petsTab.setContent(createAnimalPane());
        
        Tab visitsTab = new Tab("📅 Visites");
        visitsTab.setClosable(false);
        visitsTab.setContent(createVisitPane());
        
        // Add tabs to tab pane
        tabPane.getTabs().addAll(dashboardTab, vetsTab, ownersTab, petsTab, visitsTab);
        
        // Set tab change listener
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                statusLabel.setText("Page actuelle: " + newTab.getText());
            }
        });
    }
    
    /**
     * Create the dashboard pane
     */
    private Pane createDashboardPane() {
        VBox dashboardPane = new VBox(20);
        dashboardPane.setPadding(new Insets(20));
        dashboardPane.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Tableau de bord");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Welcome message
        Label welcomeLabel = new Label("Bienvenue dans VetCare 360");
        welcomeLabel.setStyle("-fx-font-size: 16px;");
        
        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20, 0, 20, 0));
        
        // Add statistics cards
        statsGrid.add(createStatCard("Vétérinaires", "0", "#4e73df"), 0, 0);
        statsGrid.add(createStatCard("Propriétaires", "0", "#1cc88a"), 1, 0);
        statsGrid.add(createStatCard("Animaux", "0", "#36b9cc"), 0, 1);
        statsGrid.add(createStatCard("Visites", "0", "#f6c23e"), 1, 1);
        
        dashboardPane.getChildren().addAll(titleLabel, welcomeLabel, statsGrid);
        
        return dashboardPane;
    }
    
    /**
     * Create a statistics card for the dashboard
     */
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5px; " +
                     "-fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); " +
                     "-fx-border-width: 0 0 0 4px; -fx-border-color: " + color + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        
        return card;
    }
    
    /**
     * Create the veterinarian management pane
     */
    private Pane createVeterinarianPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(15));
        pane.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Vétérinaires");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create table
        vetsTable = new TableView<>();
        vetsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        vetsTable.setPrefHeight(400);
        
        // Create action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#167e74");
        Button editButton = createActionButton("Modifier", "#4e73df");
        Button deleteButton = createActionButton("Supprimer", "#e74a3b");
        Button refreshButton = createActionButton("Actualiser", "#1cc88a");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        pane.getChildren().addAll(titleLabel, vetsTable, buttonBox);
        
        return pane;
    }
    
    /**
     * Create the owner management pane
     */
    private Pane createOwnerPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(15));
        pane.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Propriétaires");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create table
        ownersTable = new TableView<>();
        ownersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ownersTable.setPrefHeight(400);
        
        // Create action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#167e74");
        Button editButton = createActionButton("Modifier", "#4e73df");
        Button deleteButton = createActionButton("Supprimer", "#e74a3b");
        Button refreshButton = createActionButton("Actualiser", "#1cc88a");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        pane.getChildren().addAll(titleLabel, ownersTable, buttonBox);
        
        return pane;
    }
    
    /**
     * Create the animal management pane
     */
    private Pane createAnimalPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(15));
        pane.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Animaux");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create table
        petsTable = new TableView<>();
        petsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        petsTable.setPrefHeight(400);
        
        // Create action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#167e74");
        Button editButton = createActionButton("Modifier", "#4e73df");
        Button deleteButton = createActionButton("Supprimer", "#e74a3b");
        Button refreshButton = createActionButton("Actualiser", "#1cc88a");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        pane.getChildren().addAll(titleLabel, petsTable, buttonBox);
        
        return pane;
    }
    
    /**
     * Create the visit management pane
     */
    private Pane createVisitPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(15));
        pane.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Visites");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create table
        visitsTable = new TableView<>();
        visitsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        visitsTable.setPrefHeight(400);
        
        // Create action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#167e74");
        Button editButton = createActionButton("Modifier", "#4e73df");
        Button deleteButton = createActionButton("Supprimer", "#e74a3b");
        Button refreshButton = createActionButton("Actualiser", "#1cc88a");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        pane.getChildren().addAll(titleLabel, visitsTable, buttonBox);
        
        return pane;
    }
    
    /**
     * Create a styled action button
     */
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                      "-fx-background-radius: 20px; -fx-padding: 8px 16px;");
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: derive(" + color + ", -15%); -fx-text-fill: white; " +
                          "-fx-background-radius: 20px; -fx-padding: 8px 16px;");
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                          "-fx-background-radius: 20px; -fx-padding: 8px 16px;");
        });
        
        return button;
    }
    
    /**
     * Apply styles to the scene and components
     */
    private void applyStyles(Scene scene) {
        // Apply styles to tab pane
        tabPane.setStyle("-fx-background-color: #f8f9fa;");
        
        // Set tab styles
        for (Tab tab : tabPane.getTabs()) {
            // This would be better with CSS but we're avoiding resource loading
            tab.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #167e74;");
        }
    }
    
    /**
     * Load data for all tables
     */
    private void loadAllData() {
        // This method would populate all tables with data from the database
        // We're just setting up the UI for now
    }
    
    /**
     * Display an error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
