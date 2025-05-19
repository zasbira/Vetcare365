package org.vetjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the initial scene (Accueil)
        FXMLLoader fxmlLoaderAccueil = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/acceuil.fxml"));
        AnchorPane rootAccueil = fxmlLoaderAccueil.load();

        Scene accueilScene = new Scene(rootAccueil);

        // Set the title and the initial scene
        stage.setTitle("Gestion de la clinique vétérinaire");
        stage.setScene(accueilScene);

        // Get buttons from the FXML for the scene transitions


        // Show the initial scene
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
