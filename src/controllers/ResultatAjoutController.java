package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Proprietaire;

import java.io.IOException;

/**
 * Controller for the add owner result view
 */
public class ResultatAjoutController {

    @FXML
    private Label nomLabel;
    
    @FXML
    private Label prenomLabel;
    
    @FXML
    private Label adresseLabel;
    
    @FXML
    private Label telephoneLabel;
    
    private Proprietaire proprietaire;
    
    /**
     * Set the proprietaire and update the UI
     */
    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        
        // Update the UI with proprietaire details
        nomLabel.setText(proprietaire.getNom());
        prenomLabel.setText(proprietaire.getPrenom());
        adresseLabel.setText(proprietaire.getAdresse());
        telephoneLabel.setText(proprietaire.getTelephone());
    }
    
    /**
     * Navigate to modify owner view
     */
    @FXML
    private void modifierProprietaire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modifier_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire to modify
            ModifierProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Modifier Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to modify owner view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to add animal view
     */
    @FXML
    private void ajouterAnimal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouter_animal.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the owner
            AjouterAnimalController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Ajouter Animal");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to add animal view");
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
