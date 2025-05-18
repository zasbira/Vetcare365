import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * VetCare360 Dashboard Application with modern UI
 */
public class VetCare360Dashboard extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Dashboard statistics
    private int totalVets = 0;
    private int totalOwners = 0;
    private int totalPets = 0;
    private int totalVisits = 0;
    private final Map<String, Integer> speciesCount = new HashMap<>();
    
    // Colors
    private static final String SIDEBAR_COLOR = "#167e74"; // Teal
    private static final String CARD_COLOR = "#2d3e50"; // Dark blue
    private static final String HIGHLIGHT_COLOR = "#1abc9c"; // Light teal
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            initializeDatabase();
            
            // Load statistics
            loadStatistics();
            
            // Create the main layout
            BorderPane root = new BorderPane();
            
            // Create sidebar
            VBox sidebar = createSidebar();
            root.setLeft(sidebar);
            
            // Create main content area
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(20));
            mainContent.setStyle("-fx-background-color: #f5f5f5;");
            
            // Add statistics cards
            HBox statsCards = createStatsCards();
            
            // Add charts
            HBox charts = createCharts();
            
            mainContent.getChildren().addAll(statsCards, charts);
            root.setCenter(mainContent);
            
            // Set up the scene
            Scene scene = new Scene(root, 1200, 700);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360 - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Create the sidebar with navigation options
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 15, 15, 15));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: " + SIDEBAR_COLOR + ";");
        
        // Profile section
        Circle profileCircle = new Circle(40, Color.WHITE);
        Label welcomeLabel = new Label("Bienvenue,");
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(Font.font("System", 16));
        
        Label adminLabel = new Label("Dr. Dubois");
        adminLabel.setTextFill(Color.WHITE);
        adminLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        VBox profileInfo = new VBox(5, welcomeLabel, adminLabel);
        profileInfo.setAlignment(Pos.CENTER);
        
        VBox profileSection = new VBox(15, profileCircle, profileInfo);
        profileSection.setAlignment(Pos.CENTER);
        profileSection.setPadding(new Insets(0, 0, 20, 0));
        
        // Navigation buttons
        Button homeBtn = createNavButton("Accueil", true);
        Button vetsBtn = createNavButton("Vétérinaires", false);
        Button ownersBtn = createNavButton("Propriétaires", false);
        Button petsBtn = createNavButton("Animaux", false);
        Button visitsBtn = createNavButton("Visites", false);
        
        // Logout button at the bottom
        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 5;");
        logoutBtn.setPadding(new Insets(10, 15, 10, 15));
        
        Region spacer = new Region();
        spacer.setPrefHeight(50);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(
            profileSection,
            new Separator(),
            homeBtn,
            vetsBtn,
            ownersBtn,
            petsBtn,
            visitsBtn,
            spacer,
            logoutBtn
        );
        
        return sidebar;
    }
    
    /**
     * Create a navigation button for the sidebar
     */
    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setPadding(new Insets(10, 15, 10, 15));
        
        if (isActive) {
            button.setStyle("-fx-background-color: " + HIGHLIGHT_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            
            // Hover effect
            button.setOnMouseEntered(e -> 
                button.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;"));
            button.setOnMouseExited(e -> 
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;"));
        }
        
        return button;
    }
    
    /**
     * Create statistics cards for the dashboard
     */
    private HBox createStatsCards() {
        HBox cardsContainer = new HBox(20);
        cardsContainer.setAlignment(Pos.CENTER);
        
        // Veterinarians card
        StackPane vetsCard = createStatCard("Vétérinaires", Integer.toString(totalVets), "\uf0c0");
        
        // Owners card
        StackPane ownersCard = createStatCard("Propriétaires", Integer.toString(totalOwners), "\uf007");
        
        // Pets card
        StackPane petsCard = createStatCard("Animaux", Integer.toString(totalPets), "\uf1b0");
        
        // Visits card
        StackPane visitsCard = createStatCard("Visites", Integer.toString(totalVisits), "\uf073");
        
        cardsContainer.getChildren().addAll(vetsCard, ownersCard, petsCard, visitsCard);
        return cardsContainer;
    }
    
    /**
     * Create a single statistic card
     */
    private StackPane createStatCard(String title, String value, String icon) {
        StackPane card = new StackPane();
        card.setMinSize(250, 120);
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 10;");
        
        HBox content = new HBox(20);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(20));
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-family: 'FontAwesome'; -fx-font-size: 40; -fx-text-fill: white;");
        
        VBox textContent = new VBox(10);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #aaaaaa;");
        
        textContent.getChildren().addAll(valueLabel, titleLabel);
        
        content.getChildren().addAll(iconLabel, textContent);
        card.getChildren().add(content);
        
        return card;
    }
    
    /**
     * Create charts for the dashboard
     */
    private HBox createCharts() {
        HBox chartsContainer = new HBox(20);
        chartsContainer.setAlignment(Pos.CENTER);
        
        // Animal distribution chart
        VBox animalChartBox = new VBox(10);
        animalChartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label animalChartTitle = new Label("Distribution des espèces");
        animalChartTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : speciesCount.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
        
        animalChartBox.getChildren().addAll(animalChartTitle, barChart);
        
        // Visits over time chart
        VBox visitsChartBox = new VBox(10);
        visitsChartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label visitsChartTitle = new Label("Visites récentes");
        visitsChartTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        CategoryAxis visitXAxis = new CategoryAxis();
        NumberAxis visitYAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(visitXAxis, visitYAxis);
        lineChart.setTitle("");
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        
        XYChart.Series<String, Number> visitSeries = new XYChart.Series<>();
        visitSeries.getData().add(new XYChart.Data<>("Lun", 2));
        visitSeries.getData().add(new XYChart.Data<>("Mar", 5));
        visitSeries.getData().add(new XYChart.Data<>("Mer", 3));
        visitSeries.getData().add(new XYChart.Data<>("Jeu", 7));
        visitSeries.getData().add(new XYChart.Data<>("Ven", 4));
        lineChart.getData().add(visitSeries);
        
        visitsChartBox.getChildren().addAll(visitsChartTitle, lineChart);
        
        chartsContainer.getChildren().addAll(animalChartBox, visitsChartBox);
        return chartsContainer;
    }
    
    /**
     * Initialize the database
     */
    private void initializeDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create database if it doesn't exist
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                stmt.execute("CREATE DATABASE IF NOT EXISTS db");
            }
            
            // Create tables if they don't exist
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
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
                stmt.execute(createVetsTable);
                
                // Create proprietaires table
                String createPropsTable = 
                        "CREATE TABLE IF NOT EXISTS proprietaires (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  nom VARCHAR(50) NOT NULL," +
                        "  prenom VARCHAR(50) NOT NULL," +
                        "  adresse VARCHAR(200)," +
                        "  telephone VARCHAR(20)" +
                        ")";
                stmt.execute(createPropsTable);
                
                // Create animaux table
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
                
                // Create visites table
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
                
                // Check if tables have data, if not add sample data
                
                // Check veterinaires
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertVets = 
                            "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                            "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), " +
                            "('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), " +
                            "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01'), " +
                            "('Robert', 'Thomas', 'Cardiologie', 't.robert@vetcare360.fr', '01 56 78 90 12'), " +
                            "('Petit', 'Julie', 'Ophtalmologie', 'j.petit@vetcare360.fr', '01 67 89 01 23')";
                    stmt.execute(insertVets);
                }
                
                // Check proprietaires
                rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertProps = 
                            "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES " +
                            "('Martin', 'Jean', '15 Rue des Fleurs, 75001 Paris', '01 23 45 67 89'), " +
                            "('Dubois', 'Marie', '27 Avenue Victor Hugo, 69002 Lyon', '04 56 78 90 12'), " +
                            "('Petit', 'Sophie', '8 Rue du Commerce, 44000 Nantes', '02 34 56 78 90'), " +
                            "('Bernard', 'Thomas', '5 Place de la Libération, 33000 Bordeaux', '05 67 89 01 23'), " +
                            "('Robert', 'Claire', '12 Boulevard des Alpes, 38000 Grenoble', '04 76 54 32 10')";
                    stmt.execute(insertProps);
                }
                
                // Check animaux
                rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertAnimaux = 
                            "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES " +
                            "('Rex', 'Chien', 'Mâle', 5, 1), " +
                            "('Minette', 'Chat', 'Femelle', 3, 1), " +
                            "('Nemo', 'Poisson', 'Mâle', 1, 2), " +
                            "('Coco', 'Oiseau', 'Mâle', 2, 3), " +
                            "('Bella', 'Chat', 'Femelle', 4, 2), " +
                            "('Max', 'Chien', 'Mâle', 7, 4), " +
                            "('Luna', 'Chat', 'Femelle', 2, 5)";
                    stmt.execute(insertAnimaux);
                }
                
                // Check visites
                rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertVisites = 
                            "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES " +
                            "('2025-05-01', 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1), " +
                            "('2025-05-02', 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1), " +
                            "('2025-05-03', 'Contrôle dentaire', 'Tartre important', 'Détartrage programmé', 5, 2), " +
                            "('2025-05-10', 'Troubles digestifs', 'Gastrite légère', 'Régime spécial 2 semaines', 3, 3), " +
                            "('2025-05-15', 'Boiterie patte avant', 'Entorse légère', 'Repos et anti-inflammatoires', 4, 4), " +
                            "('2025-05-17', 'Contrôle annuel', 'RAS', 'Aucun traitement nécessaire', 6, 1), " +
                            "('2025-05-18', 'Vaccination', 'Animal en bonne santé', 'Vaccin', 7, 5)";
                    stmt.execute(insertVisites);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }
    
    /**
     * Load statistics from the database
     */
    private void loadStatistics() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Count veterinaires
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
            if (rs.next()) {
                totalVets = rs.getInt(1);
            }
            
            // Count proprietaires
            rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
            if (rs.next()) {
                totalOwners = rs.getInt(1);
            }
            
            // Count animaux
            rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
            if (rs.next()) {
                totalPets = rs.getInt(1);
            }
            
            // Count visites
            rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
            if (rs.next()) {
                totalVisits = rs.getInt(1);
            }
            
            // Count species distribution
            rs = stmt.executeQuery("SELECT espece, COUNT(*) FROM animaux GROUP BY espece");
            while (rs.next()) {
                speciesCount.put(rs.getString(1), rs.getInt(2));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading statistics: " + e.getMessage());
        }
    }
    
    /**
     * Show an error message
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
