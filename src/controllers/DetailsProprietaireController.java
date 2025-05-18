package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Animal;
import models.Proprietaire;
import models.Visite;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the owner details view
 */
public class DetailsProprietaireController {

    @FXML
    private Label nomLabel;
    
    @FXML
    private Label prenomLabel;
    
    @FXML
    private Label adresseLabel;
    
    @FXML
    private Label telephoneLabel;
    
    @FXML
    private Accordion animauxAccordion;
    
    @FXML
    private Label emptyAnimauxLabel;
    
    private Proprietaire proprietaire;
    
    /**
     * Set the proprietaire and update the UI
     */
    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        
        // Update the owner details
        nomLabel.setText(proprietaire.getNom());
        prenomLabel.setText(proprietaire.getPrenom());
        adresseLabel.setText(proprietaire.getAdresse());
        telephoneLabel.setText(proprietaire.getTelephone());
        
        // Load the animals
        loadAnimaux();
    }
    
    /**
     * Load the owner's animals into the accordion
     */
    private void loadAnimaux() {
        animauxAccordion.getPanes().clear();
        
        if (proprietaire.getAnimaux().isEmpty()) {
            emptyAnimauxLabel.setVisible(true);
            animauxAccordion.setVisible(false);
            return;
        }
        
        emptyAnimauxLabel.setVisible(false);
        animauxAccordion.setVisible(true);
        
        // For each animal, create a titled pane with its details and visits
        for (Animal animal : proprietaire.getAnimaux()) {
            TitledPane animalPane = createAnimalPane(animal);
            animauxAccordion.getPanes().add(animalPane);
        }
        
        // Expand the first animal pane if any exist
        if (!animauxAccordion.getPanes().isEmpty()) {
            animauxAccordion.setExpandedPane(animauxAccordion.getPanes().get(0));
        }
    }
    
    /**
     * Create a titled pane for an animal with its details and visits
     */
    private TitledPane createAnimalPane(Animal animal) {
        VBox content = new VBox(10);
        content.getStyleClass().add("form-container");
        
        // Animal details
        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().addAll(
            new Label("Espèce: " + animal.getEspece()),
            new Label("Sexe: " + animal.getSexe()),
            new Label("Âge: " + animal.getAge() + " ans")
        );
        
        // Actions for this animal
        HBox actionsBox = new HBox(10);
        Button modifyButton = new Button("📝 Modifier");
        modifyButton.setOnAction(e -> modifierAnimal(animal));
        
        Button addVisitButton = new Button("➕ Ajouter une visite");
        addVisitButton.setOnAction(e -> ajouterVisite(animal));
        
        actionsBox.getChildren().addAll(modifyButton, addVisitButton);
        
        // Visits section
        VBox visitsBox = new VBox(10);
        Label visitsLabel = new Label("Visites");
        visitsLabel.getStyleClass().add("section-title");
        visitsBox.getChildren().add(visitsLabel);
        
        // Create a simple table or list for visits
        if (animal.getVisites().isEmpty()) {
            visitsBox.getChildren().add(new Label("Aucune visite enregistrée"));
        } else {
            // Simple representation of visits - in a real app, use a TableView
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Visite visite : animal.getVisites()) {
                HBox visiteRow = new HBox(10);
                visiteRow.getChildren().addAll(
                    new Label(visite.getDate().format(formatter)),
                    new Label(visite.getDescription()),
                    new Label("Dr. " + visite.getVeterinaireNom())
                );
                visitsBox.getChildren().add(visiteRow);
            }
        }
        
        // Add all components to the content
        content.getChildren().addAll(detailsBox, actionsBox, visitsBox);
        
        // Create the titled pane with the animal's name as title
        TitledPane animalPane = new TitledPane(animal.getNom(), content);
        return animalPane;
    }
    
    /**
     * Navigate to modify animal view
     */
    private void modifierAnimal(Animal animal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modifier_animal.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the animal to modify
            ModifierAnimalController controller = loader.getController();
            controller.setAnimal(animal, proprietaire);
            
            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setTitle("VetCare 360 - Modifier Animal");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to modify animal view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to add visit view
     */
    private void ajouterVisite(Animal animal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouter_visite.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the animal
            AjouterVisiteController controller = loader.getController();
            controller.setAnimal(animal, proprietaire);
            
            Stage stage = (Stage) nomLabel.getScene().getWindow();
            stage.setTitle("VetCare 360 - Ajouter Visite");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to add visit view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to modify owner view
     */
    @FXML
    private void modifierProprietaire(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/modifier_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire to modify
            ModifierProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Modifier Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to modify owner view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to add animal view
     */
    @FXML
    private void ajouterAnimal(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ajouter_animal.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the owner
            AjouterAnimalController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Ajouter Animal");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to add animal view");
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate back to the search page
     */
    @FXML
    private void retourRecherche(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/recherche_proprietaire.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Recherche Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to search view");
            e.printStackTrace();
        }
    }
}
