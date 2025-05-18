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
 * Standalone CRUD application for VetCare360 Proprietaire (Owner) entity
 */
public class PropCRUD extends Application {

    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // UI Components
    private TableView<Proprietaire> propTable;
    private TextField idField, lastNameField, firstNameField, addressField, phoneField;
    private Label statusLabel;
    private Button addButton, editButton, deleteButton, refreshButton;

    // Currently selected proprietaire
    private Proprietaire selectedProp;

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

            Label titleLabel = new Label("VetCare 360 - Proprietaires");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            header.getChildren().add(titleLabel);

            // Create table
            propTable = new TableView<>();
            propTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Set up table columns
            TableColumn<Proprietaire, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Proprietaire, String> lastNameCol = new TableColumn<>("Nom");
            lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

            TableColumn<Proprietaire, String> firstNameCol = new TableColumn<>("Prénom");
            firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

            TableColumn<Proprietaire, String> addressCol = new TableColumn<>("Adresse");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

            TableColumn<Proprietaire, String> phoneCol = new TableColumn<>("Téléphone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            propTable.getColumns().addAll(idCol, lastNameCol, firstNameCol, addressCol, phoneCol);

            // Setup table selection listener
            propTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectedProp = newSelection;
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
            deleteButton.setOnAction(e -> deleteProprietaire());

            refreshButton = new Button("Actualiser");
            refreshButton.setOnAction(e -> loadProprietaires());

            buttonBox.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);

            // Create status bar
            HBox statusBar = new HBox(10);
            statusBar.setPadding(new Insets(5));
            statusBar.setStyle("-fx-background-color: #f0f0f0;");

            statusLabel = new Label("Prêt");
            statusBar.getChildren().add(statusLabel);

            // Set up layout
            VBox centerBox = new VBox(10);
            centerBox.getChildren().addAll(propTable, buttonBox);

            root.setTop(header);
            root.setCenter(centerBox);
            root.setBottom(statusBar);

            // Setup scene and show stage
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("VetCare 360 - CRUD Proprietaires");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Load initial data
            loadProprietaires();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur au démarrage de l'application: " + e.getMessage());
        }
    }

    /**
     * Create the form for adding/editing proprietaires
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

        // Address field
        Label addressLabel = new Label("Adresse:");
        addressField = new TextField();
        formPane.add(addressLabel, 0, row);
        formPane.add(addressField, 1, row);
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
                
                // Check if we need to add sample data
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM proprietaires");
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database initialization error: " + e.getMessage());
        }
    }

    /**
     * Load proprietaires from database
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

            propTable.setItems(props);
            statusLabel.setText("Chargé " + props.size() + " proprietaires");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading proprietaires: " + e.getMessage());
        }
    }

    /**
     * Show dialog for adding a new proprietaire
     */
    private void showAddDialog() {
        // Create dialog
        Dialog<Proprietaire> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un proprietaire");
        dialog.setHeaderText("Entrez les informations du nouveau proprietaire");

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
        TextField address = new TextField();
        address.setPromptText("Adresse");
        TextField phone = new TextField();
        phone.setPromptText("Téléphone");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(lastName, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(firstName, 1, 1);
        grid.add(new Label("Adresse:"), 0, 2);
        grid.add(address, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(phone, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        lastName.requestFocus();

        // Convert the result to a proprietaire object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Proprietaire newProp = new Proprietaire(
                        0, // ID will be assigned by database
                        lastName.getText(),
                        firstName.getText(),
                        address.getText(),
                        phone.getText()
                );
                return newProp;
            }
            return null;
        });

        Optional<Proprietaire> result = dialog.showAndWait();

        result.ifPresent(prop -> {
            addProprietaire(prop);
        });
    }

    /**
     * Add a new proprietaire to the database
     */
    private void addProprietaire(Proprietaire prop) {
        try {
            String sql = "INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES (?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, prop.getLastName());
                pstmt.setString(2, prop.getFirstName());
                pstmt.setString(3, prop.getAddress());
                pstmt.setString(4, prop.getPhone());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Proprietaire ajouté avec succès");
                    loadProprietaires(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été insérée");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ajout du proprietaire: " + e.getMessage());
        }
    }

    /**
     * Show dialog for editing an existing proprietaire
     */
    private void showEditDialog() {
        if (selectedProp == null) {
            showError("Veuillez sélectionner un proprietaire à modifier");
            return;
        }

        // Create dialog
        Dialog<Proprietaire> dialog = new Dialog<>();
        dialog.setTitle("Modifier un proprietaire");
        dialog.setHeaderText("Modifiez les informations du proprietaire");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField lastName = new TextField(selectedProp.getLastName());
        TextField firstName = new TextField(selectedProp.getFirstName());
        TextField address = new TextField(selectedProp.getAddress());
        TextField phone = new TextField(selectedProp.getPhone());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(lastName, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(firstName, 1, 1);
        grid.add(new Label("Adresse:"), 0, 2);
        grid.add(address, 1, 2);
        grid.add(new Label("Téléphone:"), 0, 3);
        grid.add(phone, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field
        lastName.requestFocus();

        // Convert the result to a proprietaire object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Proprietaire updatedProp = new Proprietaire(
                        selectedProp.getId(),
                        lastName.getText(),
                        firstName.getText(),
                        address.getText(),
                        phone.getText()
                );
                return updatedProp;
            }
            return null;
        });

        Optional<Proprietaire> result = dialog.showAndWait();

        result.ifPresent(prop -> {
            updateProprietaire(prop);
        });
    }

    /**
     * Update an existing proprietaire in the database
     */
    private void updateProprietaire(Proprietaire prop) {
        try {
            String sql = "UPDATE proprietaires SET nom=?, prenom=?, adresse=?, telephone=? WHERE id=?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, prop.getLastName());
                pstmt.setString(2, prop.getFirstName());
                pstmt.setString(3, prop.getAddress());
                pstmt.setString(4, prop.getPhone());
                pstmt.setInt(5, prop.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    statusLabel.setText("Proprietaire mis à jour avec succès");
                    loadProprietaires(); // Refresh table
                } else {
                    statusLabel.setText("Erreur: Aucune ligne n'a été mise à jour");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la mise à jour du proprietaire: " + e.getMessage());
        }
    }

    /**
     * Delete a proprietaire from the database
     */
    private void deleteProprietaire() {
        if (selectedProp == null) {
            showError("Veuillez sélectionner un proprietaire à supprimer");
            return;
        }

        // Ask for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le proprietaire: " + selectedProp.getFirstName() + " " + selectedProp.getLastName());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce proprietaire? Cette action supprimera également tous ses animaux associés.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                String sql = "DELETE FROM proprietaires WHERE id=?";

                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, selectedProp.getId());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        statusLabel.setText("Proprietaire supprimé avec succès");
                        loadProprietaires(); // Refresh table
                    } else {
                        statusLabel.setText("Erreur: Aucune ligne n'a été supprimée");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("Erreur lors de la suppression du proprietaire: " + e.getMessage());
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
     * Model class for a Proprietaire (Owner)
     */
    public static class Proprietaire {
        private final int id;
        private final String lastName;
        private final String firstName;
        private final String address;
        private final String phone;

        public Proprietaire(int id, String lastName, String firstName, String address, String phone) {
            this.id = id;
            this.lastName = lastName;
            this.firstName = firstName;
            this.address = address;
            this.phone = phone;
        }

        public int getId() { return id; }
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public String getAddress() { return address; }
        public String getPhone() { return phone; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
