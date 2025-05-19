/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Liste propriétaires
 */
package org.vetjavafx.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.DataManager;

import java.io.IOException;
import java.util.List;

public class OwnerController {

    @FXML
    private TableView<Owner> ownerTable;
    @FXML
    private TableColumn<Owner, String> nameColumn;
    @FXML
    private TableColumn<Owner, String> addressColumn;
    @FXML
    private TableColumn<Owner, String> cityColumn;
    @FXML
    private TableColumn<Owner, String> phoneColumn;
    @FXML
    private TableColumn<Owner, Void> detailsColumn;
    @FXML
    private TableColumn<Owner, Void> deleteColumn;
    @FXML
    private TextField searchField;  // Search bar

    public Button addOwnerButton;

    private ObservableList<Owner> ownerData = FXCollections.observableArrayList();

    public void initialize() {
        // Load owners from file
        List<Owner> savedOwners = DataManager.loadOwners();
        ownerData.addAll(savedOwners);

        // Set up TableColumns
        nameColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName());
        });
        addressColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress());
        });
        cityColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCity());
        });
        phoneColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone());
        });

        // Button for details in each row
        detailsColumn.setCellFactory(column -> {
            return new TableCell<Owner, Void>() {
                private final Button detailsButton = new Button("Détails");

                {
                    detailsButton.setOnAction(event -> {
                        Owner owner = getTableView().getItems().get(getIndex());
                        showOwnerDetails(owner);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailsButton);
                    }
                }
            };
        });

        // Add delete button column
        deleteColumn.setCellFactory(column -> {
            return new TableCell<Owner, Void>() {
                private final Button deleteButton = new Button("Supprimer");
                {
                    deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> {
                        Owner owner = getTableView().getItems().get(getIndex());
                        deleteOwner(owner);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
        });

        // Set data to table
        ownerTable.setItems(ownerData);

        // Set up search filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });
    }

    // Method to filter data based on search input
    private void filterData(String query) {
        ObservableList<Owner> filteredData = FXCollections.observableArrayList();
        for (Owner owner : ownerData) {
            if (owner.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredData.add(owner);
            }
        }
        ownerTable.setItems(filteredData);
    }

    // Method to show owner details
    private void showOwnerDetails(Owner owner) {
        try {
            // Load the owner details scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/ownerDetails.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            // Get the controller and set the owner
            OwnerDetailsController controller = loader.getController();
            controller.setOwner(owner);

            // Get the current stage and set the new scene
            Stage stage = (Stage) ownerTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading owner details: " + e.getMessage());
        }
    }

    // Event handler for the Veterinaires button
    @FXML
    private void handleAddOwnerButtonClick() throws IOException {
        // Load the veterinarian scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/addOwner.fxml"));
        AnchorPane root = loader.load();
        javafx.scene.Scene scene = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) addOwnerButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public Button accButton;
    public Button vetbutton;

    @FXML
    private void handleBackButtonClick() throws IOException {
        try {
            // Load the accueil.fxml scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/acceuil.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);

            // Get the current stage (window) and set the new scene
            Stage stage = (Stage) ownerTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error navigating to home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVeterinairesButtonClick() throws IOException {
        // Load the veterinarian scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listervet.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) vetbutton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void addOwnerToList(Owner newOwner) {
        // Add the new owner to the list
        ownerData.add(newOwner);

        // Save all owners to file
        DataManager.saveOwners(ownerData);

        // Update the table
        ownerTable.setItems(ownerData);
        ownerTable.refresh();
    }

    @FXML
    private void handleRefreshButtonClick() {
        // Refresh the TableView
        ownerTable.refresh();
    }

    private void deleteOwner(Owner owner) {
        // Show confirmation dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le propriétaire");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer " + owner.getFullName() + " et tous ses animaux et visites ?");

        if (alert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            try {
                // Remove from the table
                ownerData.remove(owner);
                
                // Remove from storage
                DataManager.deleteOwner(owner);
                
                // Refresh the table
                ownerTable.refresh();
                
                // Show success message
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("Propriétaire supprimé");
                successAlert.setContentText("Le propriétaire et toutes les données associées ont été supprimés avec succès.");
                successAlert.showAndWait();
            } catch (Exception e) {
                // Show error message
                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Échec de la suppression");
                errorAlert.setContentText("Impossible de supprimer le propriétaire : " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
}


