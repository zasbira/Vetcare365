/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Liste vétérinaires
 */
package org.vetjavafx.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.vetjavafx.model.Veterinarian;
import javafx.stage.Stage;


import java.io.IOException;

public class VeterinarianController {

    public Button accButton;
    public Button Ownerbutton ;

    @FXML
    private TableView<Veterinarian> veterinarianTable;
    @FXML
    private TableColumn<Veterinarian, String> nameColumn;
    @FXML
    private TableColumn<Veterinarian, String> specializationColumn;
    @FXML
    private Button veterinariansButton;
    @FXML
    private Button ownersButton;



    private ObservableList<Veterinarian> veterinarianData = FXCollections.observableArrayList();

    @FXML
    private void handleownerButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listerOwners.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        Stage stage = (Stage) Ownerbutton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

    }
    @FXML

    private void handleBackButtonClick() throws IOException {
        // Load the accueil.fxml scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/acceuil.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) accButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        // Optionally, you can print for debugging purposes
        System.out.println("Back button clicked");
    }


    public void initialize() {



      // Table column binding
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        specializationColumn.setCellValueFactory(cellData -> cellData.getValue().specializationProperty());

        // Adding data to the ObservableList
        veterinarianData.add(new Veterinarian("Dr. Emma Lefevre", "Médecine interne"));
        veterinarianData.add(new Veterinarian("Dr. Martin Dubois", "Chirurgie"));
        veterinarianData.add(new Veterinarian("Dr. Sophie Bernard", "Dermatologie"));
        veterinarianData.add(new Veterinarian("Dr. Julien Lambert", "Dentisterie"));

        // Set data in TableView
        veterinarianTable.setItems(veterinarianData);
    }


}
