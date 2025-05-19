package org.vetjavafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.DataManager;

import java.io.IOException;
import java.util.List;

/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Ajout propriétaire
 */
public class AddOwnerController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button saveOwnerButton;
    @FXML
    private Button backButton;

    @FXML
    private void handleSaveOwnerButtonClick() {
        try {
            // Validate fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                showError("Erreur", "Le nom et le prénom sont obligatoires");
                return;
            }

            // Get the owner details from the fields
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String phone = phoneField.getText();

            // Create a new Owner object
            Owner newOwner = new Owner(firstName, lastName, address, city, phone);

            // Get current owners list and add the new owner
            List<Owner> owners = DataManager.loadOwners();
            owners.add(newOwner);
            DataManager.saveOwners(owners);

            // Show success message
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Le propriétaire a été ajouté avec succès.");
            alert.showAndWait();

            // Navigate back to the owners list
            navigateToOwnersList();
        } catch (Exception e) {
            showError("Erreur", "Une erreur s'est produite lors de l'ajout du propriétaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        navigateToOwnersList();
    }

    private void navigateToOwnersList() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listerOwners.fxml"));
        Scene newScene = new Scene(loader.load());
        Stage currentStage = (Stage) saveOwnerButton.getScene().getWindow();
        currentStage.setScene(newScene);
        currentStage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
