package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the home page
 */
public class HomeController {

    /**
     * Navigate to the Veterinaires list view
     */
    @FXML
    private void showVeterinaires(ActionEvent event) {
        navigateTo(event, "/views/veterinaires.fxml", "VetCare 360 - Liste des Vétérinaires");
    }

    /**
     * Navigate to the Owner search view
     */
    @FXML
    private void showRechercheProprietaire(ActionEvent event) {
        navigateTo(event, "/views/recherche_proprietaire.fxml", "VetCare 360 - Recherche Propriétaire");
    }

    /**
     * Navigate to the Add owner view
     */
    @FXML
    private void showAjouterProprietaire(ActionEvent event) {
        navigateTo(event, "/views/ajouter_proprietaire.fxml", "VetCare 360 - Ajouter Propriétaire");
    }

    /**
     * Helper method to handle navigation to different views
     */
    private void navigateTo(ActionEvent event, String viewPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(viewPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to " + viewPath);
            e.printStackTrace();
        }
    }
}
