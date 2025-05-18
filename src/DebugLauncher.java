/**
 * Simple debug launcher to help diagnose application startup issues
 */
public class DebugLauncher {
    public static void main(String[] args) {
        System.out.println("=== VetCare360 Debug Launcher ===");
        
        try {
            // Check if JavaFX is available
            System.out.println("Checking for JavaFX...");
            Class.forName("javafx.application.Application");
            System.out.println("✓ JavaFX found!");
            
            // Check if FXML file exists
            System.out.println("\nChecking for FXML file...");
            java.io.File fxmlFile = new java.io.File("out/views/home.fxml");
            if (fxmlFile.exists()) {
                System.out.println("✓ FXML file found at: " + fxmlFile.getAbsolutePath());
            } else {
                System.out.println("✗ FXML file not found at: " + fxmlFile.getAbsolutePath());
                // Try another location
                fxmlFile = new java.io.File("src/views/home.fxml");
                if (fxmlFile.exists()) {
                    System.out.println("✓ FXML file found at alternate location: " + fxmlFile.getAbsolutePath());
                }
            }
            
            // Check if CSS file exists
            System.out.println("\nChecking for CSS file...");
            java.io.File cssFile = new java.io.File("out/resources/css/style.css");
            if (cssFile.exists()) {
                System.out.println("✓ CSS file found at: " + cssFile.getAbsolutePath());
            } else {
                System.out.println("✗ CSS file not found at: " + cssFile.getAbsolutePath());
                // Try another location
                cssFile = new java.io.File("resources/css/style.css");
                if (cssFile.exists()) {
                    System.out.println("✓ CSS file found at alternate location: " + cssFile.getAbsolutePath());
                }
            }
            
            // Check for MySQL connector
            System.out.println("\nChecking for MySQL connector...");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("✓ MySQL connector found!");
            } catch (ClassNotFoundException e) {
                System.out.println("✗ MySQL connector not found!");
            }
            
            // Try starting the actual application
            System.out.println("\nAttempting to launch main application...");
            System.out.println("Launch command: src.MainApp.main(args)");
            try {
                // Use reflection to avoid direct dependency
                Class<?> mainAppClass = Class.forName("src.MainApp");
                java.lang.reflect.Method mainMethod = mainAppClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) args);
                System.out.println("✓ Application launched successfully!");
            } catch (Exception e) {
                System.out.println("✗ Failed to launch application!");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ JavaFX not found! Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
