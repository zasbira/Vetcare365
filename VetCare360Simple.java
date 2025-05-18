import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

/**
 * Simplified VetCare360 application with database integration
 */
public class VetCare360Simple extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";       
    private static final String DB_PASSWORD = "";  
    
    // UI components
    private TabPane tabPane = new TabPane();
    private Label statusLabel = new Label();
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            initializeDatabase();
            
            // Set up the main layout
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));
            
            // Create header
            HBox header = new HBox(10);
            header.setPadding(new Insets(10));
            header.setStyle("-fx-background-color: #167e74;"); // Teal color
            
            Label titleLabel = new Label("VetCare 360");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            header.getChildren().add(titleLabel);
            
            // Create tabs
            tabPane.getTabs().addAll(
                createVeterinaireTab(),
                createProprietaireTab(),
                createAnimalTab(), 
                createVisiteTab()
            );
            
            // Create status bar
            HBox statusBar = new HBox(10);
            statusBar.setPadding(new Insets(5));
            statusBar.setStyle("-fx-background-color: #f0f0f0;");
            
            statusLabel.setText("Ready");
            Button refreshButton = new Button("Refresh Data");
            refreshButton.setOnAction(e -> refreshCurrentTab());
            
            statusBar.getChildren().addAll(statusLabel, refreshButton);
            
            // Set up the layout
            root.setTop(header);
            root.setCenter(tabPane);
            root.setBottom(statusBar);
            
            // Set up the scene
            Scene scene = new Scene(root, 1000, 600);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Initial data load
            refreshCurrentTab();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    private void refreshCurrentTab() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab != null) {
            String tabText = selectedTab.getText();
            
            if (tabText.equals("Veterinaires")) {
                loadVeterinaires();
            } else if (tabText.equals("Proprietaires")) {
                loadProprietaires();
            } else if (tabText.equals("Animaux")) {
                loadAnimaux();
            } else if (tabText.equals("Visites")) {
                loadVisites();
            }
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
                
                System.out.println("Connected to MySQL server");
                stmt.execute("CREATE DATABASE IF NOT EXISTS db");
                statusLabel.setText("Database 'db' verified");
            }
            
            // Now connect to the db database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("Connected to 'db' database");
                
                // Create tables if they don't exist
                
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
                
                statusLabel.setText("All tables verified");
                
                // Add sample data if tables are empty
                addSampleDataIfNeeded(stmt);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }
    
    private void addSampleDataIfNeeded(Statement stmt) {
        try {
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
                System.out.println("Added sample veterinaire data");
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
                System.out.println("Added sample proprietaire data");
            }
            
            // Check animaux - only add if we have proprietaires
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
                System.out.println("Added sample animal data");
            }
            
            // Check visites - only add if we have animaux and veterinaires
            rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Add sample data
                java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
                String insertVisites = 
                        "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES " +
                        "('" + today + "', 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1), " +
                        "('" + today + "', 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1)";
                stmt.execute(insertVisites);
                System.out.println("Added sample visite data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error adding sample data: " + e.getMessage());
        }
    }
    
    // Tab creation methods
    private Tab createVeterinaireTab() {
        Tab tab = new Tab("Veterinaires");
        tab.setClosable(false);
        
        TableView<Veterinaire> table = new TableView<>();
        
        TableColumn<Veterinaire, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Veterinaire, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Veterinaire, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        TableColumn<Veterinaire, String> specialiteCol = new TableColumn<>("Spécialité");
        specialiteCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        
        TableColumn<Veterinaire, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<Veterinaire, String> telephoneCol = new TableColumn<>("Téléphone");
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        table.getColumns().addAll(idCol, nomCol, prenomCol, specialiteCol, emailCol, telephoneCol);
        
        tab.setContent(table);
        tab.setUserData(table); // Store table in tab's user data for later access
        
        return tab;
    }
    
    private Tab createProprietaireTab() {
        Tab tab = new Tab("Proprietaires");
        tab.setClosable(false);
        
        TableView<Proprietaire> table = new TableView<>();
        
        TableColumn<Proprietaire, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Proprietaire, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Proprietaire, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        TableColumn<Proprietaire, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        
        TableColumn<Proprietaire, String> telephoneCol = new TableColumn<>("Téléphone");
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        table.getColumns().addAll(idCol, nomCol, prenomCol, adresseCol, telephoneCol);
        
        tab.setContent(table);
        tab.setUserData(table); // Store table in tab's user data for later access
        
        return tab;
    }
    
    private Tab createAnimalTab() {
        Tab tab = new Tab("Animaux");
        tab.setClosable(false);
        
        TableView<Animal> table = new TableView<>();
        
        TableColumn<Animal, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Animal, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Animal, String> especeCol = new TableColumn<>("Espèce");
        especeCol.setCellValueFactory(new PropertyValueFactory<>("espece"));
        
        TableColumn<Animal, String> sexeCol = new TableColumn<>("Sexe");
        sexeCol.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        
        TableColumn<Animal, Integer> ageCol = new TableColumn<>("Âge");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        TableColumn<Animal, Integer> proprietaireIdCol = new TableColumn<>("ID Propriétaire");
        proprietaireIdCol.setCellValueFactory(new PropertyValueFactory<>("proprietaireId"));
        
        table.getColumns().addAll(idCol, nomCol, especeCol, sexeCol, ageCol, proprietaireIdCol);
        
        tab.setContent(table);
        tab.setUserData(table); // Store table in tab's user data for later access
        
        return tab;
    }
    
    private Tab createVisiteTab() {
        Tab tab = new Tab("Visites");
        tab.setClosable(false);
        
        TableView<Visite> table = new TableView<>();
        
        TableColumn<Visite, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Visite, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Visite, String> motifCol = new TableColumn<>("Motif");
        motifCol.setCellValueFactory(new PropertyValueFactory<>("motif"));
        
        TableColumn<Visite, String> diagnosticCol = new TableColumn<>("Diagnostic");
        diagnosticCol.setCellValueFactory(new PropertyValueFactory<>("diagnostic"));
        
        TableColumn<Visite, String> traitementCol = new TableColumn<>("Traitement");
        traitementCol.setCellValueFactory(new PropertyValueFactory<>("traitement"));
        
        TableColumn<Visite, Integer> animalIdCol = new TableColumn<>("ID Animal");
        animalIdCol.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        
        TableColumn<Visite, Integer> veterinaireIdCol = new TableColumn<>("ID Vétérinaire");
        veterinaireIdCol.setCellValueFactory(new PropertyValueFactory<>("veterinaireId"));
        
        table.getColumns().addAll(idCol, dateCol, motifCol, diagnosticCol, traitementCol, animalIdCol, veterinaireIdCol);
        
        tab.setContent(table);
        tab.setUserData(table); // Store table in tab's user data for later access
        
        return tab;
    }
    
    // Data loading methods
    private void loadVeterinaires() {
        try {
            ObservableList<Veterinaire> vets = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM veterinaires")) {
                
                while (rs.next()) {
                    Veterinaire vet = new Veterinaire(
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
            
            // Find the veterinaires tab and update its table
            Tab tab = findTabByName("Veterinaires");
            if (tab != null) {
                @SuppressWarnings("unchecked")
                TableView<Veterinaire> table = (TableView<Veterinaire>) tab.getUserData();
                table.setItems(vets);
                statusLabel.setText("Loaded " + vets.size() + " veterinaires");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading veterinaires: " + e.getMessage());
        }
    }
    
    private void loadProprietaires() {
        try {
            ObservableList<Proprietaire> props = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM proprietaires")) {
                
                while (rs.next()) {
                    Proprietaire prop = new Proprietaire(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("adresse"),
                            rs.getString("telephone")
                    );
                    props.add(prop);
                }
            }
            
            // Find the proprietaires tab and update its table
            Tab tab = findTabByName("Proprietaires");
            if (tab != null) {
                @SuppressWarnings("unchecked")
                TableView<Proprietaire> table = (TableView<Proprietaire>) tab.getUserData();
                table.setItems(props);
                statusLabel.setText("Loaded " + props.size() + " proprietaires");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading proprietaires: " + e.getMessage());
        }
    }
    
    private void loadAnimaux() {
        try {
            ObservableList<Animal> animaux = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM animaux")) {
                
                while (rs.next()) {
                    Animal animal = new Animal(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("espece"),
                            rs.getString("sexe"),
                            rs.getInt("age"),
                            rs.getInt("proprietaire_id")
                    );
                    animaux.add(animal);
                }
            }
            
            // Find the animaux tab and update its table
            Tab tab = findTabByName("Animaux");
            if (tab != null) {
                @SuppressWarnings("unchecked")
                TableView<Animal> table = (TableView<Animal>) tab.getUserData();
                table.setItems(animaux);
                statusLabel.setText("Loaded " + animaux.size() + " animaux");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading animaux: " + e.getMessage());
        }
    }
    
    private void loadVisites() {
        try {
            ObservableList<Visite> visites = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM visites")) {
                
                while (rs.next()) {
                    Visite visite = new Visite(
                            rs.getInt("id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("motif"),
                            rs.getString("diagnostic"),
                            rs.getString("traitement"),
                            rs.getInt("animal_id"),
                            rs.getInt("veterinaire_id")
                    );
                    visites.add(visite);
                }
            }
            
            // Find the visites tab and update its table
            Tab tab = findTabByName("Visites");
            if (tab != null) {
                @SuppressWarnings("unchecked")
                TableView<Visite> table = (TableView<Visite>) tab.getUserData();
                table.setItems(visites);
                statusLabel.setText("Loaded " + visites.size() + " visites");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading visites: " + e.getMessage());
        }
    }
    
    private Tab findTabByName(String name) {
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(name)) {
                return tab;
            }
        }
        return null;
    }
    
    /**
     * Show an error message
     */
    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setTextFill(Color.RED);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Model classes for database entities
     */
    public static class Veterinaire {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String specialite;
        private final String email;
        private final String telephone;
        
        public Veterinaire(int id, String nom, String prenom, String specialite, String email, String telephone) {
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
    
    public static class Proprietaire {
        private final int id;
        private final String nom;
        private final String prenom;
        private final String adresse;
        private final String telephone;
        
        public Proprietaire(int id, String nom, String prenom, String adresse, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.adresse = adresse;
            this.telephone = telephone;
        }
        
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getAdresse() { return adresse; }
        public String getTelephone() { return telephone; }
    }
    
    public static class Animal {
        private final int id;
        private final String nom;
        private final String espece;
        private final String sexe;
        private final int age;
        private final int proprietaireId;
        
        public Animal(int id, String nom, String espece, String sexe, int age, int proprietaireId) {
            this.id = id;
            this.nom = nom;
            this.espece = espece;
            this.sexe = sexe;
            this.age = age;
            this.proprietaireId = proprietaireId;
        }
        
        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getEspece() { return espece; }
        public String getSexe() { return sexe; }
        public int getAge() { return age; }
        public int getProprietaireId() { return proprietaireId; }
    }
    
    public static class Visite {
        private final int id;
        private final LocalDate date;
        private final String motif;
        private final String diagnostic;
        private final String traitement;
        private final int animalId;
        private final int veterinaireId;
        
        public Visite(int id, LocalDate date, String motif, String diagnostic, String traitement, int animalId, int veterinaireId) {
            this.id = id;
            this.date = date;
            this.motif = motif;
            this.diagnostic = diagnostic;
            this.traitement = traitement;
            this.animalId = animalId;
            this.veterinaireId = veterinaireId;
        }
        
        public int getId() { return id; }
        public LocalDate getDate() { return date; }
        public String getMotif() { return motif; }
        public String getDiagnostic() { return diagnostic; }
        public String getTraitement() { return traitement; }
        public int getAnimalId() { return animalId; }
        public int getVeterinaireId() { return veterinaireId; }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
