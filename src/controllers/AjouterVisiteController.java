package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Animal;
import models.Proprietaire;
import models.Veterinaire;
import models.Visite;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for the add visit form
 */
public class AjouterVisiteController implements Initializable {

    @FXML
    private Label proprietaireLabel;
    
    @FXML
    private Label animalLabel;
    
    @FXML
    private DatePicker dateVisite;
    
    @FXML
    private ComboBox<Veterinaire> veterinaireComboBox;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private TableView<Visite> visitesPrecedentesTable;
    
    @FXML
    private TableColumn<Visite, LocalDate> dateColumn;
    
    @FXML
    private TableColumn<Visite, String> descriptionColumn;
    
    @FXML
    private TableColumn<Visite, String> veterinaireColumn;
    
    private Animal animal;
    private Proprietaire proprietaire;
    
    // Sample data for veterinarians - in a real app this would come from the backend
    private final Veterinaire[] veterinairesData = {
        new Veterinaire(1, "Dubois", "Marie", "Médecine générale", "m.dubois@vetcare360.fr", "01 23 45 67 89"),
        new Veterinaire(2, "Martin", "Jean", "Chirurgie", "j.martin@vetcare360.fr", "01 23 45 67 90"),
        new Veterinaire(3, "Bernard", "Sophie", "Dermatologie", "s.bernard@vetcare360.fr", "01 23 45 67 91"),
        new Veterinaire(4, "Petit", "Pierre", "Médecine générale", "p.petit@vetcare360.fr", "01 23 45 67 92"),
        new Veterinaire(5, "Robert", "Claire", "Ophtalmologie", "c.robert@vetcare360.fr", "01 23 45 67 93")
    };
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the default date to today
        dateVisite.setValue(LocalDate.now());
        
        // Set up the veterinarians dropdown
        veterinaireComboBox.setItems(FXCollections.observableArrayList(veterinairesData));
        
        // Set up the display of veterinarian names in the dropdown
        veterinaireComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Veterinaire>() {
            @Override
            protected void updateItem(Veterinaire item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Dr. " + item.getPrenom() + " " + item.getNom() + " (" + item.getSpecialite() + ")");
                }
            }
        });
        
        // Set up the button cell to show the selected veterinarian
        veterinaireComboBox.setButtonCell(new javafx.scene.control.ListCell<Veterinaire>() {
            @Override
            protected void updateItem(Veterinaire item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || item == null) {
                    setText("Sélectionnez un vétérinaire");
                } else {
                    setText("Dr. " + item.getPrenom() + " " + item.getNom());
                }
            }
        });
        
        // Set up the table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        veterinaireColumn.setCellValueFactory(new PropertyValueFactory<>("veterinaireNom"));
    }
    
    /**
     * Set the animal for this visit
     */
    public void setAnimal(Animal animal, Proprietaire proprietaire) {
        this.animal = animal;
        this.proprietaire = proprietaire;
        
        // Set the labels
        proprietaireLabel.setText(proprietaire.getPrenom() + " " + proprietaire.getNom());
        animalLabel.setText(animal.getNom() + " (" + animal.getEspece() + ")");
        
        // Load previous visits
        visitesPrecedentesTable.setItems(animal.getVisites());
    }
    
    /**
     * Handle the save button action
     */
    @FXML
    private void enregistrerVisite(ActionEvent event) {
        // Validate form fields
        if (validateForm()) {
            // Create new visit
            LocalDate date = dateVisite.getValue();
            String description = descriptionField.getText().trim();
            Veterinaire veterinaire = veterinaireComboBox.getValue();
            
            // Generate a temporary ID (in a real app, this would be handled by the backend)
            int newId = (int) (Math.random() * 10000);
            
            Visite nouvelleVisite = new Visite(
                newId, 
                date, 
                description, 
                animal.getId(), 
                veterinaire.getId(), 
                "Dr. " + veterinaire.getPrenom() + " " + veterinaire.getNom()
            );
            
            // Add the visit to the animal
            animal.addVisite(nouvelleVisite);
            
            // In a real app, we would save to the backend here
            
            // Navigate to the synthesis view with the visit details
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/synthese_visite.fxml"));
                Parent root = loader.load();
                
                // Get the controller and set the visit data
                SyntheseVisiteController controller = loader.getController();
                controller.setData(proprietaire, animal, nouvelleVisite);
                
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("VetCare 360 - Synthèse de la Visite");
                Scene scene = new Scene(root, 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("Error navigating to synthesis view");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Validate the form fields
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();
        
        if (dateVisite.getValue() == null) {
            errors.append("- La date est obligatoire\n");
        }
        
        if (veterinaireComboBox.getValue() == null) {
            errors.append("- Le vétérinaire est obligatoire\n");
        }
        
        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("- La description est obligatoire\n");
        }
        
        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }
        
        return true;
    }
    
    /**
     * Navigate back to the owner details
     */
    @FXML
    private void retourDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/details_proprietaire.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the proprietaire
            DetailsProprietaireController controller = loader.getController();
            controller.setProprietaire(proprietaire);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("VetCare 360 - Détails du Propriétaire");
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/resources/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error navigating to details view");
            e.printStackTrace();
        }
    }
}
