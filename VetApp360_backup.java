import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Simplified VetCare360 application that addresses package structure issues
 * All-in-one solution with minimal dependencies
 */
public class VetApp360 extends Application {
    
    // UI components
    private TabPane tabPane;
    private TableView<String[]> vetsTable, ownersTable, petsTable, visitsTable;
    
    // Database settings
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // UI components for sidebar navigation
    private StackPane contentArea;
    private Button homeBtn, ownersBtn, vetsBtn, animalsBtn, visitsBtn;
    private Label vetCountLabel, ownerCountLabel, animalCountLabel, visitCountLabel;
    private BarChart<String, Number> visitStatsChart, animalTypesChart;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            initializeDatabase();
            
            // Create the main layout
            BorderPane mainLayout = new BorderPane();
            
            // Create dark sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);
            
            // Create content area
            contentArea = new StackPane();
            contentArea.setStyle("-fx-background-color: #f0f5f5;");
            mainLayout.setCenter(contentArea);
            
            // Show dashboard as default
            showDashboard();
            
            // Create scene
            Scene scene = new Scene(mainLayout, 1200, 700);
            primaryStage.setTitle("VetCare 360");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Load statistics
            loadStatistics();
            
        } catch (Exception e) {
            showError("Error starting application", e);
        }
    }
    
    /**
     * Create sidebar with navigation buttons
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #1a2328;"); // Dark color from image
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        
        // Logo area
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Circle icon
        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(36, 36);
        iconCircle.setMaxSize(36, 36);
        iconCircle.setStyle("-fx-background-color: #20B2AA; -fx-background-radius: 18px;");
        
        Label iconLabel = new Label("V");
        iconLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        iconCircle.getChildren().add(iconLabel);
        
        // App title
        Label appTitle = new Label("VetCare 360");
        appTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        logoBox.getChildren().addAll(iconCircle, appTitle);
        
        // Create navigation buttons
        homeBtn = createNavButton("Accueil", true);
        homeBtn.setOnAction(e -> showDashboard());
        
        ownersBtn = createNavButton("Propriétaires", false);
        ownersBtn.setOnAction(e -> showOwners());
        
        vetsBtn = createNavButton("Vétérinaires", false);
        vetsBtn.setOnAction(e -> showVets());
        
        animalsBtn = createNavButton("Animaux", false);
        animalsBtn.setOnAction(e -> showAnimals());
        
        visitsBtn = createNavButton("Visites", false);
        visitsBtn.setOnAction(e -> showVisits());
        
        // Add navigation elements
        sidebar.getChildren().addAll(
            logoBox,
            new Separator(),
            homeBtn,
            ownersBtn,
            vetsBtn,
            animalsBtn,
            visitsBtn
        );
        
        return sidebar;
    }
    
    /**
     * Create a navigation button
     */
    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 15, 10, 15));
        
        if (isActive) {
            button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
        }
        
        // Hover effects
        button.setOnMouseEntered(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 5px;");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
            }
        });
        
        return button;
    }
    
    /**
     * Set the active navigation button
     */
    private void setActiveButton(Button activeButton) {
        // Reset all buttons
        for (Button btn : new Button[]{homeBtn, ownersBtn, vetsBtn, animalsBtn, visitsBtn}) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
            
            // Set mouse event handlers again
            btn.setOnMouseEntered(e -> {
                btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 5px;");
            });
            
            btn.setOnMouseExited(e -> {
                if (btn != activeButton) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
                }
            });
        }
        
        // Set active button
        activeButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
    }
    
    /**
     * Initialize the database with required tables
     */
    private void initializeDatabase() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Connect to MySQL server and create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("CREATE DATABASE IF NOT EXISTS db");
            }
            
            // Connect to the actual database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Create veterinaires table
                stmt.execute("CREATE TABLE IF NOT EXISTS veterinaires (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nom VARCHAR(50) NOT NULL," +
                        "prenom VARCHAR(50) NOT NULL," +
                        "specialite VARCHAR(100)," +
                        "email VARCHAR(100)," +
                        "telephone VARCHAR(20)" +
                        ")");
                
                // Create proprietaires table
                stmt.execute("CREATE TABLE IF NOT EXISTS proprietaires (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nom VARCHAR(50) NOT NULL," +
                        "prenom VARCHAR(50) NOT NULL," +
                        "adresse VARCHAR(200)," +
                        "telephone VARCHAR(20)" +
                        ")");
                
                // Create animaux table
                stmt.execute("CREATE TABLE IF NOT EXISTS animaux (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "nom VARCHAR(50) NOT NULL," +
                        "espece VARCHAR(50) NOT NULL," +
                        "sexe VARCHAR(20)," +
                        "age INT," +
                        "proprietaire_id INT," +
                        "FOREIGN KEY (proprietaire_id) REFERENCES proprietaires(id) ON DELETE CASCADE" +
                        ")");
                
                // Create visites table
                stmt.execute("CREATE TABLE IF NOT EXISTS visites (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "date DATE NOT NULL," +
                        "motif VARCHAR(200) NOT NULL," +
                        "diagnostic TEXT," +
                        "traitement TEXT," +
                        "animal_id INT," +
                        "veterinaire_id INT," +
                        "FOREIGN KEY (animal_id) REFERENCES animaux(id) ON DELETE CASCADE," +
                        "FOREIGN KEY (veterinaire_id) REFERENCES veterinaires(id) ON DELETE SET NULL" +
                        ")");
                
                // Add sample data if tables are empty
                addSampleData(conn);
            }
            
            System.out.println("Database initialization complete");
            
        } catch (Exception e) {
            showError("Database initialization error", e);
        }
    }
    
    /**
     * Add sample data if tables are empty
     */
    private void addSampleData(Connection conn) throws SQLException {
        // Check if veterinaires table is empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires")) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Adding sample veterinarians...");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES (?, ?, ?, ?, ?)")) {
                    
                    // Sample vet 1
                    pstmt.setString(1, "Martin");
                    pstmt.setString(2, "Sophie");
                    pstmt.setString(3, "Chirurgie");
                    pstmt.setString(4, "s.martin@vetcare.com");
                    pstmt.setString(5, "01 23 45 67 89");
                    pstmt.executeUpdate();
                    
                    // Sample vet 2
                    pstmt.setString(1, "Dubois");
                    pstmt.setString(2, "Pierre");
                    pstmt.setString(3, "Dermatologie");
                    pstmt.setString(4, "p.dubois@vetcare.com");
                    pstmt.setString(5, "01 98 76 54 32");
                    pstmt.executeUpdate();
                }
            }
        }
        
        // Check if proprietaires table is empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires")) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Adding sample owners...");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)")) {
                    
                    // Sample owner 1
                    pstmt.setString(1, "Leroy");
                    pstmt.setString(2, "Marie");
                    pstmt.setString(3, "15 Rue des Lilas, Paris");
                    pstmt.setString(4, "06 12 34 56 78");
                    pstmt.executeUpdate();
                    
                    // Sample owner 2
                    pstmt.setString(1, "Bernard");
                    pstmt.setString(2, "Jean");
                    pstmt.setString(3, "8 Avenue Victor Hugo, Lyon");
                    pstmt.setString(4, "07 65 43 21 09");
                    pstmt.executeUpdate();
                }
            }
        }
        
        // Add sample animals if table is empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux")) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Adding sample animals...");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES (?, ?, ?, ?, ?)")) {
                    
                    // Sample animal 1
                    pstmt.setString(1, "Rex");
                    pstmt.setString(2, "Chien");
                    pstmt.setString(3, "Mâle");
                    pstmt.setInt(4, 5);
                    pstmt.setInt(5, 1);
                    pstmt.executeUpdate();
                    
                    // Sample animal 2
                    pstmt.setString(1, "Félix");
                    pstmt.setString(2, "Chat");
                    pstmt.setString(3, "Mâle");
                    pstmt.setInt(4, 3);
                    pstmt.setInt(5, 2);
                    pstmt.executeUpdate();
                }
            }
        }
        
        // Add sample visits if table is empty
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM visites")) {
            
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Adding sample visits...");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES (?, ?, ?, ?, ?, ?)")) {
                    
                    // Sample visit 1
                    pstmt.setDate(1, Date.valueOf("2025-05-15"));
                    pstmt.setString(2, "Vaccination annuelle");
                    pstmt.setString(3, "Animal en bonne santé");
                    pstmt.setString(4, "Vaccin polyvalent");
                    pstmt.setInt(5, 1);
                    pstmt.setInt(6, 1);
                    pstmt.executeUpdate();
                    
                    // Sample visit 2
                    pstmt.setDate(1, Date.valueOf("2025-05-17"));
                    pstmt.setString(2, "Problème de peau");
                    pstmt.setString(3, "Dermatite allergique");
                    pstmt.setString(4, "Antihistaminiques et shampooing spécial");
                    pstmt.setInt(5, 2);
                    pstmt.setInt(6, 2);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    /**
            ")");
    
    // Create animaux table
    stmt.execute("CREATE TABLE IF NOT EXISTS animaux (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "nom VARCHAR(50) NOT NULL," +
            "espece VARCHAR(50) NOT NULL," +
            "sexe VARCHAR(20)," +
            "age INT," +
            "proprietaire_id INT," +
            "FOREIGN KEY (proprietaire_id) REFERENCES proprietaires(id) ON DELETE CASCADE" +
            ")");
     * Create a statistics card for the dashboard
     */
    private VBox createStatCard(String title, String count, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10); -fx-background-radius: 5px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        
        Label countLabel = new Label(count);
        countLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        countLabel.setId(title.toLowerCase() + "Count");
        
        card.getChildren().addAll(titleLabel, countLabel);
        
        return card;
    }
    
    /**
     * Create the veterinarians tab content
     */
    private Pane createVetsTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));
        tabContent.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Vétérinaires");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#20B2AA");
        Button editButton = createActionButton("Modifier", "#5F9EA0");
        Button deleteButton = createActionButton("Supprimer", "#FF6347");
        Button refreshButton = createActionButton("Actualiser", "#4682B4");
        
        toolbar.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        // Create table
        vetsTable = new TableView<>();
        vetsTable.setPrefHeight(500);
        
        // Set button actions
        addButton.setOnAction(e -> showAddVetDialog());
        refreshButton.setOnAction(e -> loadVeterinarians());
        
        tabContent.getChildren().addAll(titleLabel, toolbar, vetsTable);
        
        return tabContent;
    }
    
    /**
     * Create the owners tab content
     */
    private Pane createOwnersTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));
        tabContent.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Propriétaires");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#20B2AA");
        Button editButton = createActionButton("Modifier", "#5F9EA0");
        Button deleteButton = createActionButton("Supprimer", "#FF6347");
        Button refreshButton = createActionButton("Actualiser", "#4682B4");
        
        toolbar.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        // Create table
        ownersTable = new TableView<>();
        ownersTable.setPrefHeight(500);
        
        // Set button actions
        refreshButton.setOnAction(e -> loadOwners());
        
        tabContent.getChildren().addAll(titleLabel, toolbar, ownersTable);
        
        return tabContent;
    }
    
    /**
     * Create the pets tab content
     */
    private Pane createPetsTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));
        tabContent.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Animaux");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#20B2AA");
        Button editButton = createActionButton("Modifier", "#5F9EA0");
        Button deleteButton = createActionButton("Supprimer", "#FF6347");
        Button refreshButton = createActionButton("Actualiser", "#4682B4");
        
        toolbar.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        // Create table
        petsTable = new TableView<>();
        petsTable.setPrefHeight(500);
        
        // Set button actions
        refreshButton.setOnAction(e -> loadPets());
        
        tabContent.getChildren().addAll(titleLabel, toolbar, petsTable);
        
        return tabContent;
    }
    
    /**
     * Create the visits tab content
     */
    private Pane createVisitsTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));
        tabContent.setStyle("-fx-background-color: white;");
        
        Label titleLabel = new Label("Gestion des Visites");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create toolbar
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10, 0, 10, 0));
        
        Button addButton = createActionButton("Ajouter", "#20B2AA");
        Button editButton = createActionButton("Modifier", "#5F9EA0");
        Button deleteButton = createActionButton("Supprimer", "#FF6347");
        Button refreshButton = createActionButton("Actualiser", "#4682B4");
        
        toolbar.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        // Create table
        visitsTable = new TableView<>();
        visitsTable.setPrefHeight(500);
        
        // Set button actions
        refreshButton.setOnAction(e -> loadVisits());
        
        tabContent.getChildren().addAll(titleLabel, toolbar, visitsTable);
        
        return tabContent;
    }
    
    /**
     * Create a styled action button
     */
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
        button.setPadding(new Insets(8, 15, 8, 15));
        
        return button;
    }
    
    /**
     * Show dialog to add a new veterinarian
     */
    private void showAddVetDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un vétérinaire");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField specialiteField = new TextField();
        TextField emailField = new TextField();
        TextField telephoneField = new TextField();
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Spécialité:"), 0, 2);
        grid.add(specialiteField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(telephoneField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the first field
        nomField.requestFocus();
        
        // Convert the result to void when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                saveVeterinarian(nomField.getText(), prenomField.getText(), 
                               specialiteField.getText(), emailField.getText(), 
                               telephoneField.getText());
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    /**
     * Save a new veterinarian to the database
     */
    private void saveVeterinarian(String nom, String prenom, String specialite, String email, String telephone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES (?, ?, ?, ?, ?)")) {
            
            pstmt.setString(1, nom);
            pstmt.setString(2, prenom);
            pstmt.setString(3, specialite);
            pstmt.setString(4, email);
            pstmt.setString(5, telephone);
            
            pstmt.executeUpdate();
            
            showMessage("Vétérinaire ajouté avec succès");
            loadVeterinarians();
            
        } catch (SQLException e) {
            showError("Error adding veterinarian", e);
        }
    }
    
    /**
     * Load veterinarians from the database
     */
    private void loadVeterinarians() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM veterinaires")) {
            
            ObservableList<String[]> data = FXCollections.observableArrayList();
            
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = rs.getString("id");
                row[1] = rs.getString("nom");
                row[2] = rs.getString("prenom");
                row[3] = rs.getString("specialite");
                row[4] = rs.getString("email");
                row[5] = rs.getString("telephone");
                data.add(row);
            }
            
            // Update the dashboard count
            Label countLabel = (Label) ((VBox) ((HBox) ((VBox) tabPane.getTabs().get(0).getContent()).getChildren().get(1)).getChildren().get(0)).getChildren().get(1);
            countLabel.setText(Integer.toString(data.size()));
            
            // Set table data
            vetsTable.getItems().clear();
            vetsTable.getItems().addAll(data);
            
        } catch (SQLException e) {
            showError("Error loading veterinarians", e);
        }
    }
    
    /**
     * Load owners from the database
     */
    private void loadOwners() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM proprietaires")) {
            
            ObservableList<String[]> data = FXCollections.observableArrayList();
            
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("id");
                row[1] = rs.getString("nom");
                row[2] = rs.getString("prenom");
                row[3] = rs.getString("adresse");
                row[4] = rs.getString("telephone");
                data.add(row);
            }
            
            // Update the dashboard count
            Label countLabel = (Label) ((VBox) ((HBox) ((VBox) tabPane.getTabs().get(0).getContent()).getChildren().get(1)).getChildren().get(1)).getChildren().get(1);
            countLabel.setText(Integer.toString(data.size()));
            
            // Set table data
            ownersTable.getItems().clear();
            ownersTable.getItems().addAll(data);
            
        } catch (SQLException e) {
            showError("Error loading owners", e);
        }
    }
    
    /**
     * Load pets from the database
     */
    private void loadPets() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.*, p.nom as prop_nom, p.prenom as prop_prenom " +
                     "FROM animaux a LEFT JOIN proprietaires p ON a.proprietaire_id = p.id")) {
            
            ObservableList<String[]> data = FXCollections.observableArrayList();
            
            while (rs.next()) {
                String[] row = new String[7];
                row[0] = rs.getString("id");
                row[1] = rs.getString("nom");
                row[2] = rs.getString("espece");
                row[3] = rs.getString("sexe");
                row[4] = rs.getString("age");
                row[5] = rs.getString("proprietaire_id");
                
                String propName = rs.getString("prop_prenom") + " " + rs.getString("prop_nom");
                row[6] = propName;
                
                data.add(row);
            }
            
            // Update the dashboard count
            Label countLabel = (Label) ((VBox) ((HBox) ((VBox) tabPane.getTabs().get(0).getContent()).getChildren().get(1)).getChildren().get(2)).getChildren().get(1);
            countLabel.setText(Integer.toString(data.size()));
            
            // Set table data
            petsTable.getItems().clear();
            petsTable.getItems().addAll(data);
            
        } catch (SQLException e) {
            showError("Error loading pets", e);
        }
    }
    
    /**
     * Load visits from the database
     */
    private void loadVisits() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT v.*, a.nom as animal_nom, vt.nom as vet_nom, vt.prenom as vet_prenom " +
                     "FROM visites v " +
                     "LEFT JOIN animaux a ON v.animal_id = a.id " +
                     "LEFT JOIN veterinaires vt ON v.veterinaire_id = vt.id")) {
            
            ObservableList<String[]> data = FXCollections.observableArrayList();
            
            while (rs.next()) {
                String[] row = new String[8];
                row[0] = rs.getString("id");
                row[1] = rs.getString("date");
                row[2] = rs.getString("motif");
                row[3] = rs.getString("diagnostic");
                row[4] = rs.getString("traitement");
                row[5] = rs.getString("animal_id");
                row[6] = rs.getString("veterinaire_id");
                
                String vetName = rs.getString("vet_prenom") + " " + rs.getString("vet_nom");
                row[7] = vetName;
                
                data.add(row);
            }
            
            // Update the dashboard count
            Label countLabel = (Label) ((VBox) ((HBox) ((VBox) tabPane.getTabs().get(0).getContent()).getChildren().get(1)).getChildren().get(3)).getChildren().get(1);
            countLabel.setText(Integer.toString(data.size()));
            
            // Set table data
            visitsTable.getItems().clear();
            visitsTable.getItems().addAll(data);
            
        } catch (SQLException e) {
            showError("Error loading visits", e);
        }
    }
    
    /**
     * Show an error message dialog
     */
    private void showError(String title, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(e.getMessage());
        e.printStackTrace();
        alert.showAndWait();
    }
    
    /**
     * Show an information message dialog
     */
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
