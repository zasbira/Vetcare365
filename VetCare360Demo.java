import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Comprehensive standalone application to demonstrate all database tables in VetCare360
 */
public class VetCare360Demo extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // UI components
    private TabPane tabPane = new TabPane();
    private Label statusLabel = new Label("Status: Ready");
    
    // Tables
    private TableView<Veterinaire> vetTable = new TableView<>();
    private TableView<Proprietaire> propTable = new TableView<>();
    private TableView<Animal> animalTable = new TableView<>();
    private TableView<Visite> visiteTable = new TableView<>();
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Set up the main layout
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(10));
            
            // Create title
            Label titleLabel = new Label("VetCare 360 - Gestion Clinique Vétérinaire");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #008080;");
            
            // Initialize database
            initializeDatabase();
            
            // Create tabs
            Tab vetTab = createVeterinairesTab();
            Tab propTab = createProprietairesTab();
            Tab animalTab = createAnimauxTab();
            Tab visiteTab = createVisitesTab();
            
            tabPane.getTabs().addAll(vetTab, propTab, animalTab, visiteTab);
            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab == vetTab) {
                    loadVeterinaires();
                } else if (newTab == propTab) {
                    loadProprietaires();
                } else if (newTab == animalTab) {
                    loadAnimaux();
                } else if (newTab == visiteTab) {
                    loadVisites();
                }
            });
            
            // Create buttons
            Button refreshButton = new Button("Rafraîchir");
            refreshButton.setOnAction(e -> {
                Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
                if (selectedTab == vetTab) {
                    loadVeterinaires();
                } else if (selectedTab == propTab) {
                    loadProprietaires();
                } else if (selectedTab == animalTab) {
                    loadAnimaux();
                } else if (selectedTab == visiteTab) {
                    loadVisites();
                }
            });
            
            // Add components to layout
            VBox topBox = new VBox(10, titleLabel);
            topBox.setPadding(new Insets(0, 0, 10, 0));
            
            HBox bottomBox = new HBox(10, refreshButton, statusLabel);
            bottomBox.setPadding(new Insets(10, 0, 0, 0));
            
            root.setTop(topBox);
            root.setCenter(tabPane);
            root.setBottom(bottomBox);
            
            // Set up the scene
            Scene scene = new Scene(root, 1000, 700);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360 Demo");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Load initial data
            loadVeterinaires();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Create tab for veterinarians
     */
    private Tab createVeterinairesTab() {
        Tab tab = new Tab("Vétérinaires");
        tab.setClosable(false);
        
        // Set up table columns
        TableColumn<Veterinaire, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Veterinaire, String> lastNameCol = new TableColumn<>("Nom");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Veterinaire, String> firstNameCol = new TableColumn<>("Prénom");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        TableColumn<Veterinaire, String> specialtyCol = new TableColumn<>("Spécialité");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        
        TableColumn<Veterinaire, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<Veterinaire, String> phoneCol = new TableColumn<>("Téléphone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        vetTable.getColumns().addAll(idCol, lastNameCol, firstNameCol, specialtyCol, emailCol, phoneCol);
        
        // Set column widths
        idCol.setPrefWidth(50);
        lastNameCol.setPrefWidth(150);
        firstNameCol.setPrefWidth(150);
        specialtyCol.setPrefWidth(150);
        emailCol.setPrefWidth(250);
        phoneCol.setPrefWidth(150);
        
        BorderPane content = new BorderPane();
        content.setCenter(vetTable);
        content.setPadding(new Insets(10));
        
        tab.setContent(content);
        return tab;
    }
    
    /**
     * Create tab for owners
     */
    private Tab createProprietairesTab() {
        Tab tab = new Tab("Propriétaires");
        tab.setClosable(false);
        
        // Set up table columns
        TableColumn<Proprietaire, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Proprietaire, String> lastNameCol = new TableColumn<>("Nom");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Proprietaire, String> firstNameCol = new TableColumn<>("Prénom");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        
        TableColumn<Proprietaire, String> addressCol = new TableColumn<>("Adresse");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        
        TableColumn<Proprietaire, String> phoneCol = new TableColumn<>("Téléphone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        propTable.getColumns().addAll(idCol, lastNameCol, firstNameCol, addressCol, phoneCol);
        
        // Set column widths
        idCol.setPrefWidth(50);
        lastNameCol.setPrefWidth(150);
        firstNameCol.setPrefWidth(150);
        addressCol.setPrefWidth(300);
        phoneCol.setPrefWidth(150);
        
        BorderPane content = new BorderPane();
        content.setCenter(propTable);
        content.setPadding(new Insets(10));
        
        tab.setContent(content);
        return tab;
    }
    
    /**
     * Create tab for animals
     */
    private Tab createAnimauxTab() {
        Tab tab = new Tab("Animaux");
        tab.setClosable(false);
        
        // Set up table columns
        TableColumn<Animal, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Animal, String> nameCol = new TableColumn<>("Nom");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        TableColumn<Animal, String> speciesCol = new TableColumn<>("Espèce");
        speciesCol.setCellValueFactory(new PropertyValueFactory<>("espece"));
        
        TableColumn<Animal, String> sexCol = new TableColumn<>("Sexe");
        sexCol.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        
        TableColumn<Animal, Integer> ageCol = new TableColumn<>("Âge");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        TableColumn<Animal, Integer> ownerIdCol = new TableColumn<>("Propriétaire");
        ownerIdCol.setCellValueFactory(new PropertyValueFactory<>("proprietaireId"));
        
        animalTable.getColumns().addAll(idCol, nameCol, speciesCol, sexCol, ageCol, ownerIdCol);
        
        // Set column widths
        idCol.setPrefWidth(50);
        nameCol.setPrefWidth(150);
        speciesCol.setPrefWidth(100);
        sexCol.setPrefWidth(100);
        ageCol.setPrefWidth(50);
        ownerIdCol.setPrefWidth(100);
        
        BorderPane content = new BorderPane();
        content.setCenter(animalTable);
        content.setPadding(new Insets(10));
        
        tab.setContent(content);
        return tab;
    }
    
    /**
     * Create tab for visits
     */
    private Tab createVisitesTab() {
        Tab tab = new Tab("Visites");
        tab.setClosable(false);
        
        // Set up table columns
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
        
        TableColumn<Visite, Integer> animalIdCol = new TableColumn<>("Animal");
        animalIdCol.setCellValueFactory(new PropertyValueFactory<>("animalId"));
        
        TableColumn<Visite, Integer> vetIdCol = new TableColumn<>("Vétérinaire");
        vetIdCol.setCellValueFactory(new PropertyValueFactory<>("veterinaireId"));
        
        visiteTable.getColumns().addAll(idCol, dateCol, motifCol, diagnosticCol, traitementCol, animalIdCol, vetIdCol);
        
        // Set column widths
        idCol.setPrefWidth(50);
        dateCol.setPrefWidth(150);
        motifCol.setPrefWidth(150);
        diagnosticCol.setPrefWidth(200);
        traitementCol.setPrefWidth(200);
        animalIdCol.setPrefWidth(100);
        vetIdCol.setPrefWidth(100);
        
        BorderPane content = new BorderPane();
        content.setCenter(visiteTable);
        content.setPadding(new Insets(10));
        
        tab.setContent(content);
        return tab;
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
                
                statusLabel.setText("Status: All tables verified");
                
                // Check if tables have data - add sample data if empty
                
                // Check veterinaires
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM veterinaires");
                rs.next();
                int vetCount = rs.getInt(1);
                
                if (vetCount == 0) {
                    // Add sample data
                    String insertSampleVets = 
                            "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES " +
                            "('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), " +
                            "('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), " +
                            "('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01'), " +
                            "('Robert', 'Thomas', 'Cardiologie', 't.robert@vetcare360.fr', '01 56 78 90 12'), " +
                            "('Petit', 'Julie', 'Ophtalmologie', 'j.petit@vetcare360.fr', '01 67 89 01 23')";
                    
                    stmt.execute(insertSampleVets);
                    statusLabel.setText("Status: Sample veterinarians added");
                }
                
                // Check proprietaires
                rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
                rs.next();
                int propCount = rs.getInt(1);
                
                if (propCount == 0) {
                    // Add sample data
                    String insertSampleProps = 
                            "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES " +
                            "('Martin', 'Jean', '15 Rue des Fleurs, 75001 Paris', '01 23 45 67 89'), " +
                            "('Dubois', 'Marie', '27 Avenue Victor Hugo, 69002 Lyon', '04 56 78 90 12'), " +
                            "('Petit', 'Sophie', '8 Rue du Commerce, 44000 Nantes', '02 34 56 78 90'), " +
                            "('Bernard', 'Thomas', '5 Place de la Libération, 33000 Bordeaux', '05 67 89 01 23'), " +
                            "('Robert', 'Claire', '12 Boulevard des Alpes, 38000 Grenoble', '04 76 54 32 10')";
                    
                    stmt.execute(insertSampleProps);
                    statusLabel.setText("Status: Sample owners added");
                }
                
                // Check animaux - only add if we have proprietaires
                rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
                rs.next();
                int animalCount = rs.getInt(1);
                
                if (animalCount == 0 && propCount > 0) {
                    // Add sample data
                    String insertSampleAnimaux = 
                            "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES " +
                            "('Rex', 'Chien', 'Mâle', 5, 1), " +
                            "('Minette', 'Chat', 'Femelle', 3, 1), " +
                            "('Nemo', 'Poisson', 'Mâle', 1, 2), " +
                            "('Coco', 'Oiseau', 'Mâle', 2, 3), " +
                            "('Bella', 'Chat', 'Femelle', 4, 2)";
                    
                    stmt.execute(insertSampleAnimaux);
                    statusLabel.setText("Status: Sample animals added");
                }
                
                // Check visites - only add if we have animaux and veterinaires
                rs = stmt.executeQuery("SELECT COUNT(*) FROM visites");
                rs.next();
                int visiteCount = rs.getInt(1);
                
                if (visiteCount == 0 && animalCount > 0 && vetCount > 0) {
                    // Add sample data with a LocalDate for the current year
                    LocalDate today = LocalDate.now();
                    String insertSampleVisites = 
                            "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES " +
                            "('" + Date.valueOf(today.minusDays(17)) + "', 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1), " +
                            "('" + Date.valueOf(today.minusDays(16)) + "', 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1), " +
                            "('" + Date.valueOf(today.minusDays(15)) + "', 'Contrôle dentaire', 'Tartre important', 'Détartrage programmé', 5, 2), " +
                            "('" + Date.valueOf(today.minusDays(8)) + "', 'Troubles digestifs', 'Gastrite légère', 'Régime spécial 2 semaines', 3, 3), " +
                            "('" + Date.valueOf(today.minusDays(3)) + "', 'Boiterie patte avant', 'Entorse légère', 'Repos et anti-inflammatoires', 4, 4)";
                    
                    stmt.execute(insertSampleVisites);
                    statusLabel.setText("Status: Sample visits added");
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
            
            // Update the table
            vetTable.setItems(vets);
            statusLabel.setText("Status: Loaded " + vets.size() + " veterinaires");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading veterinaires: " + e.getMessage());
        }
    }
    
    /**
     * Load owners from the database
     */
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
            
            // Update the table
            propTable.setItems(props);
            statusLabel.setText("Status: Loaded " + props.size() + " proprietaires");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading proprietaires: " + e.getMessage());
        }
    }
    
    /**
     * Load animals from the database
     */
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
            
            // Update the table
            animalTable.setItems(animaux);
            statusLabel.setText("Status: Loaded " + animaux.size() + " animaux");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading animaux: " + e.getMessage());
        }
    }
    
    /**
     * Load visits from the database
     */
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
            
            // Update the table
            visiteTable.setItems(visites);
            statusLabel.setText("Status: Loaded " + visites.size() + " visites");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading visites: " + e.getMessage());
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
