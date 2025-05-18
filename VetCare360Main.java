import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Integrated VetCare360 application with Bootstrap-inspired UI
 */
public class VetCare360Main extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // UI components
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Label statusLabel;
    
    // Module panes
    private VBox dashboardPane;
    private VBox veterinarianPane;
    private VBox ownerPane;
    private VBox animalPane;
    private VBox visitPane;
    
    // Active button tracking
    private Button activeNavButton;
    
    // Statistics
    private int vetCount = 0;
    private int ownerCount = 0;
    private int animalCount = 0;
    private int visitCount = 0;

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
            
            // Create sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);
            
            // Create content area
            contentArea = new StackPane();
            contentArea.setPadding(new Insets(20));
            mainLayout.setCenter(contentArea);
            
            // Create status bar
            HBox statusBar = createStatusBar();
            mainLayout.setBottom(statusBar);
            
            // Initialize all module panes
            createDashboardPane();
            createVeterinarianPane();
            createOwnerPane();
            createAnimalPane();
            createVisitPane();
            
            // Show dashboard as default
            showDashboard();
            
            // Set up the scene with the Bootstrap-inspired CSS
            Scene scene = new Scene(mainLayout, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/css/bootstrap-style.css").toExternalForm());
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360");
            
            try {
                // Try to load logo if exists
                Image icon = new Image(new FileInputStream("resources/images/logo.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Logo not found, using default icon.");
            }
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
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
                addSampleDataIfNeeded(stmt);
                
                // Load statistics
                loadStatistics();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }
    
    /**
     * Add sample data to tables if they are empty
     */
    private void addSampleDataIfNeeded(Statement stmt) throws SQLException {
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
     * Load statistics for the dashboard
     */
    private void loadStatistics() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Get veterinarian count
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
            if (rs.next()) {
                vetCount = rs.getInt(1);
            }
            
            // Get owner count
            rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
            if (rs.next()) {
                ownerCount = rs.getInt(1);
            }
            
            // Get animal count
            rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
            if (rs.next()) {
                animalCount = rs.getInt(1);
            }
            
            // Get visit count
            rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
            if (rs.next()) {
                visitCount = rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading statistics: " + e.getMessage());
        }
    }
    
    /**
     * Create the header with logo and title
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.getStyleClass().add("navbar");
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Logo placeholder
        ImageView logoView = null;
        try {
            // Try to load logo if exists
            Image logo = new Image(new FileInputStream("resources/images/logo.png"), 40, 40, true, true);
            logoView = new ImageView(logo);
        } catch (Exception e) {
            // Create a placeholder if logo not found
            StackPane logoPlaceholder = new StackPane();
            logoPlaceholder.setPrefSize(40, 40);
            logoPlaceholder.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
            Label logoText = new Label("V360");
            logoText.getStyleClass().add("text-primary");
            logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            logoPlaceholder.getChildren().add(logoText);
            logoView = new ImageView();
        }
        
        // Title
        Label titleLabel = new Label("VetCare 360");
        titleLabel.getStyleClass().addAll("h2", "text-white");
        
        // Subtitle
        Label subtitleLabel = new Label("Gestion de Clinique Vétérinaire");
        subtitleLabel.getStyleClass().add("text-white");
        
        // Put title and subtitle in a VBox
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Add all to header
        if (logoView != null) {
            header.getChildren().addAll(logoView, titleBox);
        } else {
            header.getChildren().add(titleBox);
        }
        
        return header;
    }
    
    /**
     * Create the sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(250);
        
        // Dashboard button
        Button dashboardBtn = createNavButton("📊 Tableau de bord", e -> {
            updateActiveButton(dashboardBtn);
            showDashboard();
        });
        
        // Veterinarians button
        Button vetsBtn = createNavButton("🩺 Vétérinaires", e -> {
            updateActiveButton(vetsBtn);
            showVeterinarians();
        });
        
        // Owners button
        Button ownersBtn = createNavButton("👤 Propriétaires", e -> {
            updateActiveButton(ownersBtn);
            showOwners();
        });
        
        // Animals button
        Button animalsBtn = createNavButton("🐾 Animaux", e -> {
            updateActiveButton(animalsBtn);
            showAnimals();
        });
        
        // Visits button
        Button visitsBtn = createNavButton("📅 Visites", e -> {
            updateActiveButton(visitsBtn);
            showVisits();
        });
        
        // Add separator before settings
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        // Settings button
        Button settingsBtn = createNavButton("⚙️ Paramètres", e -> {
            showMessage("Fonctionnalité à venir", "Les paramètres seront disponibles dans une prochaine version.");
        });
        
        // Help button
        Button helpBtn = createNavButton("❓ Aide", e -> {
            showMessage("Aide", "VetCare 360 est une application de gestion pour cliniques vétérinaires.\n\n" +
                    "Utilisez la barre latérale pour naviguer entre les différentes sections.\n\n" +
                    "Pour obtenir plus d'informations, consultez la documentation ou contactez le support.");
        });
        
        // About button
        Button aboutBtn = createNavButton("ℹ️ À propos", e -> showAbout());
        
        sidebar.getChildren().addAll(
            dashboardBtn,
            vetsBtn, 
            ownersBtn, 
            animalsBtn, 
            visitsBtn, 
            separator,
            settingsBtn,
            helpBtn,
            aboutBtn
        );
        
        // Set dashboard as active by default
        updateActiveButton(dashboardBtn);
        
        return sidebar;
    }
    
    /**
     * Create a styled navigation button
     */
    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 15, 10, 15));
        button.setOnAction(handler);
        
        return button;
    }
    
    /**
     * Update the active navigation button
     */
    private void updateActiveButton(Button button) {
        if (activeNavButton != null) {
            activeNavButton.getStyleClass().remove("active");
        }
        button.getStyleClass().add("active");
        activeNavButton = button;
    }
    
    /**
     * Create status bar for the bottom of the layout
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #f0f0f0;");
        
        statusLabel = new Label("Prêt");
        statusLabel.setTextFill(Color.valueOf("#555555"));
        
        Label versionLabel = new Label("VetCare 360 v1.0");
        versionLabel.setTextFill(Color.valueOf("#555555"));
        HBox.setHgrow(versionLabel, Priority.ALWAYS);
        versionLabel.setAlignment(Pos.CENTER_RIGHT);
        
        statusBar.getChildren().addAll(statusLabel, versionLabel);
        
        return statusBar;
    }
    
    /**
     * Create the dashboard pane
     */
    private void createDashboardPane() {
        dashboardPane = new VBox(20);
        dashboardPane.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Tableau de bord");
        titleLabel.getStyleClass().add("h3");
        
        // Create statistics cards
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        
        // Vets card
        VBox vetsCard = createDashboardCard("🩺 Vétérinaires", String.valueOf(vetCount), "dash-card-vets");
        GridPane.setConstraints(vetsCard, 0, 0);
        
        // Owners card
        VBox ownersCard = createDashboardCard("👤 Propriétaires", String.valueOf(ownerCount), "dash-card-owners");
        GridPane.setConstraints(ownersCard, 1, 0);
        
        // Animals card
        VBox animalsCard = createDashboardCard("🐾 Animaux", String.valueOf(animalCount), "dash-card-pets");
        GridPane.setConstraints(animalsCard, 0, 1);
        
        // Visits card
        VBox visitsCard = createDashboardCard("📅 Visites", String.valueOf(visitCount), "dash-card-visits");
        GridPane.setConstraints(visitsCard, 1, 1);
        
        statsGrid.getChildren().addAll(vetsCard, ownersCard, animalsCard, visitsCard);
        
        // Recent activities section
        Label activitiesLabel = new Label("Activités récentes");
        activitiesLabel.getStyleClass().addAll("h4", "mt-4");
        
        VBox activitiesCard = new VBox(10);
        activitiesCard.getStyleClass().add("card");
        activitiesCard.setPadding(new Insets(15));
        
        Label activityTitle = new Label("Aujourd'hui");
        activityTitle.getStyleClass().add("dash-title");
        
        VBox activityList = new VBox(10);
        activityList.getChildren().addAll(
            createActivityItem("Visite: Vaccination annuelle pour Rex", "Marie Dubois", LocalDate.now().toString()),
            createActivityItem("Nouveau propriétaire: Sophie Petit", "Système", LocalDate.now().toString()),
            createActivityItem("Nouvel animal: Minette (Chat)", "Jean Martin", LocalDate.now().toString())
        );
        
        activitiesCard.getChildren().addAll(activityTitle, new Separator(), activityList);
        
        dashboardPane.getChildren().addAll(titleLabel, statsGrid, activitiesLabel, activitiesCard);
    }
    
    /**
     * Create a dashboard stat card
     */
    private VBox createDashboardCard(String title, String value, String styleClass) {
        VBox card = new VBox(5);
        card.getStyleClass().addAll("dash-card", styleClass);
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dash-title");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("dash-number");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        
        return card;
    }
    
    /**
     * Create an activity item for the dashboard
     */
    private HBox createActivityItem(String text, String user, String date) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Circle circle = new Circle(5);
        circle.setFill(Color.valueOf("#167e74"));
        
        VBox content = new VBox(3);
        Label activityText = new Label(text);
        activityText.setWrapText(true);
        
        Label metaText = new Label("Par " + user + " • " + date);
        metaText.getStyleClass().add("text-muted");
        metaText.setFont(Font.font("Segoe UI", 11));
        
        content.getChildren().addAll(activityText, metaText);
        HBox.setHgrow(content, Priority.ALWAYS);
        
        item.getChildren().addAll(circle, content);
        
        return item;
    }
    
    /**
     * Create the veterinarian management pane
     */
    private void createVeterinarianPane() {
        // Implementation for veterinarian pane will be added here
        // Similar to the VetCRUD implementation
        veterinarianPane = new VBox(20);
        veterinarianPane.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Gestion des Vétérinaires");
        titleLabel.getStyleClass().add("h3");
        
        // TODO: Add veterinarian management implementation
        
        veterinarianPane.getChildren().add(titleLabel);
    }
    
    /**
     * Create the owner management pane
     */
    private void createOwnerPane() {
        // Implementation for owner pane will be added here
        // Similar to the PropCRUD implementation
        ownerPane = new VBox(20);
        ownerPane.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Gestion des Propriétaires");
        titleLabel.getStyleClass().add("h3");
        
        // TODO: Add owner management implementation
        
        ownerPane.getChildren().add(titleLabel);
    }
    
    /**
     * Create the animal management pane
     */
    private void createAnimalPane() {
        // Implementation for animal pane will be added here
        // Similar to the AnimalCRUD implementation
        animalPane = new VBox(20);
        animalPane.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Gestion des Animaux");
        titleLabel.getStyleClass().add("h3");
        
        // TODO: Add animal management implementation
        
        animalPane.getChildren().add(titleLabel);
    }
    
    /**
     * Create the visit management pane
     */
    private void createVisitPane() {
        // Implementation for visit pane will be added here
        // Similar to the VisiteCRUD implementation
        visitPane = new VBox(20);
        visitPane.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Gestion des Visites");
        titleLabel.getStyleClass().add("h3");
        
        // TODO: Add visit management implementation
        
        visitPane.getChildren().add(titleLabel);
    }
    
    /**
     * Show the dashboard
     */
    private void showDashboard() {
        clearContentArea();
        contentArea.getChildren().add(dashboardPane);
        statusLabel.setText("Tableau de bord");
    }
    
    /**
     * Show the veterinarian management
     */
    private void showVeterinarians() {
        clearContentArea();
        contentArea.getChildren().add(veterinarianPane);
        statusLabel.setText("Gestion des vétérinaires");
    }
    
    /**
     * Show the owner management
     */
    private void showOwners() {
        clearContentArea();
        contentArea.getChildren().add(ownerPane);
        statusLabel.setText("Gestion des propriétaires");
    }
    
    /**
     * Show the animal management
     */
    private void showAnimals() {
        clearContentArea();
        contentArea.getChildren().add(animalPane);
        statusLabel.setText("Gestion des animaux");
    }
    
    /**
     * Show the visit management
     */
    private void showVisits() {
        clearContentArea();
        contentArea.getChildren().add(visitPane);
        statusLabel.setText("Gestion des visites");
    }
    
    /**
     * Clear the content area
     */
    private void clearContentArea() {
        contentArea.getChildren().clear();
    }
    
    /**
     * Show the about dialog
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos de VetCare 360");
        alert.setHeaderText("VetCare 360 - Système de Gestion Vétérinaire");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label versionLabel = new Label("Version: 1.0");
        Label descLabel = new Label("VetCare 360 est une application JavaFX moderne destinée aux cliniques vétérinaires pour gérer facilement les propriétaires d'animaux, les animaux, les vétérinaires et les visites médicales.");
        descLabel.setWrapText(true);
        
        Label techLabel = new Label("Technologies utilisées:");
        Label techDetailsLabel = new Label("• JavaFX (UI)\n• MySQL (Base de données)\n• CSS (Thème personnalisé inspiré Bootstrap)");
        
        Label copyrightLabel = new Label("© 2025 VetCare 360. Tous droits réservés.");
        copyrightLabel.setStyle("-fx-font-style: italic;");
        
        content.getChildren().addAll(versionLabel, descLabel, new Separator(), techLabel, techDetailsLabel, new Separator(), copyrightLabel);
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    
    /**
     * Show a message dialog
     */
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    
    /**
     * Circle shape class (since JavaFX Circle may not be directly accessible)
     */
    private static class Circle extends StackPane {
        public Circle(double radius) {
            setMinSize(radius * 2, radius * 2);
            setMaxSize(radius * 2, radius * 2);
            setPrefSize(radius * 2, radius * 2);
            setStyle("-fx-background-radius: " + radius + "px; -fx-background-color: #167e74;");
        }
        
        public void setFill(Color color) {
            String hex = String.format("#%02X%02X%02X",
                                     (int)(color.getRed() * 255),
                                     (int)(color.getGreen() * 255),
                                     (int)(color.getBlue() * 255));
            setStyle("-fx-background-radius: " + (getMinWidth() / 2) + "px; -fx-background-color: " + hex + ";");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
