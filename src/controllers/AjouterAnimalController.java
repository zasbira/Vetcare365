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
 * Controller for the add animal form
 */
public class AjouterAnimalController implements Initializable {

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
     * Set the proprietaire for this animal
     */
    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        proprietaireLabel.setText(proprietaire.getPrenom() + " " + proprietaire.getNom());
    }
    
    /**
     * Handle the save button action
     */
    @FXML
    private void enregistrerAnimal(ActionEvent event) {
        // Validate form fields
        if (validateForm()) {
            // Create new animal
            String nom = nomField.getText().trim();
            String espece = especeComboBox.getValue();
            String sexe = sexeComboBox.getValue();
            int age = ageSpinner.getValue();
            
            // Generate a temporary ID (in a real app, this would be handled by the backend)
            int newId = (int) (Math.random() * 10000);
            
            Animal nouvelAnimal = new Animal(newId, nom, espece, sexe, age, proprietaire.getId());
            
            // Add the animal to the proprietaire
            proprietaire.addAnimal(nouvelAnimal);
            
            // In a real app, we would save to the backend here
            
            // Navigate back to the owner details or to add visit
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
                
                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Animal ajouté");
                alert.setHeaderText("Animal ajouté avec succès");
                alert.setContentText("L'animal " + nom + " a été ajouté à " + proprietaire.getPrenom() + " " + proprietaire.getNom());
                alert.showAndWait();
                
            } catch (IOException e) {
                System.err.println("Error navigating to details view");
                e.printStackTrace();
            }
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
    private void retourProprietaire(ActionEvent event) {
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
