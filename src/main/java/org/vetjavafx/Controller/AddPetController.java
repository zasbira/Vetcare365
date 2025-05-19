package org.vetjavafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.Pet;
import org.vetjavafx.model.DataManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Ajout animal
 */
public class AddPetController {

    // Composants de l'interface utilisateur
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;

    // Données
    private Owner owner;
    
    // Liste prédéfinie des types d'animaux pour la ComboBox
    private static final List<String> PET_TYPES = Arrays.asList(
        "Chien", "Chat", "Oiseau", "Rongeur", "Reptile"
    );

    /**
     * Initialise les composants du formulaire
     * Configure la liste déroulante des types d'animaux et le sélecteur de date
     */
    @FXML
    public void initialize() {
        // Remplit la liste déroulante des types d'animaux
        typeComboBox.getItems().addAll(PET_TYPES);
        
        // Configure le sélecteur de date au format français
        birthDatePicker.setPromptText("JJ/MM/AAAA");
    }

    /**
     * Définit le propriétaire pour le nouvel animal
     * @param owner Le propriétaire à associer au nouvel animal
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Gère le clic sur le bouton de sauvegarde
     * Valide les entrées et crée un nouvel animal
     */
    @FXML
    private void handleSaveButtonClick() {
        try {
            // Valide tous les champs requis
            if (nameField.getText().isEmpty()) {
                throw new IllegalArgumentException("Le nom de l'animal est requis");
            }
            if (typeComboBox.getValue() == null) {
                throw new IllegalArgumentException("Le type d'animal est requis");
            }
            if (birthDatePicker.getValue() == null) {
                throw new IllegalArgumentException("La date de naissance est requise");
            }

            // Formate la date au format français
            String formattedDate = birthDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Crée le nouvel objet animal
            Pet newPet = new Pet(
                nameField.getText(),
                formattedDate,
                typeComboBox.getValue()
            );

            // Enregistre l'animal dans le dossier du propriétaire
            DataManager.addPetToOwner(owner, newPet);

            // Affiche le message de succès
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("L'animal a été ajouté avec succès.");
            alert.showAndWait();

            // Retourne à l'écran des détails du propriétaire
            navigateToOwnerDetails();
        } catch (Exception e) {
            // Affiche un message d'erreur en cas de problème
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'ajout de l'animal: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gère le clic sur le bouton retour
     * Retourne à l'écran des détails du propriétaire
     */
    @FXML
    private void handleBackButtonClick() throws IOException {
        navigateToOwnerDetails();
    }

    /**
     * Navigue vers l'écran des détails du propriétaire
     */
    private void navigateToOwnerDetails() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/ownerDetails.fxml"));
        AnchorPane root = loader.load();
        OwnerDetailsController controller = loader.getController();
        controller.setOwner(owner);

        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
} 