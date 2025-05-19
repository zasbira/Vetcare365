/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Menu principal
 */
package org.vetjavafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AcceuilController {

    public Button vetbutton ;
    public Button vetsecondbutton ;
    public Button Ownerbutton ;
    public Button Ownersecondbutton ;



    // Event handler for the Veterinaires button
    @FXML
    private void handleVeterinairesButtonClick() throws IOException {
        // Load the veterinarian scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listervet.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) vetbutton.getScene().getWindow();
        // accButton can be any button in the acceuil scene
        stage.setScene(scene);
        stage.show();

        System.out.println("Veterinaires Button clicked");
    }
    @FXML

    private void handlesecondVeterinairesButtonClick() throws IOException {
        // Load the veterinarian scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listervet.fxml"));
        AnchorPane root = loader.load();
        Scene scene1 = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) vetsecondbutton.getScene().getWindow();
        // accButton can be any button in the acceuil scene
        stage.setScene(scene1);
        stage.show();

        System.out.println("Veterinaires Button clicked");
    }


    @FXML
    private void handleownerButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listerOwners.fxml"));
        AnchorPane root = loader.load();
        Scene scene1 = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) Ownerbutton.getScene().getWindow();
        // accButton can be any button in the acceuil scene
        stage.setScene(scene1);
        stage.show();

    }
    @FXML
    private void handleOwnersecondbuttonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/listerOwners.fxml"));
        AnchorPane root = loader.load();
        Scene scene1 = new Scene(root);

        // Get the current stage (window) and set the new scene
        Stage stage = (Stage) Ownersecondbutton.getScene().getWindow();
        // accButton can be any button in the acceuil scene
        stage.setScene(scene1);
        stage.show();

    }
}
