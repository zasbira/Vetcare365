package org.vetjavafx.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.vetjavafx.model.Owner;
import org.vetjavafx.model.Pet;
import org.vetjavafx.model.DataManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Modification animal
 */
public class ModifyPetController {
    // Composants de l'interface utilisateur
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField birthDateField;
    @FXML
    private Button saveButton;
    @FXML
    private Button addVisitButton;
    @FXML
    private Button backButton;

    // Données
    private Owner owner;
    private Pet pet;

    // Liste prédéfinie des types d'animaux pour la ComboBox
    private static final List<String> PET_TYPES = Arrays.asList(
        "Chien", "Chat", "Oiseau", "Rongeur", "Reptile"
    );

    /**
     * Initialise les composants du formulaire
     * Configure la liste déroulante des types d'animaux
     */
    @FXML
    public void initialize() {
        // Remplit la liste déroulante des types d'animaux
        typeComboBox.getItems().addAll(PET_TYPES);
    }

    /**
     * Définit le propriétaire et l'animal à modifier
     * @param owner Le propriétaire de l'animal
     * @param pet L'animal à modifier
     */
    public void setOwnerAndPet(Owner owner, Pet pet) {
        this.owner = owner;
        this.pet = pet;
        updateFields();
    }

    /**
     * Met à jour les champs du formulaire avec les informations actuelles de l'animal
     */
    private void updateFields() {
        nameField.setText(pet.getName());
        typeComboBox.setValue(pet.getType());
        birthDateField.setText(pet.getBirthDate());
    }

    /**
     * Gère le clic sur le bouton de sauvegarde
     * Met à jour les informations de l'animal et enregistre les modifications
     */
    @FXML
    private void handleSaveButtonClick() throws IOException {
        try {
            // Validation des champs requis
            if (nameField.getText().isEmpty()) {
                throw new IllegalArgumentException("Le nom de l'animal est requis");
            }
            if (typeComboBox.getValue() == null) {
                throw new IllegalArgumentException("Le type d'animal est requis");
            }
            if (birthDateField.getText().isEmpty()) {
                throw new IllegalArgumentException("La date de naissance est requise");
            }
            
            // Met à jour les informations de l'animal avec les nouvelles valeurs
            pet.setName(nameField.getText());
            pet.setType(typeComboBox.getValue());
            pet.setBirthDate(birthDateField.getText());

            // Mise à jour de l'animal dans le propriétaire
            int petIndex = owner.getPets().indexOf(pet);
            if (petIndex >= 0) {
                owner.getPets().set(petIndex, pet);
            }
            
            // Enregistre les modifications dans le stockage persistant
            DataManager.updateOwner(owner);

            // Affiche un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Les informations de l'animal ont été modifiées avec succès.");
            alert.showAndWait();
            
            // Retourne à l'écran des détails du propriétaire
            navigateToOwnerDetails();
        } catch (Exception e) {
            // Affiche un message d'erreur en cas de problème
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de la modification de l'animal: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gère le clic sur le bouton d'ajout de visite
     * Ouvre le formulaire d'ajout de visite pour cet animal
     */
    @FXML
    private void handleAddVisitButtonClick() throws IOException {
        // Charge le formulaire d'ajout de visite
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/addVisit.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        // Configure le contrôleur d'ajout de visite avec le propriétaire et l'animal actuels
        AddVisitController controller = loader.getController();
        controller.setOwnerAndPet(owner, pet);

        // Affiche le formulaire d'ajout de visite
        Stage stage = (Stage) addVisitButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
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
        // Charge le formulaire des détails du propriétaire
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vetjavafx/view/ownerDetails.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);

        // Configure le contrôleur des détails du propriétaire
        OwnerDetailsController controller = loader.getController();
        controller.setOwner(owner);

        // Affiche le formulaire des détails du propriétaire
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
} 