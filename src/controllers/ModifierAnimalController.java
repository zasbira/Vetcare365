package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Animal;
import models.Proprietaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the edit animal form
 */
public class ModifierAnimalController implements Initializable {

    @FXML
    private Label proprietaireLabel;
    
    @FXML
    private TextField nomField;
    
    @FXML
    private ComboBox<String> especeComboBox;
    
    @FXML
    private ComboBox<String> sexeComboBox;
    
    @FXML
    private Spinner<Integer> ageSpinner;
    
    private Animal animal;
    private Proprietaire proprietaire;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup the species dropdown
        especeComboBox.setItems(FXCollections.observableArrayList(
            "Chien", "Chat", "Lapin", "Oiseau", "Reptile", "Rongeur", "Autre"
        ));
        
        // Setup the sex dropdown
        sexeComboBox.setItems(FXCollections.observableArrayList(
            "Mâle", "Femelle"
        ));
        
        // Setup the age spinner (0-30 years)
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0);
        ageSpinner.setValueFactory(valueFactory);
    }
    
    /**
     * Set the animal to edit and initialize form fields
     */
    public void setAnimal(Animal animal, Proprietaire proprietaire) {
        this.animal = animal;
        this.proprietaire = proprietaire;
        
        // Set the proprietaire label
        proprietaireLabel.setText(proprietaire.getPrenom() + " " + proprietaire.getNom());
        
        // Populate form fields with animal data
        nomField.setText(animal.getNom());
        especeComboBox.setValue(animal.getEspece());
        sexeComboBox.setValue(animal.getSexe());
        
        // Set the age spinner value
        SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, animal.getAge());
        ageSpinner.setValueFactory(valueFactory);
    }
    
    /**
     * Handle the update button action
     */
    @FXML
    private void mettreAJourAnimal(ActionEvent event) {
        // Validate form fields
        if (validateForm()) {
            // Update animal with new values
            animal.setNom(nomField.getText().trim());
            animal.setEspece(especeComboBox.getValue());
            animal.setSexe(sexeComboBox.getValue());
            animal.setAge(ageSpinner.getValue());
            
            // In a real app, we would save to the backend here
            
            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise à jour réussie");
            alert.setHeaderText("Animal mis à jour");
            alert.setContentText("Les informations de l'animal ont été mises à jour avec succès.");
            alert.showAndWait();
            
            // Navigate back to owner details
            retourDetails(event);
        }
    }
    
    /**
     * Validate the form fields
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (nomField.getText().trim().isEmpty()) {
            errors.append("- Le nom est obligatoire\n");
        }
        
        if (especeComboBox.getValue() == null) {
            errors.append("- L'espèce est obligatoire\n");
        }
        
        if (sexeComboBox.getValue() == null) {
            errors.append("- Le sexe est obligatoire\n");
        }
        
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    /**
     * Navigate back to the owner details
     */
    @FXML
    private void retourDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/details_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire
            DetailsProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Détails du Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to details view");
            e.printStackTrace();
        }
    }
}
