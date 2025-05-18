import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Standalone launcher for VetCare360 application
 * This class can run independently without relying on other classes
 */
public class DirectLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create a simple UI with diagnostic information
            Label titleLabel = new Label("VetCare 360 Diagnostic Launcher");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            Label statusLabel = new Label("JavaFX runtime is working correctly");
            statusLabel.setStyle("-fx-font-size: 14px;");
            
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> primaryStage.close());
            
            // Create layout
            VBox root = new VBox(20);
            root.setStyle("-fx-padding: 20px; -fx-alignment: center;");
            root.getChildren().addAll(titleLabel, statusLabel, closeButton);
            
            // Set up the scene
            Scene scene = new Scene(root, 400, 300);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360 Launcher");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("JavaFX window displayed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Starting VetCare360 diagnostic launcher...");
        launch(args);
    }
}
