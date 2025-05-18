package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Proprietaire;

import java.io.IOException;

/**
 * Controller for the add owner form
 */
public class AjouterProprietaireController {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextArea adresseField;
    
    @FXML
    private TextField telephoneField;
    
    /**
     * Handle the save button action
     */
    @FXML
    private void enregistrerProprietaire(ActionEvent event) {
        // Validate form fields
        if (validateForm()) {
            // Create new owner
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String adresse = adresseField.getText().trim();
            String telephone = telephoneField.getText().trim();
            
            // Generate a temporary ID (in a real app, this would be handled by the backend)
            int newId = (int) (Math.random() * 10000);
            
            Proprietaire nouveauProprietaire = new Proprietaire(newId, nom, prenom, adresse, telephone);
            
            // Navigate to the confirmation screen with the new owner
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/resultat_ajout.fxml"));
                Parent root = loader.load();
                
                // Get the controller and pass the new owner
                ResultatAjoutController controller = loader.getController();
                controller.setProprietaire(nouveauProprietaire);
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("VetCare 360 - Propriétaire Ajouté");
                Scene scene = new Scene(root, 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error navigating to result view");
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
        
        if (prenomField.getText().trim().isEmpty()) {
            errors.append("- Le prénom est obligatoire\n");
        }
        
        if (adresseField.getText().trim().isEmpty()) {
            errors.append("- L'adresse est obligatoire\n");
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            errors.append("- Le numéro de téléphone est obligatoire\n");
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
