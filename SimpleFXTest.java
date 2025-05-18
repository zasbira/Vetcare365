import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Simple JavaFX test application
 */
public class SimpleFXTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a simple UI
        Label label = new Label("JavaFX is working!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        StackPane root = new StackPane();
        root.getChildren().add(label);
        
        // Set up the scene
        Scene scene = new Scene(root, 400, 300);
        
        // Configure and show the stage
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("JavaFX window displayed successfully!");
    }
    
    public static void main(String[] args) {
        System.out.println("Starting simple JavaFX test...");
        launch(args);
    }
}
