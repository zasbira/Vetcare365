import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * VetCare360 Dashboard with database connectivity and CRUD operations
 */
// Entity classes with attributes
class Owner {
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String telephone;
    private String email;
    
    public Owner() {}
    
    public Owner(int id, String firstName, String lastName, String address, String city, String telephone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.email = email;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

class Vet {
    private int id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String telephone;
    private String email;
    
    public Vet() {}
    
    public Vet(int id, String firstName, String lastName, String specialization, String telephone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.telephone = telephone;
        this.email = email;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + specialization + ")";
    }
}

class Animal {
    private int id;
    private String name;
    private Date birthDate;
    private String type;
    private String breed;
    private String gender;
    private Owner owner;
    
    public Animal() {}
    
    public Animal(int id, String name, Date birthDate, String type, String breed, String gender, Owner owner) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.owner = owner;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}

class Visit {
    private int id;
    private Date date;
    private String description;
    private Animal animal;
    private Vet vet;
    
    public Visit() {}
    
    public Visit(int id, Date date, String description, Animal animal, Vet vet) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.animal = animal;
        this.vet = vet;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    
    public Vet getVet() { return vet; }
    public void setVet(Vet vet) { this.vet = vet; }
}

// Database connection management
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/vetcare360";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Database driver not found", e);
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

public class SimpleVetDashboard extends Application {
    
    // UI components
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Button homeBtn, ownersBtn, vetsBtn, animalsBtn, visitsBtn;
    
    // Data lists for tables
    private ObservableList<Owner> ownersList = FXCollections.observableArrayList();
    private ObservableList<Vet> vetsList = FXCollections.observableArrayList();
    private ObservableList<Animal> animalsList = FXCollections.observableArrayList();
    private ObservableList<Visit> visitsList = FXCollections.observableArrayList();
    
    // Database connectivity flag
    private boolean databaseConnected = false;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Try to connect to database
            try {
                DBConnection.getConnection();
                databaseConnected = true;
                System.out.println("Database connection successful!");
                // Load initial data
                loadAllData();
            } catch (SQLException e) {
                databaseConnected = false;
                System.err.println("Database connection failed: " + e.getMessage());
                // We continue with the application even if DB connection fails
            }
            
            // Create main layout
            mainLayout = new BorderPane();
            
            // Create sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);
            
            // Create content area
            contentArea = new StackPane();
            contentArea.setStyle("-fx-background-color: #f0f5f5;");
            mainLayout.setCenter(contentArea);
            
            // Show dashboard by default
            showDashboard();
            
            // Set up the scene
            Scene scene = new Scene(mainLayout, 1200, 700);
            primaryStage.setTitle("VetCare 360 Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Create sidebar with navigation
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
     * Load data from database
     */
    private void loadAllData() {
        if (!databaseConnected) return;
        
        try {
            // Load owners
            loadOwners();
            
            // Load vets
            loadVets();
            
            // Load animals
            loadAnimals();
            
            // Load visits
            loadVisits();
            
        } catch (SQLException e) {
            System.err.println("Error loading data: " + e.getMessage());
            showError("Error loading data from database: " + e.getMessage());
        }
    }
    
    /**
     * Load owners from database
     */
    private void loadOwners() throws SQLException {
        ownersList.clear();
        String query = "SELECT * FROM owners";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Owner owner = new Owner(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                ownersList.add(owner);
            }
        }
    }
    
    /**
     * Load vets from database
     */
    private void loadVets() throws SQLException {
        vetsList.clear();
        String query = "SELECT * FROM vets";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Vet vet = new Vet(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("specialization"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                vetsList.add(vet);
            }
        }
    }
    
    /**
     * Load animals from database
     */
    private void loadAnimals() throws SQLException {
        animalsList.clear();
        String query = "SELECT a.*, o.id as owner_id, o.first_name, o.last_name, o.address, o.city, o.telephone, o.email " +
                      "FROM animals a JOIN owners o ON a.owner_id = o.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Owner owner = new Owner(
                    rs.getInt("owner_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                
                Animal animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("birth_date"),
                    rs.getString("type"),
                    rs.getString("breed"),
                    rs.getString("gender"),
                    owner
                );
                animalsList.add(animal);
            }
        }
    }
    
    /**
     * Load visits from database
     */
    private void loadVisits() throws SQLException {
        visitsList.clear();
        String query = "SELECT v.*, a.id as animal_id, a.name as animal_name, a.birth_date, a.type, a.breed, a.gender, " +
                      "o.id as owner_id, o.first_name as owner_first_name, o.last_name as owner_last_name, " +
                      "o.address, o.city, o.telephone, o.email, " +
                      "vt.id as vet_id, vt.first_name as vet_first_name, vt.last_name as vet_last_name, " +
                      "vt.specialization, vt.telephone as vet_telephone, vt.email as vet_email " +
                      "FROM visits v " +
                      "JOIN animals a ON v.animal_id = a.id " +
                      "JOIN owners o ON a.owner_id = o.id " +
                      "JOIN vets vt ON v.vet_id = vt.id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Owner owner = new Owner(
                    rs.getInt("owner_id"),
                    rs.getString("owner_first_name"),
                    rs.getString("owner_last_name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("telephone"),
                    rs.getString("email")
                );
                
                Animal animal = new Animal(
                    rs.getInt("animal_id"),
                    rs.getString("animal_name"),
                    rs.getDate("birth_date"),
                    rs.getString("type"),
                    rs.getString("breed"),
                    rs.getString("gender"),
                    owner
                );
                
                Vet vet = new Vet(
                    rs.getInt("vet_id"),
                    rs.getString("vet_first_name"),
                    rs.getString("vet_last_name"),
                    rs.getString("specialization"),
                    rs.getString("vet_telephone"),
                    rs.getString("vet_email")
                );
                
                Visit visit = new Visit(
                    rs.getInt("id"),
                    rs.getDate("date"),
                    rs.getString("description"),
                    animal,
                    vet
                );
                
                visitsList.add(visit);
            }
        }
    }
    
    /**
     * Show dashboard view
     */
    private void showDashboard() {
        setActiveButton(homeBtn);
        
        // Create scrollable dashboard content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.setStyle("-fx-background-color: #f0f5f5;");
        
        // Dashboard header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Tableau de bord");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitleLabel = new Label("Gestion de clinique vétérinaire");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        // Add connection status
        String statusText = databaseConnected ? "Connecté à la base de données" : "Base de données non connectée";
        Label dbStatusLabel = new Label(statusText);
        dbStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (databaseConnected ? "#2e7d32" : "#c62828") + "; -fx-font-weight: bold;");
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel, dbStatusLabel);
        header.getChildren().add(titleBox);
        
        // Create stats cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        VBox ownersCard = createStatsCard("Propriétaires", "25", "#4e73df");
        VBox animalsCard = createStatsCard("Animaux", "42", "#1cc88a");
        VBox visitsCard = createStatsCard("Visites", "18", "#f6c23e");
        VBox vetsCard = createStatsCard("Vétérinaires", "8", "#36b9cc");
        
        statsCards.getChildren().addAll(ownersCard, animalsCard, visitsCard, vetsCard);
        
        // Create charts section
        HBox chartsSection = new HBox(20);
        chartsSection.setAlignment(Pos.CENTER);
        
        // Create visit statistics chart
        VBox visitStatsBox = new VBox(10);
        visitStatsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        visitStatsBox.setPadding(new Insets(15));
        visitStatsBox.setPrefWidth(500);
        
        Label visitChartTitle = new Label("Statistiques des visites");
        visitChartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label visitChartSubtitle = new Label("Visites mensuelles au cours des 12 derniers mois");
        visitChartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Create the visit chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> visitChart = new BarChart<>(xAxis, yAxis);
        visitChart.setTitle("");
        visitChart.setLegendVisible(false);
        visitChart.setAnimated(false);
        visitChart.setPrefHeight(300);
        visitChart.setStyle("-fx-background-color: transparent;");
        
        // Add sample data for visit chart
        XYChart.Series<String, Number> visitSeries = new XYChart.Series<>();
        visitSeries.setName("Nombre de visites");
        
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jui", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        int[] values = {0, 4, 8, 2, 1, 0, 0, 3, 2, 1, 0, 0};
        
        for (int i = 0; i < months.length; i++) {
            visitSeries.getData().add(new XYChart.Data<>(months[i], values[i]));
        }
        
        visitChart.getData().add(visitSeries);
        
        // Style the bars to match purple color in image
        for (XYChart.Data<String, Number> item : visitSeries.getData()) {
            item.getNode().setStyle("-fx-bar-fill: #8884d8;");
        }
        
        visitStatsBox.getChildren().addAll(visitChartTitle, visitChartSubtitle, visitChart);
        
        // Create animal types chart
        VBox animalTypesBox = new VBox(10);
        animalTypesBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        animalTypesBox.setPadding(new Insets(15));
        animalTypesBox.setPrefWidth(400);
        
        Label animalChartTitle = new Label("Types d'animaux");
        animalChartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label animalChartSubtitle = new Label("Répartition par espèce");
        animalChartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Create the chart for animal types
        CategoryAxis animalXAxis = new CategoryAxis();
        NumberAxis animalYAxis = new NumberAxis();
        animalYAxis.setTickUnit(1);
        BarChart<String, Number> animalChart = new BarChart<>(animalXAxis, animalYAxis);
        animalChart.setTitle("");
        animalChart.setLegendVisible(false);
        animalChart.setAnimated(false);
        animalChart.setPrefHeight(300);
        animalChart.setStyle("-fx-background-color: transparent;");
        
        // Sample data for animal chart
        XYChart.Series<String, Number> animalSeries = new XYChart.Series<>();
        
        String[] species = {"Chien", "Oiseau", "Lapin", "Chat", "Rongeur"};
        int[] counts = {8, 3, 1, 2, 2};
        String[] colors = {"#4e73df", "#36b9cc", "#1cc88a", "#f6c23e", "#e74a3b"};
        
        for (int i = 0; i < species.length; i++) {
            animalSeries.getData().add(new XYChart.Data<>(species[i], counts[i]));
        }
        
        animalChart.getData().add(animalSeries);
        
        // Style each bar with different colors
        int colorIndex = 0;
        for (XYChart.Data<String, Number> item : animalSeries.getData()) {
            item.getNode().setStyle("-fx-bar-fill: " + colors[colorIndex % colors.length] + ";");
            colorIndex++;
        }
        
        animalTypesBox.getChildren().addAll(animalChartTitle, animalChartSubtitle, animalChart);
        
        chartsSection.getChildren().addAll(visitStatsBox, animalTypesBox);
        
        // Add all sections to dashboard
        dashboardContent.getChildren().addAll(header, statsCards, chartsSection);
        
        // Set dashboard content
        scrollPane.setContent(dashboardContent);
        
        // Update content area
        contentArea.getChildren().clear();
        contentArea.getChildren().add(scrollPane);
    }
    
    /**
     * Create a statistics card
     */
    private VBox createStatsCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        
        // Card header with title
        HBox header = new HBox();
        header.setPadding(new Insets(10, 15, 0, 15));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        header.getChildren().add(titleLabel);
        
        // Card content with value
        VBox content = new VBox();
        content.setPadding(new Insets(5, 15, 10, 15));
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        content.getChildren().add(valueLabel);
        
        // Colorful bottom border
        Region border = new Region();
        border.setPrefHeight(5);
        border.setMaxWidth(Double.MAX_VALUE);
        border.setStyle("-fx-background-color: " + color + ";");
        
        card.getChildren().addAll(header, content, border);
        
        return card;
    }
    
    /**
     * Show a centered message
     */
    private void showMessage(String message) {
        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(100, 20, 20, 20));
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        messageBox.getChildren().add(messageLabel);
        
        // Set as active content
        contentArea.getChildren().clear();
        contentArea.getChildren().add(messageBox);
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
