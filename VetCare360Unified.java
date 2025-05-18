import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Unified launcher for VetCare360 - combines all modules in one application
 */
public class VetCare360Unified extends Application {

    private BorderPane mainLayout;
    private VBox homeView;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create main layout
            mainLayout = new BorderPane();
            
            // Create home view
            createHomeView();
            
            // Set home view as the initial center content
            mainLayout.setCenter(homeView);
            
            // Create header
            HBox header = createHeader();
            mainLayout.setTop(header);
            
            // Create sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);
            
            // Create status bar
            HBox statusBar = createStatusBar();
            mainLayout.setBottom(statusBar);
            
            // Set up the scene
            Scene scene = new Scene(mainLayout, 1200, 700);
            
            // Configure and show the stage
            primaryStage.setTitle("VetCare 360");
            try {
                // Try to load logo if exists
                InputStream iconStream = new FileInputStream("resources/images/logo.png");
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("Logo not found, using default icon.");
            }
            
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Create the header with logo and title
     */
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #167e74;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Logo placeholder
        ImageView logoView = null;
        try {
            // Try to load logo if exists
            InputStream iconStream = new FileInputStream("resources/images/logo.png");
            Image logo = new Image(iconStream, 40, 40, true, true);
            logoView = new ImageView(logo);
        } catch (Exception e) {
            // Create a placeholder if logo not found
            StackPane logoPlaceholder = new StackPane();
            logoPlaceholder.setPrefSize(40, 40);
            logoPlaceholder.setStyle("-fx-background-color: white; -fx-background-radius: 20;");
            Label logoText = new Label("V360");
            logoText.setTextFill(Color.valueOf("#167e74"));
            logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            logoPlaceholder.getChildren().add(logoText);
            logoView = new ImageView();
        }
        
        // Title
        Label titleLabel = new Label("VetCare 360");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        
        // Subtitle
        Label subtitleLabel = new Label("Gestion de Clinique Vétérinaire");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setTextFill(Color.WHITE);
        
        // Put title and subtitle in a VBox
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Add all to header
        if (logoView != null) {
            header.getChildren().addAll(logoView, titleBox);
        } else {
            header.getChildren().add(titleBox);
        }
        
        return header;
    }
    
    /**
     * Create the sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f5f5f5;");
        sidebar.setPrefWidth(200);
        
        Label navTitle = new Label("Navigation");
        navTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        navTitle.setPadding(new Insets(0, 0, 10, 0));
        
        // Home button
        Button homeBtn = createNavButton("🏠 Accueil", e -> showHomeView());
        
        // Veterinarians button
        Button vetsBtn = createNavButton("🩺 Vétérinaires", e -> launchModule("VetCRUD"));
        
        // Owners button
        Button ownersBtn = createNavButton("👤 Propriétaires", e -> launchModule("PropCRUD"));
        
        // Animals button
        Button animalsBtn = createNavButton("🐾 Animaux", e -> launchModule("AnimalCRUD"));
        
        // Visits button
        Button visitsBtn = createNavButton("📅 Visites", e -> launchModule("VisiteCRUD"));
        
        // About button
        Button aboutBtn = createNavButton("ℹ️ À propos", e -> showAbout());
        
        // Add separator before about
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));
        
        sidebar.getChildren().addAll(
            navTitle, 
            homeBtn, 
            vetsBtn, 
            ownersBtn, 
            animalsBtn, 
            visitsBtn, 
            separator, 
            aboutBtn
        );
        
        return sidebar;
    }
    
    /**
     * Create a styled navigation button
     */
    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333;");
        button.setPadding(new Insets(10, 15, 10, 15));
        
        // Hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #167e74;")
        );
        button.setOnMouseExited(e -> 
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #333333;")
        );
        
        button.setOnAction(handler);
        
        return button;
    }
    
    /**
     * Create status bar for the bottom of the layout
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #f0f0f0;");
        
        Label statusLabel = new Label("Prêt");
        statusLabel.setTextFill(Color.valueOf("#555555"));
        
        Label versionLabel = new Label("VetCare 360 v1.0");
        versionLabel.setTextFill(Color.valueOf("#555555"));
        HBox.setHgrow(versionLabel, Priority.ALWAYS);
        versionLabel.setAlignment(Pos.CENTER_RIGHT);
        
        statusBar.getChildren().addAll(statusLabel, versionLabel);
        
        return statusBar;
    }
    
    /**
     * Create the home view
     */
    private void createHomeView() {
        homeView = new VBox(20);
        homeView.setAlignment(Pos.CENTER);
        homeView.setPadding(new Insets(50));
        
        // Welcome title
        Label welcomeLabel = new Label("Bienvenue dans VetCare 360");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        welcomeLabel.setTextFill(Color.valueOf("#167e74"));
        
        // Description
        Label descLabel = new Label("Système de gestion complet pour cliniques vétérinaires");
        descLabel.setFont(Font.font("Segoe UI", 16));
        
        // Module cards in a grid
        GridPane modulesGrid = new GridPane();
        modulesGrid.setHgap(20);
        modulesGrid.setVgap(20);
        modulesGrid.setAlignment(Pos.CENTER);
        
        // Add module cards
        modulesGrid.add(createModuleCard(
            "🩺 Vétérinaires", 
            "Gérer les vétérinaires et leurs spécialités",
            e -> launchModule("VetCRUD")), 0, 0);
            
        modulesGrid.add(createModuleCard(
            "👤 Propriétaires", 
            "Gérer les propriétaires d'animaux",
            e -> launchModule("PropCRUD")), 1, 0);
            
        modulesGrid.add(createModuleCard(
            "🐾 Animaux", 
            "Gérer les animaux et leurs informations",
            e -> launchModule("AnimalCRUD")), 0, 1);
            
        modulesGrid.add(createModuleCard(
            "📅 Visites", 
            "Planifier et suivre les visites médicales",
            e -> launchModule("VisiteCRUD")), 1, 1);
        
        homeView.getChildren().addAll(welcomeLabel, descLabel, new Separator(), modulesGrid);
    }
    
    /**
     * Create a module card for the home view
     */
    private VBox createModuleCard(String title, String description, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.valueOf("#167e74"));
        
        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", 14));
        descLabel.setWrapText(true);
        
        // Button
        Button launchBtn = new Button("Ouvrir");
        launchBtn.setStyle("-fx-background-color: #167e74; -fx-text-fill: white; -fx-background-radius: 20;");
        launchBtn.setPadding(new Insets(8, 15, 8, 15));
        
        // Hover effect
        launchBtn.setOnMouseEntered(e -> 
            launchBtn.setStyle("-fx-background-color: #0d5951; -fx-text-fill: white; -fx-background-radius: 20;")
        );
        launchBtn.setOnMouseExited(e -> 
            launchBtn.setStyle("-fx-background-color: #167e74; -fx-text-fill: white; -fx-background-radius: 20;")
        );
        
        launchBtn.setOnAction(handler);
        
        // Add space between description and button
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        card.getChildren().addAll(titleLabel, descLabel, spacer, launchBtn);
        
        return card;
    }
    
    /**
     * Show the home view
     */
    private void showHomeView() {
        mainLayout.setCenter(homeView);
    }
    
    /**
     * Launch a specific module
     */
    private void launchModule(String moduleName) {
        try {
            // Create a new process for the module
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "run-" + moduleName.toLowerCase() + ".bat");
            pb.directory(new java.io.File(System.getProperty("user.dir")));
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error launching module: " + e.getMessage());
        }
    }
    
    /**
     * Show the about dialog
     */
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos de VetCare 360");
        alert.setHeaderText("VetCare 360 - Système de Gestion Vétérinaire");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label versionLabel = new Label("Version: 1.0");
        Label descLabel = new Label("VetCare 360 est une application JavaFX moderne destinée aux cliniques vétérinaires pour gérer facilement les propriétaires d'animaux, les animaux, les vétérinaires et les visites médicales.");
        descLabel.setWrapText(true);
        
        Label techLabel = new Label("Technologies utilisées:");
        Label techDetailsLabel = new Label("• JavaFX (UI)\n• MySQL (Base de données)\n• CSS (Thème personnalisé)");
        
        Label copyrightLabel = new Label("© 2025 VetCare 360. Tous droits réservés.");
        copyrightLabel.setStyle("-fx-font-style: italic;");
        
        content.getChildren().addAll(versionLabel, descLabel, new Separator(), techLabel, techDetailsLabel, new Separator(), copyrightLabel);
        
        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }
    
    /**
     * Display an error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
