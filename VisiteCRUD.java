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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Standalone CRUD application for VetCare360 Visite (Visit) entity
 */
public class VisiteCRUD extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // UI Components
    private TableView<Visite> visiteTable;
    private DatePicker datePicker;
    private TextField motifField, diagnosticField, traitementField;
    private ComboBox<String> animalComboBox, vetComboBox;
    private Label statusLabel;
    private Button addButton, editButton, deleteButton, refreshButton;

    // Currently selected visite
    private Visite selectedVisite;
    
    // Maps to store IDs by name
    private Map<String, Integer> animalMap = new HashMap<>();
    private Map<String, Integer> vetMap = new HashMap<>();

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

            Label titleLabel = new Label("VetCare 360 - Visites");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            header.getChildren().add(titleLabel);

            // Create table
            visiteTable = new TableView<>();
            visiteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

            TableColumn<Visite, String> animalCol = new TableColumn<>("Animal");
            animalCol.setCellValueFactory(new PropertyValueFactory<>("animalName"));
            
            TableColumn<Visite, String> vetCol = new TableColumn<>("Vétérinaire");
            vetCol.setCellValueFactory(new PropertyValueFactory<>("vetName"));

            visiteTable.getColumns().addAll(idCol, dateCol, motifCol, diagnosticCol, traitementCol, animalCol, vetCol);

            // Setup table selection listener
            visiteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedVisite = newSelection;
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
            deleteButton.setOnAction(e -> deleteVisite());

            refreshButton = new Button("Actualiser");
            refreshButton.setOnAction(e -> loadVisites());

            buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);

            // Create status bar
            HBox statusBar = new HBox(10);
            statusBar.setPadding(new Insets(5));
            statusBar.setStyle("-fx-background-color: #f0f0f0;");

            statusLabel = new Label("Prêt");
            statusBar.getChildren().add(statusLabel);

            // Set up layout
            VBox centerBox = new VBox(10);
            centerBox.getChildren().addAll(visiteTable, buttonBox);

            root.setTop(header);
            root.setCenter(centerBox);
            root.setBottom(statusBar);

            // Setup scene and show stage
            Scene scene = new Scene(root, 1000, 600);
            primaryStage.setTitle("VetCare 360 - CRUD Visites");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load initial data
            loadVisites();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur au démarrage de l'application: " + e.getMessage());
        }
    }

    /**
     * Create the form for adding/editing visites
     */
    private GridPane createFormPane() {
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(20));

        int row = 0;

        // Date field
        Label dateLabel = new Label("Date:");
        datePicker = new DatePicker();
        formPane.add(dateLabel, 0, row);
        formPane.add(datePicker, 1, row);
        row++;

        // Motif field
        Label motifLabel = new Label("Motif:");
        motifField = new TextField();
        formPane.add(motifLabel, 0, row);
        formPane.add(motifField, 1, row);
        row++;

        // Diagnostic field
        Label diagnosticLabel = new Label("Diagnostic:");
        diagnosticField = new TextField();
        formPane.add(diagnosticLabel, 0, row);
        formPane.add(diagnosticField, 1, row);
        row++;

        // Traitement field
        Label traitementLabel = new Label("Traitement:");
        traitementField = new TextField();
        formPane.add(traitementLabel, 0, row);
        formPane.add(traitementField, 1, row);
        row++;

        // Animal field
        Label animalLabel = new Label("Animal:");
        animalComboBox = new ComboBox<>();
        formPane.add(animalLabel, 0, row);
        formPane.add(animalComboBox, 1, row);
        row++;

        // Veterinaire field
        Label vetLabel = new Label("Vétérinaire:");
        vetComboBox = new ComboBox<>();
        formPane.add(vetLabel, 0, row);
        formPane.add(vetComboBox, 1, row);

        return formPane;
    }

    /**
     * Initialize the database
     */
    private void initializeDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("Connected to database");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Load visites from database
     */
    private void loadVisites() {
        try {
            ObservableList<Visite> visites = FXCollections.observableArrayList();
            
            // First load animals and vets for the dropdowns
            loadAnimals();
            loadVeterinaires();
            
            // Build query with joins to get animal and vet names
            String query = "SELECT v.*, " +
                           "a.nom as animal_nom, " +
                           "vet.prenom as vet_prenom, vet.nom as vet_nom " +
                           "FROM visites v " +
                           "LEFT JOIN animaux a ON v.animal_id = a.id " +
                           "LEFT JOIN veterinaires vet ON v.veterinaire_id = vet.id";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    // Create animal name string if animal exists
                    String animalName = null;
                    if (rs.getObject("animal_id") != null) {
                        animalName = rs.getString("animal_nom");
                    }
                    
                    // Create vet name string if vet exists
                    String vetName = null;
                    if (rs.getObject("veterinaire_id") != null) {
                        vetName = rs.getString("vet_prenom") + " " + rs.getString("vet_nom");
                    }
                    
                    java.sql.Date sqlDate = rs.getDate("date");
                    LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                    
                    Visite visite = new Visite(
                            rs.getInt("id"),
                            date,
                            rs.getString("motif"),
                            rs.getString("diagnostic"),
                            rs.getString("traitement"),
                            rs.getObject("animal_id") != null ? rs.getInt("animal_id") : null,
                            rs.getObject("veterinaire_id") != null ? rs.getInt("veterinaire_id") : null,
                            animalName,
                            vetName
                    );
                    visites.add(visite);
                }
            }

            visiteTable.setItems(visites);
            statusLabel.setText("Chargé " + visites.size() + " visites");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading visites: " + e.getMessage());
        }
    }
    
    /**
     * Load animals for the dropdown
     */
    private void loadAnimals() {
        try {
            animalMap.clear();
            ObservableList<String> animalNames = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, nom FROM animaux")) {
                
                while (rs.next()) {
                    String name = rs.getString("nom");
                    animalNames.add(name);
                    animalMap.put(name, rs.getInt("id"));
                }
            }
            
            animalComboBox.setItems(animalNames);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading animals: " + e.getMessage());
        }
    }
    
    /**
     * Load veterinaires for the dropdown
     */
    private void loadVeterinaires() {
        try {
            vetMap.clear();
            ObservableList<String> vetNames = FXCollections.observableArrayList();
            
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom FROM veterinaires")) {
                
                while (rs.next()) {
                    String fullName = rs.getString("prenom") + " " + rs.getString("nom");
                    vetNames.add(fullName);
                    vetMap.put(fullName, rs.getInt("id"));
                }
            }
            
            vetComboBox.setItems(vetNames);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading veterinaires: " + e.getMessage());
        }
    }

    /**
     * Show dialog for adding a new visite
     */
    private void showAddDialog() {
        // Create dialog
        Dialog<Visite> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une visite");
        dialog.setHeaderText("Entrez les informations de la nouvelle visite");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker date = new DatePicker(LocalDate.now());
        TextField motif = new TextField();
        motif.setPromptText("Motif de la visite");
        TextField diagnostic = new TextField();
        diagnostic.setPromptText("Diagnostic");
        TextField traitement = new TextField();
        traitement.setPromptText("Traitement");
        ComboBox<String> animalDropdown = new ComboBox<>(animalComboBox.getItems());
        animalDropdown.setPromptText("Sélectionner un animal");
        ComboBox<String> vetDropdown = new ComboBox<>(vetComboBox.getItems());
        vetDropdown.setPromptText("Sélectionner un vétérinaire");

        grid.add(new Label("Date:"), 0, 0);
        grid.add(date, 1, 0);
        grid.add(new Label("Motif:"), 0, 1);
        grid.add(motif, 1, 1);
        grid.add(new Label("Diagnostic:"), 0, 2);
        grid.add(diagnostic, 1, 2);
        grid.add(new Label("Traitement:"), 0, 3);
        grid.add(traitement, 1, 3);
        grid.add(new Label("Animal:"), 0, 4);
        grid.add(animalDropdown, 1, 4);
        grid.add(new Label("Vétérinaire:"), 0, 5);
        grid.add(vetDropdown, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        motif.requestFocus();

        // Convert the result to a visite object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (motif.getText().isEmpty()) {
                    showError("Le motif est obligatoire");
                    return null;
                }
                
                if (animalDropdown.getValue() == null) {
                    showError("Veuillez sélectionner un animal");
                    return null;
                }
                
                Integer animalId = animalMap.get(animalDropdown.getValue());
                Integer vetId = null;
                if (vetDropdown.getValue() != null) {
                    vetId = vetMap.get(vetDropdown.getValue());
                }
                
                Visite newVisite = new Visite(
                        0, // ID will be assigned by database
                        date.getValue(),
                        motif.getText(),
                        diagnostic.getText(),
                        traitement.getText(),
                        animalId,
                        vetId,
                        animalDropdown.getValue(),
                        vetDropdown.getValue()
                );
                return newVisite;
            }
            return null;
        });

        Optional<Visite> result = dialog.showAndWait();

        result.ifPresent(visite -> {
            addVisite(visite);
        });
    }

    /**
     * Add a new visite to the database
     */
    private void addVisite(Visite visite) {
        try {
            String sql = "INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(visite.getDate()));
                pstmt.setString(2, visite.getMotif());
                pstmt.setString(3, visite.getDiagnostic());
                pstmt.setString(4, visite.getTraitement());
                
                if (visite.getAnimalId() != null) {
                    pstmt.setInt(5, visite.getAnimalId());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }
                
                if (visite.getVetId() != null) {
                    pstmt.setInt(6, visite.getVetId());
                } else {
                    pstmt.setNull(6, Types.INTEGER);
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Visite ajoutée avec succès");
                    loadVisites(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été insérée");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout de la visite: " + e.getMessage());
        }
    }

    /**
     * Show dialog for editing an existing visite
     */
    private void showEditDialog() {
        if (selectedVisite == null) {
            showError("Veuillez sélectionner une visite à modifier");
            return;
        }

        // Create dialog
        Dialog<Visite> dialog = new Dialog<>();
        dialog.setTitle("Modifier une visite");
        dialog.setHeaderText("Modifiez les informations de la visite");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker date = new DatePicker(selectedVisite.getDate());
        TextField motif = new TextField(selectedVisite.getMotif());
        TextField diagnostic = new TextField(selectedVisite.getDiagnostic());
        TextField traitement = new TextField(selectedVisite.getTraitement());
        ComboBox<String> animalDropdown = new ComboBox<>(animalComboBox.getItems());
        animalDropdown.setValue(selectedVisite.getAnimalName());
        ComboBox<String> vetDropdown = new ComboBox<>(vetComboBox.getItems());
        vetDropdown.setValue(selectedVisite.getVetName());

        grid.add(new Label("Date:"), 0, 0);
        grid.add(date, 1, 0);
        grid.add(new Label("Motif:"), 0, 1);
        grid.add(motif, 1, 1);
        grid.add(new Label("Diagnostic:"), 0, 2);
        grid.add(diagnostic, 1, 2);
        grid.add(new Label("Traitement:"), 0, 3);
        grid.add(traitement, 1, 3);
        grid.add(new Label("Animal:"), 0, 4);
        grid.add(animalDropdown, 1, 4);
        grid.add(new Label("Vétérinaire:"), 0, 5);
        grid.add(vetDropdown, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        motif.requestFocus();

        // Convert the result to a visite object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (motif.getText().isEmpty()) {
                    showError("Le motif est obligatoire");
                    return null;
                }
                
                if (animalDropdown.getValue() == null) {
                    showError("Veuillez sélectionner un animal");
                    return null;
                }
                
                Integer animalId = animalMap.get(animalDropdown.getValue());
                Integer vetId = null;
                if (vetDropdown.getValue() != null) {
                    vetId = vetMap.get(vetDropdown.getValue());
                }
                
                Visite updatedVisite = new Visite(
                        selectedVisite.getId(),
                        date.getValue(),
                        motif.getText(),
                        diagnostic.getText(),
                        traitement.getText(),
                        animalId,
                        vetId,
                        animalDropdown.getValue(),
                        vetDropdown.getValue()
                );
                return updatedVisite;
            }
            return null;
        });

        Optional<Visite> result = dialog.showAndWait();

        result.ifPresent(visite -> {
            updateVisite(visite);
        });
    }

    /**
     * Update an existing visite in the database
     */
    private void updateVisite(Visite visite) {
        try {
            String sql = "UPDATE visites SET date=?, motif=?, diagnostic=?, traitement=?, animal_id=?, veterinaire_id=? WHERE id=?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(visite.getDate()));
                pstmt.setString(2, visite.getMotif());
                pstmt.setString(3, visite.getDiagnostic());
                pstmt.setString(4, visite.getTraitement());
                
                if (visite.getAnimalId() != null) {
                    pstmt.setInt(5, visite.getAnimalId());
                } else {
                    pstmt.setNull(5, Types.INTEGER);
                }
                
                if (visite.getVetId() != null) {
                    pstmt.setInt(6, visite.getVetId());
                } else {
                    pstmt.setNull(6, Types.INTEGER);
                }
                
                pstmt.setInt(7, visite.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Visite mise à jour avec succès");
                    loadVisites(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été mise à jour");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la mise à jour de la visite: " + e.getMessage());
        }
    }

    /**
     * Delete a visite from the database
     */
    private void deleteVisite() {
        if (selectedVisite == null) {
            showError("Veuillez sélectionner une visite à supprimer");
            return;
        }

        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la visite: " + selectedVisite.getMotif());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette visite?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                String sql = "DELETE FROM visites WHERE id=?";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, selectedVisite.getId());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        statusLabel.setText("Visite supprimée avec succès");
                        loadVisites(); // Refresh table
                    } else {
                        statusLabel.setText("Erreur: Aucune ligne n'a été supprimée");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression de la visite: " + e.getMessage());
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
     * Model class for a Visite (Visit)
     */
    public static class Visite {
        private final int id;
        private final LocalDate date;
        private final String motif;
        private final String diagnostic;
        private final String traitement;
        private final Integer animalId; // Can be null
        private final String animalName; // Can be null
        private final Integer vetId; // Can be null
        private final String vetName; // Can be null

        public Visite(int id, LocalDate date, String motif, String diagnostic, String traitement, 
                      Integer animalId, Integer vetId, String animalName, String vetName) {
            this.id = id;
            this.date = date;
            this.motif = motif;
            this.diagnostic = diagnostic;
            this.traitement = traitement;
            this.animalId = animalId;
            this.vetId = vetId;
            this.animalName = animalName;
            this.vetName = vetName;
        }

        public int getId() { return id; }
        public LocalDate getDate() { return date; }
        public String getMotif() { return motif; }
        public String getDiagnostic() { return diagnostic; }
        public String getTraitement() { return traitement; }
        public Integer getAnimalId() { return animalId; }
        public Integer getVetId() { return vetId; }
        public String getAnimalName() { return animalName; }
        public String getVetName() { return vetName; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
