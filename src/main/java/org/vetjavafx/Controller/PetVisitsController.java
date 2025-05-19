/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Liste visites
 */
package org.vetjavafx.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.Pet;
import org.vetjavafx.model.Visite;
import org.vetjavafx.model.Veterinarian;

import java.io.IOException;

public class PetVisitsController {

    @FXML
    private Label petInfoLabel;
    @FXML
    private TableView<Visite> visitsTable;
    @FXML
    private TableColumn<Visite, String> dateColumn;
    @FXML
    private TableColumn<Visite, String> descriptionColumn;
    @FXML
    private TableColumn<Visite, String> veterinarianColumn;
    @FXML
    private Button backButton;

    private Owner owner;
    private Pet pet;
    private ObservableList<Visite> visitsData = FXCollections.observableArrayList();

    public void setOwnerAndPet(Owner owner, Pet pet) {
        this.owner = owner;
        this.pet = pet;
        updateUI();
    }

    private void updateUI() {
        // Update pet info label
        petInfoLabel.setText("Visites de " + pet.getName() + " (" + pet.getType() + ")");

        // Set up table columns
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        veterinarianColumn.setCellValueFactory(cellData -> {
            Veterinarian vet = cellData.getValue().getVeterinarian();
            return new javafx.beans.property.SimpleStringProperty(
                vet != null ? vet.getName() + " (" + vet.getSpecialization() + ")" : ""
            );
        });

        // Load visits data
        visitsData.clear();
        visitsData.addAll(pet.getVisites());
        visitsTable.setItems(visitsData);
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/ownerDetails.fxml"));
        AnchorPane root = loader.load();
        OwnerDetailsController controller = loader.getController();
        controller.setOwner(owner);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
} 