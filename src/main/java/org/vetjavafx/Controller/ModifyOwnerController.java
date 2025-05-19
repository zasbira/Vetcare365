/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Modification propriétaire
 */
package org.vetjavafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.DataManager;

import java.io.IOException;

public class ModifyOwnerController {

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
    private Button saveButton;
    @FXML
    private Button backButton;

    private Owner owner;

    public void setOwner(Owner owner) {
        this.owner = owner;
        updateFields();
    }

    private void updateFields() {
        firstNameField.setText(owner.getFirstName());
        lastNameField.setText(owner.getLastName());
        addressField.setText(owner.getAddress());
        cityField.setText(owner.getCity());
        phoneField.setText(owner.getPhone());
    }

    @FXML
    private void handleSaveButtonClick() {
        try {
            // Validate fields
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                showError("Erreur", "Le nom et le prénom sont obligatoires");
                return;
            }

            // Update owner information
            owner.setFirstName(firstNameField.getText());
            owner.setLastName(lastNameField.getText());
            owner.setAddress(addressField.getText());
            owner.setCity(cityField.getText());
            owner.setPhone(phoneField.getText());

            // Save changes using the new updateOwner method
            DataManager.updateOwner(owner);

            // Show success message
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Les informations du propriétaire ont été mises à jour avec succès.");
            alert.showAndWait();

            // Navigate back to owner details
            navigateToOwnerDetails();
        } catch (Exception e) {
            showError("Erreur", "Une erreur s'est produite lors de la mise à jour du propriétaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        navigateToOwnerDetails();
    }

    private void navigateToOwnerDetails() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/ownerDetails.fxml"));
        AnchorPane root = loader.load();
        OwnerDetailsController controller = loader.getController();
        controller.setOwner(owner);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 