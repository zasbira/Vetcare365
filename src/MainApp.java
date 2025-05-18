package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

/**
 * Main application class for VetCare 360 - Veterinary Clinic Management System
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main home view with explicit file path
        URL homeUrl = new File("out/views/home.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(homeUrl);
        
        // Set up the primary stage
        primaryStage.setTitle("VetCare 360 - Gestion Clinique Vétérinaire");
        Scene scene = new Scene(root, 1000, 700);
        
        // Add stylesheet with explicit file path
        URL cssUrl = new File("out/resources/css/style.css").toURI().toURL();
        scene.getStylesheets().add(cssUrl.toExternalForm());
        
        System.out.println("FXML loaded from: " + homeUrl);
        System.out.println("CSS loaded from: " + cssUrl);
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
