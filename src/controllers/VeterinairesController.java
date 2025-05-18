package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Veterinaire;
import VeterinaireService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the veterinarians list view
 */
public class VeterinairesController implements Initializable {

    @FXML
    private TableView<Veterinaire> veterinairesTable;
    
    @FXML
    private TableColumn<Veterinaire, String> nomColumn;
    
    @FXML
    private TableColumn<Veterinaire, String> prenomColumn;
    
    @FXML
    private TableColumn<Veterinaire, String> specialiteColumn;
    
    @FXML
    private TableColumn<Veterinaire, String> emailColumn;
    
    @FXML
    private TableColumn<Veterinaire, String> telephoneColumn;
    
    @FXML
    private ComboBox<String> specialiteFilter;
    
    private VeterinaireService veterinairesService = new VeterinaireService();
    private ObservableList<Veterinaire> veterinairesData;
    private FilteredList<Veterinaire> filteredVeterinaires;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        // Load data from service
        veterinairesData = veterinairesService.getAllVeterinaires();
        
        // Create filtered list
        filteredVeterinaires = new FilteredList<>(veterinairesData, p -> true);
        veterinairesTable.setItems(filteredVeterinaires);
        
        // Set up the specialty filter
        ObservableList<String> specialites = FXCollections.observableArrayList();
        specialites.add("Toutes les spécialités");
        
        // Extract unique specialties from veterinarians
        veterinairesData.forEach(v -> {
            if (!specialites.contains(v.getSpecialite())) {
                specialites.add(v.getSpecialite());
            }
        });
        
        specialiteFilter.setItems(specialites);
        specialiteFilter.setValue("Toutes les spécialités");
        
        // Add listener to the filter combobox
        specialiteFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.equals("Toutes les spécialités")) {
                filteredVeterinaires.setPredicate(p -> true);
            } else {
                filteredVeterinaires.setPredicate(p -> p.getSpecialite().equals(newVal));
            }
        });
    }
    
    /**
     * Handle refresh button
     */
    @FXML
    private void refreshVeterinaires() {
        // Fetch fresh data from the service
        System.out.println("Refreshing veterinarians data...");
        
        // Get updated data from the service
        veterinairesData = veterinairesService.getAllVeterinaires();
        
        // Update the filtered list with the new data
        filteredVeterinaires = new FilteredList<>(veterinairesData, p -> true);
        veterinairesTable.setItems(filteredVeterinaires);
        
        // Reset the filter
        specialiteFilter.setValue("Toutes les spécialités");
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
