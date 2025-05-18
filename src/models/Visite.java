package models;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model class representing a Visit/Consultation
 */
public class Visite {
    private final SimpleIntegerProperty id;
    private final SimpleObjectProperty<LocalDate> date;
    private final SimpleStringProperty motif;
    private final SimpleStringProperty diagnostic;
    private final SimpleStringProperty traitement;
    private final SimpleIntegerProperty animalId;
    private final SimpleIntegerProperty veterinaireId;

    public Visite(int id, LocalDate date, String motif, String diagnostic, String traitement, int animalId, int veterinaireId) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleObjectProperty<>(date);
        this.motif = new SimpleStringProperty(motif);
        this.diagnostic = new SimpleStringProperty(diagnostic);
        this.traitement = new SimpleStringProperty(traitement);
        this.animalId = new SimpleIntegerProperty(animalId);
        this.veterinaireId = new SimpleIntegerProperty(veterinaireId);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public LocalDate getDate() {
        return date.get();
    }

    public String getMotif() {
        return motif.get();
    }

    public String getDiagnostic() {
        return diagnostic.get();
    }

    public String getTraitement() {
        return traitement.get();
    }

    public int getAnimalId() {
        return animalId.get();
    }

    public int getVeterinaireId() {
        return veterinaireId.get();
    }

    // Veterinaire name should be retrieved from VeterinaireService

    // Property getters for TableView binding
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public SimpleStringProperty motifProperty() {
        return motif;
    }
    
    public SimpleStringProperty diagnosticProperty() {
        return diagnostic;
    }
    
    public SimpleStringProperty traitementProperty() {
        return traitement;
    }

    public SimpleIntegerProperty animalIdProperty() {
        return animalId;
    }

    public SimpleIntegerProperty veterinaireIdProperty() {
        return veterinaireId;
    }

    // No veterinaireNom property anymore

    // Setters
    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public void setMotif(String motif) {
        this.motif.set(motif);
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic.set(diagnostic);
    }

    public void setTraitement(String traitement) {
        this.traitement.set(traitement);
    }

    public void setVeterinaireId(int veterinaireId) {
        this.veterinaireId.set(veterinaireId);
    }

    // No setter for veterinaireNom anymore
}
