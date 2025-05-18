import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Simple VetCare360 Dashboard with dark sidebar
 * No database dependencies to ensure it runs properly
 */
public class SimpleVetDashboard extends Application {
    
    // UI components
    private BorderPane mainLayout;
    private StackPane contentArea;
    private Button homeBtn, ownersBtn, vetsBtn, animalsBtn, visitsBtn;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create main layout
            mainLayout = new BorderPane();
            
            // Create sidebar
            VBox sidebar = createSidebar();
            mainLayout.setLeft(sidebar);
            
            // Create content area
            contentArea = new StackPane();
            contentArea.setStyle("-fx-background-color: #f0f5f5;");
            mainLayout.setCenter(contentArea);
            
            // Show dashboard by default
            showDashboard();
            
            // Set up the scene
            Scene scene = new Scene(mainLayout, 1200, 700);
            primaryStage.setTitle("VetCare 360 Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Create sidebar with navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #1a2328;"); // Dark color from image
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        
        // Logo area
        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        
        // Circle icon
        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(36, 36);
        iconCircle.setMaxSize(36, 36);
        iconCircle.setStyle("-fx-background-color: #20B2AA; -fx-background-radius: 18px;");
        
        Label iconLabel = new Label("V");
        iconLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        iconCircle.getChildren().add(iconLabel);
        
        // App title
        Label appTitle = new Label("VetCare 360");
        appTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        logoBox.getChildren().addAll(iconCircle, appTitle);
        
        // Create navigation buttons
        homeBtn = createNavButton("Accueil", true);
        homeBtn.setOnAction(e -> showDashboard());
        
        ownersBtn = createNavButton("Propriétaires", false);
        ownersBtn.setOnAction(e -> showMessage("Gestion des propriétaires"));
        
        vetsBtn = createNavButton("Vétérinaires", false);
        vetsBtn.setOnAction(e -> showMessage("Gestion des vétérinaires"));
        
        animalsBtn = createNavButton("Animaux", false);
        animalsBtn.setOnAction(e -> showMessage("Gestion des animaux"));
        
        visitsBtn = createNavButton("Visites", false);
        visitsBtn.setOnAction(e -> showMessage("Gestion des visites"));
        
        // Add navigation elements
        sidebar.getChildren().addAll(
            logoBox,
            new Separator(),
            homeBtn,
            ownersBtn,
            vetsBtn,
            animalsBtn,
            visitsBtn
        );
        
        return sidebar;
    }
    
    /**
     * Create a navigation button
     */
    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(10, 15, 10, 15));
        
        if (isActive) {
            button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
        }
        
        // Hover effects
        button.setOnMouseEntered(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 5px;");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!isActive) {
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
            }
        });
        
        return button;
    }
    
    /**
     * Set the active navigation button
     */
    private void setActiveButton(Button activeButton) {
        // Reset all buttons
        for (Button btn : new Button[]{homeBtn, ownersBtn, vetsBtn, animalsBtn, visitsBtn}) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
            
            // Set mouse event handlers again
            btn.setOnMouseEntered(e -> {
                btn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-background-radius: 5px;");
            });
            
            btn.setOnMouseExited(e -> {
                if (btn != activeButton) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-background-radius: 5px;");
                }
            });
        }
        
        // Set active button
        activeButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5px;");
    }
    
    /**
     * Show dashboard view
     */
    private void showDashboard() {
        setActiveButton(homeBtn);
        
        // Create scrollable dashboard content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.setStyle("-fx-background-color: #f0f5f5;");
        
        // Dashboard header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titleLabel = new Label("Tableau de bord");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitleLabel = new Label("Gestion de clinique vétérinaire");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        header.getChildren().add(titleBox);
        
        // Create stats cards
        HBox statsCards = new HBox(20);
        statsCards.setAlignment(Pos.CENTER);
        
        VBox ownersCard = createStatsCard("Propriétaires", "25", "#4e73df");
        VBox animalsCard = createStatsCard("Animaux", "42", "#1cc88a");
        VBox visitsCard = createStatsCard("Visites", "18", "#f6c23e");
        VBox vetsCard = createStatsCard("Vétérinaires", "8", "#36b9cc");
        
        statsCards.getChildren().addAll(ownersCard, animalsCard, visitsCard, vetsCard);
        
        // Create charts section
        HBox chartsSection = new HBox(20);
        chartsSection.setAlignment(Pos.CENTER);
        
        // Create visit statistics chart
        VBox visitStatsBox = new VBox(10);
        visitStatsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                             "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        visitStatsBox.setPadding(new Insets(15));
        visitStatsBox.setPrefWidth(500);
        
        Label visitChartTitle = new Label("Statistiques des visites");
        visitChartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label visitChartSubtitle = new Label("Visites mensuelles au cours des 12 derniers mois");
        visitChartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Create the visit chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> visitChart = new BarChart<>(xAxis, yAxis);
        visitChart.setTitle("");
        visitChart.setLegendVisible(false);
        visitChart.setAnimated(false);
        visitChart.setPrefHeight(300);
        visitChart.setStyle("-fx-background-color: transparent;");
        
        // Add sample data for visit chart
        XYChart.Series<String, Number> visitSeries = new XYChart.Series<>();
        visitSeries.setName("Nombre de visites");
        
        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jui", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        int[] values = {0, 4, 8, 2, 1, 0, 0, 3, 2, 1, 0, 0};
        
        for (int i = 0; i < months.length; i++) {
            visitSeries.getData().add(new XYChart.Data<>(months[i], values[i]));
        }
        
        visitChart.getData().add(visitSeries);
        
        // Style the bars to match purple color in image
        for (XYChart.Data<String, Number> item : visitSeries.getData()) {
            item.getNode().setStyle("-fx-bar-fill: #8884d8;");
        }
        
        visitStatsBox.getChildren().addAll(visitChartTitle, visitChartSubtitle, visitChart);
        
        // Create animal types chart
        VBox animalTypesBox = new VBox(10);
        animalTypesBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        animalTypesBox.setPadding(new Insets(15));
        animalTypesBox.setPrefWidth(400);
        
        Label animalChartTitle = new Label("Types d'animaux");
        animalChartTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label animalChartSubtitle = new Label("Répartition par espèce");
        animalChartSubtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Create the chart for animal types
        CategoryAxis animalXAxis = new CategoryAxis();
        NumberAxis animalYAxis = new NumberAxis();
        animalYAxis.setTickUnit(1);
        BarChart<String, Number> animalChart = new BarChart<>(animalXAxis, animalYAxis);
        animalChart.setTitle("");
        animalChart.setLegendVisible(false);
        animalChart.setAnimated(false);
        animalChart.setPrefHeight(300);
        animalChart.setStyle("-fx-background-color: transparent;");
        
        // Sample data for animal chart
        XYChart.Series<String, Number> animalSeries = new XYChart.Series<>();
        
        String[] species = {"Chien", "Oiseau", "Lapin", "Chat", "Rongeur"};
        int[] counts = {8, 3, 1, 2, 2};
        String[] colors = {"#4e73df", "#36b9cc", "#1cc88a", "#f6c23e", "#e74a3b"};
        
        for (int i = 0; i < species.length; i++) {
            animalSeries.getData().add(new XYChart.Data<>(species[i], counts[i]));
        }
        
        animalChart.getData().add(animalSeries);
        
        // Style each bar with different colors
        int colorIndex = 0;
        for (XYChart.Data<String, Number> item : animalSeries.getData()) {
            item.getNode().setStyle("-fx-bar-fill: " + colors[colorIndex % colors.length] + ";");
            colorIndex++;
        }
        
        animalTypesBox.getChildren().addAll(animalChartTitle, animalChartSubtitle, animalChart);
        
        chartsSection.getChildren().addAll(visitStatsBox, animalTypesBox);
        
        // Add all sections to dashboard
        dashboardContent.getChildren().addAll(header, statsCards, chartsSection);
        
        // Set dashboard content
        scrollPane.setContent(dashboardContent);
        
        // Update content area
        contentArea.getChildren().clear();
        contentArea.getChildren().add(scrollPane);
    }
    
    /**
     * Create a statistics card
     */
    private VBox createStatsCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        
        // Card header with title
        HBox header = new HBox();
        header.setPadding(new Insets(10, 15, 0, 15));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        header.getChildren().add(titleLabel);
        
        // Card content with value
        VBox content = new VBox();
        content.setPadding(new Insets(5, 15, 10, 15));
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        content.getChildren().add(valueLabel);
        
        // Colorful bottom border
        Region border = new Region();
        border.setPrefHeight(5);
        border.setMaxWidth(Double.MAX_VALUE);
        border.setStyle("-fx-background-color: " + color + ";");
        
        card.getChildren().addAll(header, content, border);
        
        return card;
    }
    
    /**
     * Show a simple message in the content area
     */
    private void showMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        StackPane messagePane = new StackPane();
        messagePane.getChildren().add(messageLabel);
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(messagePane);
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
