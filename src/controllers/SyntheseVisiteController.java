package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Animal;
import models.Proprietaire;
import models.Visite;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the visit summary view
 */
public class SyntheseVisiteController {

    @FXML
    private Label proprietaireLabel;
    
    @FXML
    private Label animalLabel;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label veterinaireLabel;
    
    @FXML
    private Label descriptionLabel;
    
    private Proprietaire proprietaire;
    private Animal animal;
    private Visite visite;
    
    /**
     * Set the data for the summary
     */
    public void setData(Proprietaire proprietaire, Animal animal, Visite visite) {
        this.proprietaire = proprietaire;
        this.animal = animal;
        this.visite = visite;
        
        // Update the UI with the data
        proprietaireLabel.setText(proprietaire.getPrenom() + " " + proprietaire.getNom());
        animalLabel.setText(animal.getNom() + " (" + animal.getEspece() + ", " + animal.getAge() + " ans)");
        
        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateLabel.setText(visite.getDate().format(formatter));
        
        veterinaireLabel.setText(visite.getVeterinaireNom());
        descriptionLabel.setText(visite.getDescription());
    }
    
    /**
     * Handle the print button action
     */
    @FXML
    private void imprimerSynthese() {
        // In a real app, this would connect to the printer service
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impression");
        alert.setHeaderText("Envoi vers l'imprimante");
        alert.setContentText("La synthèse est en cours d'impression...");
        alert.showAndWait();
    }
    
    /**
     * Navigate to add another visit view
     */
    @FXML
    private void ajouterAutreVisite(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouter_visite.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the animal
            AjouterVisiteController controller = loader.getController();
            controller.setAnimal(animal, proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Ajouter Visite");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to add visit view");
            e.printStackTrace();
        }
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
