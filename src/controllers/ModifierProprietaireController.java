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
 * Controller for the edit owner form
 */
public class ModifierProprietaireController {

    @FXML
    private TextField nomField;
    
    @FXML
    private TextField prenomField;
    
    @FXML
    private TextArea adresseField;
    
    @FXML
    private TextField telephoneField;
    
    private Proprietaire proprietaire;
    
    /**
     * Set the proprietaire to edit and initialize form fields
     */
    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        
        // Populate form fields with proprietaire data
        nomField.setText(proprietaire.getNom());
        prenomField.setText(proprietaire.getPrenom());
        adresseField.setText(proprietaire.getAdresse());
        telephoneField.setText(proprietaire.getTelephone());
    }
    
    /**
     * Handle the update button action
     */
    @FXML
    private void mettreAJourProprietaire(ActionEvent event) {
        // Validate form fields
        if (validateForm()) {
            // Update proprietaire with new values
            proprietaire.setNom(nomField.getText().trim());
            proprietaire.setPrenom(prenomField.getText().trim());
            proprietaire.setAdresse(adresseField.getText().trim());
            proprietaire.setTelephone(telephoneField.getText().trim());
            
            // In a real app, we would save to the backend here
            
            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Mise à jour réussie");
            alert.setHeaderText("Propriétaire mis à jour");
            alert.setContentText("Les informations du propriétaire ont été mises à jour avec succès.");
            alert.showAndWait();
            
            // Navigate to details view
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
     * Navigate back to the previous page
     */
    @FXML
    private void retourArriere(ActionEvent event) {
        try {
            // We need to handle different back navigation scenarios
            // For simplicity, we'll go back to search
            Parent root = FXMLLoader.load(getClass().getResource("/views/recherche_proprietaire.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Recherche Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating back");
            e.printStackTrace();
        }
    }
}
