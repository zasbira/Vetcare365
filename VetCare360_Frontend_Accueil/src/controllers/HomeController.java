package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class HomeController {

    @FXML
    public void goToVeterinaires(ActionEvent event) {
        loadScene("veterinaires.fxml");
    }

    @FXML
    public void goToRechercheProprietaire(ActionEvent event) {
        loadScene("recherche_proprietaire.fxml");
    }

    @FXML
    public void goToAjoutProprietaire(ActionEvent event) {
        loadScene("ajout_proprietaire.fxml");
    }

    private void loadScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxml));
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
