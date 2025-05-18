package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Proprietaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the owner search view
 */
public class ProprietaireRechercheController implements Initializable {

    @FXML
    private TextField nomSearchField;
    
    @FXML
    private TableView<Proprietaire> proprietairesTable;
    
    @FXML
    private TableColumn<Proprietaire, String> nomColumn;
    
    @FXML
    private TableColumn<Proprietaire, String> prenomColumn;
    
    @FXML
    private TableColumn<Proprietaire, String> adresseColumn;
    
    @FXML
    private TableColumn<Proprietaire, String> telephoneColumn;
    
    @FXML
    private TableColumn<Proprietaire, Void> actionsColumn;
    
    // Sample data - in a real app this would come from the backend
    private ObservableList<Proprietaire> proprietairesSample = FXCollections.observableArrayList(
        new Proprietaire(1, "Martin", "Jean", "15 Rue des Fleurs, 75001 Paris", "01 23 45 67 89"),
        new Proprietaire(2, "Dubois", "Marie", "27 Avenue Victor Hugo, 69002 Lyon", "04 56 78 90 12"),
        new Proprietaire(3, "Petit", "Sophie", "8 Rue du Commerce, 44000 Nantes", "02 34 56 78 90"),
        new Proprietaire(4, "Bernard", "Thomas", "5 Place de la Libération, 33000 Bordeaux", "05 67 89 01 23"),
        new Proprietaire(5, "Robert", "Claire", "12 Boulevard des Alpes, 38000 Grenoble", "04 76 54 32 10")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        // Set up the action buttons in the table
        setupActionsColumn();
        
        // Initially the table is empty until the user searches
        proprietairesTable.setItems(FXCollections.observableArrayList());
    }
    
    /**
     * Sets up the actions column with View and Edit buttons
     */
    private void setupActionsColumn() {
        Callback<TableColumn<Proprietaire, Void>, TableCell<Proprietaire, Void>> cellFactory = 
                new Callback<TableColumn<Proprietaire, Void>, TableCell<Proprietaire, Void>>() {
            @Override
            public TableCell<Proprietaire, Void> call(TableColumn<Proprietaire, Void> param) {
                return new TableCell<Proprietaire, Void>() {
                    private final Button viewButton = new Button("Voir détails");
                    private final Button editButton = new Button("Modifier");
                    private final HBox pane = new HBox(5, viewButton, editButton);
                    
                    {
                        viewButton.setOnAction(event -> {
                            Proprietaire proprietaire = getTableView().getItems().get(getIndex());
                            viewProprietaireDetails(proprietaire);
                        });
                        
                        editButton.setOnAction(event -> {
                            Proprietaire proprietaire = getTableView().getItems().get(getIndex());
                            editProprietaire(proprietaire);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        
        actionsColumn.setCellFactory(cellFactory);
    }
    
    /**
     * Handle the search button action
     */
    @FXML
    private void rechercherProprietaire() {
        String searchTerm = nomSearchField.getText().trim().toLowerCase();
        
        if (searchTerm.isEmpty()) {
            proprietairesTable.setItems(FXCollections.observableArrayList());
            return;
        }
        
        // Filter the proprietaires based on search term (case insensitive)
        ObservableList<Proprietaire> filteredList = FXCollections.observableArrayList();
        
        for (Proprietaire p : proprietairesSample) {
            if (p.getNom().toLowerCase().contains(searchTerm)) {
                filteredList.add(p);
            }
        }
        
        proprietairesTable.setItems(filteredList);
    }
    
    /**
     * Navigate to add new owner view
     */
    @FXML
    private void ajouterNouveauProprietaire(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/ajouter_proprietaire.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Ajouter Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to add owner view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to owner details view
     */
    private void viewProprietaireDetails(Proprietaire proprietaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/details_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire to display
            DetailsProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) proprietairesTable.getScene().getWindow();
            stage.setTitle("VetCare 360 - Détails du Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to owner details view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to edit owner view
     */
    private void editProprietaire(Proprietaire proprietaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modifier_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire to edit
            ModifierProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) proprietairesTable.getScene().getWindow();
            stage.setTitle("VetCare 360 - Modifier Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to edit owner view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate back to the home page
     */
    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/home.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Gestion Clinique Vétérinaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to home view");
            e.printStackTrace();
        }
    }
}
