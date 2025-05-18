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
import java.util.Optional;

/**
 * Standalone CRUD application for VetCare360 Veterinarian entity
 */
public class VetCRUD extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // UI Components
    private TableView<Veterinarian> vetTable;
    private TextField idField, lastNameField, firstNameField, specialtyField, emailField, phoneField;
    private Label statusLabel;
    private Button addButton, editButton, deleteButton, refreshButton;

    // Currently selected veterinarian
    private Veterinarian selectedVet;

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

            Label titleLabel = new Label("VetCare 360 - Veterinarians");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            header.getChildren().add(titleLabel);

            // Create table
            vetTable = new TableView<>();
            vetTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Set up table columns
            TableColumn<Veterinarian, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Veterinarian, String> lastNameCol = new TableColumn<>("Nom");
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

            TableColumn<Veterinarian, String> firstNameCol = new TableColumn<>("Prénom");
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

            TableColumn<Veterinarian, String> specialtyCol = new TableColumn<>("Spécialité");
            specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));

            TableColumn<Veterinarian, String> emailCol = new TableColumn<>("Email");
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

            TableColumn<Veterinarian, String> phoneCol = new TableColumn<>("Téléphone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            vetTable.getColumns().addAll(idCol, lastNameCol, firstNameCol, specialtyCol, emailCol, phoneCol);

            // Setup table selection listener
            vetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedVet = newSelection;
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
            deleteButton.setOnAction(e -> deleteVeterinarian());

            refreshButton = new Button("Actualiser");
            refreshButton.setOnAction(e -> loadVeterinarians());

            buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);

            // Create status bar
            HBox statusBar = new HBox(10);
            statusBar.setPadding(new Insets(5));
            statusBar.setStyle("-fx-background-color: #f0f0f0;");

            statusLabel = new Label("Prêt");
            statusBar.getChildren().add(statusLabel);

            // Set up layout
            VBox centerBox = new VBox(10);
            centerBox.getChildren().addAll(vetTable, buttonBox);

            root.setTop(header);
            root.setCenter(centerBox);
            root.setBottom(statusBar);

            // Setup scene and show stage
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("VetCare 360 - CRUD Vétérinaires");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load initial data
            loadVeterinarians();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur au démarrage de l'application: " + e.getMessage());
        }
    }

    /**
     * Create the form for adding/editing veterinarians
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

        // Last name field
        Label lastNameLabel = new Label("Nom:");
        lastNameField = new TextField();
        formPane.add(lastNameLabel, 0, row);
        formPane.add(lastNameField, 1, row);
        row++;

        // First name field
        Label firstNameLabel = new Label("Prénom:");
        firstNameField = new TextField();
        formPane.add(firstNameLabel, 0, row);
        formPane.add(firstNameField, 1, row);
        row++;

        // Specialty field
        Label specialtyLabel = new Label("Spécialité:");
        specialtyField = new TextField();
        formPane.add(specialtyLabel, 0, row);
        formPane.add(specialtyField, 1, row);
        row++;

        // Email field
        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        formPane.add(emailLabel, 0, row);
        formPane.add(emailField, 1, row);
        row++;

        // Phone field
        Label phoneLabel = new Label("Téléphone:");
        phoneField = new TextField();
        formPane.add(phoneLabel, 0, row);
        formPane.add(phoneField, 1, row);

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
                
                // Check if we need to add sample data
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
                    System.out.println("Added sample veterinarian data");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Load veterinarians from database
     */
    private void loadVeterinarians() {
        try {
            ObservableList<Veterinarian> vets = FXCollections.observableArrayList();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM veterinaires")) {

                while (rs.next()) {
                    Veterinarian vet = new Veterinarian(
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

            vetTable.setItems(vets);
            statusLabel.setText("Chargé " + vets.size() + " vétérinaires");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading veterinarians: " + e.getMessage());
        }
    }

    /**
     * Show dialog for adding a new veterinarian
     */
    private void showAddDialog() {
        // Create dialog
        Dialog<Veterinarian> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un vétérinaire");
        dialog.setHeaderText("Entrez les informations du nouveau vétérinaire");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lastName = new TextField();
        lastName.setPromptText("Nom");
        TextField firstName = new TextField();
        firstName.setPromptText("Prénom");
        TextField specialty = new TextField();
        specialty.setPromptText("Spécialité");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField phone = new TextField();
        phone.setPromptText("Téléphone");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(lastName, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(firstName, 1, 1);
        grid.add(new Label("Spécialité:"), 0, 2);
        grid.add(specialty, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(phone, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        lastName.requestFocus();

        // Convert the result to a veterinarian object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Veterinarian newVet = new Veterinarian(
                        0, // ID will be assigned by database
                        lastName.getText(),
                        firstName.getText(),
                        specialty.getText(),
                        email.getText(),
                        phone.getText()
                );
                return newVet;
            }
            return null;
        });

        Optional<Veterinarian> result = dialog.showAndWait();

        result.ifPresent(vet -> {
            addVeterinarian(vet);
        });
    }

    /**
     * Add a new veterinarian to the database
     */
    private void addVeterinarian(Veterinarian vet) {
        try {
            String sql = "INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, vet.getLastName());
                pstmt.setString(2, vet.getFirstName());
                pstmt.setString(3, vet.getSpecialty());
                pstmt.setString(4, vet.getEmail());
                pstmt.setString(5, vet.getPhone());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Vétérinaire ajouté avec succès");
                    loadVeterinarians(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été insérée");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout du vétérinaire: " + e.getMessage());
        }
    }

    /**
     * Show dialog for editing an existing veterinarian
     */
    private void showEditDialog() {
        if (selectedVet == null) {
            showError("Veuillez sélectionner un vétérinaire à modifier");
            return;
        }

        // Create dialog
        Dialog<Veterinarian> dialog = new Dialog<>();
        dialog.setTitle("Modifier un vétérinaire");
        dialog.setHeaderText("Modifiez les informations du vétérinaire");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lastName = new TextField(selectedVet.getLastName());
        TextField firstName = new TextField(selectedVet.getFirstName());
        TextField specialty = new TextField(selectedVet.getSpecialty());
        TextField email = new TextField(selectedVet.getEmail());
        TextField phone = new TextField(selectedVet.getPhone());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(lastName, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(firstName, 1, 1);
        grid.add(new Label("Spécialité:"), 0, 2);
        grid.add(specialty, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(email, 1, 3);
        grid.add(new Label("Téléphone:"), 0, 4);
        grid.add(phone, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        lastName.requestFocus();

        // Convert the result to a veterinarian object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Veterinarian updatedVet = new Veterinarian(
                        selectedVet.getId(),
                        lastName.getText(),
                        firstName.getText(),
                        specialty.getText(),
                        email.getText(),
                        phone.getText()
                );
                return updatedVet;
            }
            return null;
        });

        Optional<Veterinarian> result = dialog.showAndWait();

        result.ifPresent(vet -> {
            updateVeterinarian(vet);
        });
    }

    /**
     * Update an existing veterinarian in the database
     */
    private void updateVeterinarian(Veterinarian vet) {
        try {
            String sql = "UPDATE veterinaires SET nom=?, prenom=?, specialite=?, email=?, telephone=? WHERE id=?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, vet.getLastName());
                pstmt.setString(2, vet.getFirstName());
                pstmt.setString(3, vet.getSpecialty());
                pstmt.setString(4, vet.getEmail());
                pstmt.setString(5, vet.getPhone());
                pstmt.setInt(6, vet.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Vétérinaire mis à jour avec succès");
                    loadVeterinarians(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été mise à jour");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la mise à jour du vétérinaire: " + e.getMessage());
        }
    }

    /**
     * Delete a veterinarian from the database
     */
    private void deleteVeterinarian() {
        if (selectedVet == null) {
            showError("Veuillez sélectionner un vétérinaire à supprimer");
            return;
        }

        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le vétérinaire: " + selectedVet.getFirstName() + " " + selectedVet.getLastName());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce vétérinaire?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                String sql = "DELETE FROM veterinaires WHERE id=?";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, selectedVet.getId());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        statusLabel.setText("Vétérinaire supprimé avec succès");
                        loadVeterinarians(); // Refresh table
                    } else {
                        statusLabel.setText("Erreur: Aucune ligne n'a été supprimée");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression du vétérinaire: " + e.getMessage());
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
     * Model class for a Veterinarian
     */
    public static class Veterinarian {
        private final int id;
        private final String lastName;
        private final String firstName;
        private final String specialty;
        private final String email;
        private final String phone;

        public Veterinarian(int id, String lastName, String firstName, String specialty, String email, String phone) {
            this.id = id;
            this.lastName = lastName;
            this.firstName = firstName;
            this.specialty = specialty;
            this.email = email;
            this.phone = phone;
        }

        public int getId() { return id; }
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public String getSpecialty() { return specialty; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
