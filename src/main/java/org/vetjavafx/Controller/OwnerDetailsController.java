/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Détails propriétaire
 */
package org.vetjavafx.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.Pet;

import java.io.IOException;

public class OwnerDetailsController {

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button addPetButton;
    @FXML
    private Button modifyOwnerButton;
    @FXML
    private TableView<Pet> petsTable;
    @FXML
    private TableColumn<Pet, String> petNameColumn;
    @FXML
    private TableColumn<Pet, String> petTypeColumn;
    @FXML
    private TableColumn<Pet, String> petBirthDateColumn;
    @FXML
    private TableColumn<Pet, Integer> petVisitsColumn;
    @FXML
    private TableColumn<Pet, Void> actionsColumn;

    private Owner owner;
    private ObservableList<Pet> petsData = FXCollections.observableArrayList();

    public void setOwner(Owner owner) {
        this.owner = owner;
        updateFields();
        updatePetsTable();
    }

    private void updateFields() {
        firstNameLabel.setText(owner.getFirstName());
        lastNameLabel.setText(owner.getLastName());
        addressLabel.setText(owner.getAddress());
        cityLabel.setText(owner.getCity());
        phoneLabel.setText(String.valueOf(owner.getPhone()));
    }

    private void updatePetsTable() {
        petsData.clear();
        petsData.addAll(owner.getPets());

        petNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        petTypeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        petBirthDateColumn.setCellValueFactory(cellData -> cellData.getValue().birthDateProperty());
        petVisitsColumn.setCellValueFactory(cellData -> cellData.getValue().visitsCountProperty().asObject());

        // Add action buttons to each row
        actionsColumn.setCellFactory(new Callback<TableColumn<Pet, Void>, TableCell<Pet, Void>>() {
            @Override
            public TableCell<Pet, Void> call(final TableColumn<Pet, Void> param) {
                return new TableCell<Pet, Void>() {
                    private final Button modifyButton = new Button("Modifier");
                    private final Button addVisitButton = new Button("Ajouter Visite");
                    private final Button viewVisitsButton = new Button("Voir Visites");

                    {
                        modifyButton.setOnAction(event -> {
                            Pet pet = getTableView().getItems().get(getIndex());
                            navigateToModifyPet(pet);
                        });

                        addVisitButton.setOnAction(event -> {
                            Pet pet = getTableView().getItems().get(getIndex());
                            navigateToAddVisit(pet);
                        });

                        viewVisitsButton.setOnAction(event -> {
                            Pet pet = getTableView().getItems().get(getIndex());
                            navigateToPetVisits(pet);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5, modifyButton, addVisitButton, viewVisitsButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });

        petsTable.setItems(petsData);
    }

    private void navigateToPetVisits(Pet pet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/petVisits.fxml"));
            AnchorPane root = loader.load();
            PetVisitsController controller = loader.getController();
            controller.setOwnerAndPet(owner, pet);

            Stage stage = (Stage) petsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listerOwners.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAddPetButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/addPet.fxml"));
        AnchorPane root = loader.load();
        AddPetController controller = loader.getController();
        controller.setOwner(owner);

        Scene scene = new Scene(root);
        Stage stage = (Stage) addPetButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleModifyOwnerButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/modifyOwner.fxml"));
        AnchorPane root = loader.load();
        ModifyOwnerController controller = loader.getController();
        controller.setOwner(owner);

        Scene scene = new Scene(root);
        Stage stage = (Stage) modifyOwnerButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void navigateToModifyPet(Pet pet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/modifyPet.fxml"));
            AnchorPane root = loader.load();
            ModifyPetController controller = loader.getController();
            controller.setOwnerAndPet(owner, pet);

            Stage stage = (Stage) petsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToAddVisit(Pet pet) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/addVisit.fxml"));
            AnchorPane root = loader.load();
            AddVisitController controller = loader.getController();
            controller.setOwnerAndPet(owner, pet);

            Stage stage = (Stage) petsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 