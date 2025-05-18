import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Standalone CRUD application for VetCare360 Animal (Pet) entity
 */
public class AnimalCRUD extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // UI Components
    private TableView<Animal> animalTable;
    private TextField idField, nameField, speciesField, sexField, ageField;
    private ComboBox<String> ownerComboBox;
    private Label statusLabel;
    private Button addButton, editButton, deleteButton, refreshButton;

    // Currently selected animal
    private Animal selectedAnimal;
    
    // Map to store owner IDs by name
    private Map<String, Integer> ownerMap = new HashMap<>();

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
            header.setStyle("-fx-background-color: #167e74;");

            Label titleLabel = new Label("VetCare 360 - Animaux");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            header.getChildren().add(titleLabel);

            // Create table
            animalTable = new TableView<>();
            animalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Set up table columns
            TableColumn<Animal, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Animal, String> nameCol = new TableColumn<>("Nom");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Animal, String> speciesCol = new TableColumn<>("Espèce");
            speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));

            TableColumn<Animal, String> sexCol = new TableColumn<>("Sexe");
            sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));

            TableColumn<Animal, Integer> ageCol = new TableColumn<>("Âge");
            ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
            
            TableColumn<Animal, String> ownerCol = new TableColumn<>("Propriétaire");
            ownerCol.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

            animalTable.getColumns().addAll(idCol, nameCol, speciesCol, sexCol, ageCol, ownerCol);

            // Setup table selection listener
            animalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedAnimal = newSelection;
            });

            // Create form panel
            GridPane formPane = createFormPane();

            // Create buttons
            HBox buttonBox = new HBox(10);
            buttonBox.setPadding(new Insets(10));

            addButton = new Button("Ajouter");
            addButton.setOnAction(e -> showAddDialog());

            editButton = new Button("Modifier");
            editButton.setOnAction(e -> showEditDialog());

            deleteButton = new Button("Supprimer");
            deleteButton.setOnAction(e -> deleteAnimal());

            refreshButton = new Button("Actualiser");
            refreshButton.setOnAction(e -> loadAnimals());

            buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);

            // Create status bar
            HBox statusBar = new HBox(10);
            statusBar.setPadding(new Insets(5));
            statusBar.setStyle("-fx-background-color: #f0f0f0;");

            statusLabel = new Label("Prêt");
            statusBar.getChildren().add(statusLabel);

            // Set up layout
            VBox centerBox = new VBox(10);
            centerBox.getChildren().addAll(animalTable, buttonBox);

            root.setTop(header);
            root.setCenter(centerBox);
            root.setBottom(statusBar);

            // Setup scene and show stage
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("VetCare 360 - CRUD Animaux");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load initial data
            loadAnimals();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur au démarrage de l'application: " + e.getMessage());
        }
    }

    /**
     * Create the form for adding/editing animals
     */
    private GridPane createFormPane() {
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(20));

        int row = 0;

        // ID field (hidden for new records)
        Label idLabel = new Label("ID:");
        idField = new TextField();
        idField.setEditable(false);
        formPane.add(idLabel, 0, row);
        formPane.add(idField, 1, row);
        row++;

        // Name field
        Label nameLabel = new Label("Nom:");
        nameField = new TextField();
        formPane.add(nameLabel, 0, row);
        formPane.add(nameField, 1, row);
        row++;

        // Species field
        Label speciesLabel = new Label("Espèce:");
        speciesField = new TextField();
        formPane.add(speciesLabel, 0, row);
        formPane.add(speciesField, 1, row);
        row++;

        // Sex field
        Label sexLabel = new Label("Sexe:");
        sexField = new TextField();
        formPane.add(sexLabel, 0, row);
        formPane.add(sexField, 1, row);
        row++;

        // Age field
        Label ageLabel = new Label("Âge:");
        ageField = new TextField();
        formPane.add(ageLabel, 0, row);
        formPane.add(ageField, 1, row);
        row++;

        // Owner field
        Label ownerLabel = new Label("Propriétaire:");
        ownerComboBox = new ComboBox<>();
        formPane.add(ownerLabel, 0, row);
        formPane.add(ownerComboBox, 1, row);

        return formPane;
    }

    /**
     * Initialize the database
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
                
                // Check if we need to add sample data
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM animaux");
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Load animals from database
     */
    private void loadAnimals() {
        try {
            ObservableList<Animal> animals = FXCollections.observableArrayList();
            
            // First load owners for the dropdown
            loadOwners();
            
            // Build query with join to get owner names
            String query = "SELECT a.*, p.nom as proprio_nom, p.prenom as proprio_prenom " +
                          "FROM animaux a " +
                          "LEFT JOIN proprietaires p ON a.proprietaire_id = p.id";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    // Create owner name string if owner exists
                    String ownerName = null;
                    if (rs.getObject("proprietaire_id") != null) {
                        ownerName = rs.getString("proprio_prenom") + " " + rs.getString("proprio_nom");
                    }
                    
                    Animal animal = new Animal(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("espece"),
                            rs.getString("sexe"),
                            rs.getInt("age"),
                            rs.getObject("proprietaire_id") != null ? rs.getInt("proprietaire_id") : null,
                            ownerName
                    );
                    animals.add(animal);
                }
            }

            animalTable.setItems(animals);
            statusLabel.setText("Chargé " + animals.size() + " animaux");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading animals: " + e.getMessage());
        }
    }
    
    /**
     * Load owners for the dropdown
     */
    private void loadOwners() {
        try {
            ownerMap.clear();
            ObservableList<String> ownerNames = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom FROM proprietaires")) {
                
                while (rs.next()) {
                    String fullName = rs.getString("prenom") + " " + rs.getString("nom");
                    ownerNames.add(fullName);
                    ownerMap.put(fullName, rs.getInt("id"));
                }
            }
            
            ownerComboBox.setItems(ownerNames);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading owners: " + e.getMessage());
        }
    }

    /**
     * Show dialog for adding a new animal
     */
    private void showAddDialog() {
        // Create dialog
        Dialog<Animal> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un animal");
        dialog.setHeaderText("Entrez les informations du nouvel animal");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Nom");
        TextField species = new TextField();
        species.setPromptText("Espèce");
        TextField sex = new TextField();
        sex.setPromptText("Sexe");
        TextField age = new TextField();
        age.setPromptText("Âge");
        ComboBox<String> ownerDropdown = new ComboBox<>(ownerComboBox.getItems());
        ownerDropdown.setPromptText("Sélectionner un propriétaire");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Espèce:"), 0, 1);
        grid.add(species, 1, 1);
        grid.add(new Label("Sexe:"), 0, 2);
        grid.add(sex, 1, 2);
        grid.add(new Label("Âge:"), 0, 3);
        grid.add(age, 1, 3);
        grid.add(new Label("Propriétaire:"), 0, 4);
        grid.add(ownerDropdown, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        name.requestFocus();

        // Convert the result to an animal object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Integer ownerId = null;
                    String ownerName = ownerDropdown.getValue();
                    if (ownerName != null && !ownerName.isEmpty()) {
                        ownerId = ownerMap.get(ownerName);
                    }
                    
                    Animal newAnimal = new Animal(
                            0, // ID will be assigned by database
                            name.getText(),
                            species.getText(),
                            sex.getText(),
                            age.getText().isEmpty() ? 0 : Integer.parseInt(age.getText()),
                            ownerId,
                            ownerName
                    );
                    return newAnimal;
                } catch (NumberFormatException e) {
                    showError("L'âge doit être un nombre entier");
                    return null;
                }
            }
            return null;
        });

        Optional<Animal> result = dialog.showAndWait();

        result.ifPresent(animal -> {
            addAnimal(animal);
        });
    }

    /**
     * Add a new animal to the database
     */
    private void addAnimal(Animal animal) {
        try {
            String sql = "INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, animal.getName());
                pstmt.setString(2, animal.getSpecies());
                pstmt.setString(3, animal.getSex());
                pstmt.setInt(4, animal.getAge());
                
                if (animal.getOwnerId() != null) {
                    pstmt.setInt(5, animal.getOwnerId());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Animal ajouté avec succès");
                    loadAnimals(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été insérée");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout de l'animal: " + e.getMessage());
        }
    }

    /**
     * Show dialog for editing an existing animal
     */
    private void showEditDialog() {
        if (selectedAnimal == null) {
            showError("Veuillez sélectionner un animal à modifier");
            return;
        }

        // Create dialog
        Dialog<Animal> dialog = new Dialog<>();
        dialog.setTitle("Modifier un animal");
        dialog.setHeaderText("Modifiez les informations de l'animal");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField(selectedAnimal.getName());
        TextField species = new TextField(selectedAnimal.getSpecies());
        TextField sex = new TextField(selectedAnimal.getSex());
        TextField age = new TextField(String.valueOf(selectedAnimal.getAge()));
        ComboBox<String> ownerDropdown = new ComboBox<>(ownerComboBox.getItems());
        ownerDropdown.setValue(selectedAnimal.getOwnerName());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Espèce:"), 0, 1);
        grid.add(species, 1, 1);
        grid.add(new Label("Sexe:"), 0, 2);
        grid.add(sex, 1, 2);
        grid.add(new Label("Âge:"), 0, 3);
        grid.add(age, 1, 3);
        grid.add(new Label("Propriétaire:"), 0, 4);
        grid.add(ownerDropdown, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        name.requestFocus();

        // Convert the result to an animal object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Integer ownerId = null;
                    String ownerName = ownerDropdown.getValue();
                    if (ownerName != null && !ownerName.isEmpty()) {
                        ownerId = ownerMap.get(ownerName);
                    }
                    
                    Animal updatedAnimal = new Animal(
                            selectedAnimal.getId(),
                            name.getText(),
                            species.getText(),
                            sex.getText(),
                            age.getText().isEmpty() ? 0 : Integer.parseInt(age.getText()),
                            ownerId,
                            ownerName
                    );
                    return updatedAnimal;
                } catch (NumberFormatException e) {
                    showError("L'âge doit être un nombre entier");
                    return null;
                }
            }
            return null;
        });

        Optional<Animal> result = dialog.showAndWait();

        result.ifPresent(animal -> {
            updateAnimal(animal);
        });
    }

    /**
     * Update an existing animal in the database
     */
    private void updateAnimal(Animal animal) {
        try {
            String sql = "UPDATE animaux SET nom=?, espece=?, sexe=?, age=?, proprietaire_id=? WHERE id=?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, animal.getName());
                pstmt.setString(2, animal.getSpecies());
                pstmt.setString(3, animal.getSex());
                pstmt.setInt(4, animal.getAge());
                
                if (animal.getOwnerId() != null) {
                    pstmt.setInt(5, animal.getOwnerId());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }
                
                pstmt.setInt(6, animal.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Animal mis à jour avec succès");
                    loadAnimals(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été mise à jour");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la mise à jour de l'animal: " + e.getMessage());
        }
    }

    /**
     * Delete a animal from the database
     */
    private void deleteAnimal() {
        if (selectedAnimal == null) {
            showError("Veuillez sélectionner un animal à supprimer");
            return;
        }

        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'animal: " + selectedAnimal.getName());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet animal? Cette action supprimera également toutes les visites associées.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                String sql = "DELETE FROM animaux WHERE id=?";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, selectedAnimal.getId());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        statusLabel.setText("Animal supprimé avec succès");
                        loadAnimals(); // Refresh table
                    } else {
                        statusLabel.setText("Erreur: Aucune ligne n'a été supprimée");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression de l'animal: " + e.getMessage());
            }
        }
    }

    /**
     * Display an error message
     */
    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setStyle("-fx-text-fill: red;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Model class for an Animal (Pet)
     */
    public static class Animal {
        private final int id;
        private final String name;
        private final String species;
        private final String sex;
        private final int age;
        private final Integer ownerId; // Can be null
        private final String ownerName; // Can be null

        public Animal(int id, String name, String species, String sex, int age, Integer ownerId, String ownerName) {
            this.id = id;
            this.name = name;
            this.species = species;
            this.sex = sex;
            this.age = age;
            this.ownerId = ownerId;
            this.ownerName = ownerName;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSpecies() { return species; }
        public String getSex() { return sex; }
        public int getAge() { return age; }
        public Integer getOwnerId() { return ownerId; }
        public String getOwnerName() { return ownerName; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
