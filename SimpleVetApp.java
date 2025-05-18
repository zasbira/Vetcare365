import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Extremely simple JavaFX application to verify JavaFX is working properly.
 * No database connection or complex UI.
 */
public class SimpleVetApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Label label = new Label("VetCare360 - Simple Test Application");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        root.getChildren().add(label);
        
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("VetCare360 Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
