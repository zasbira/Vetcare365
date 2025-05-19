package org.vetjavafx.model;

import java.io.Serializable;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Classe visite
 */
public class Visite implements Serializable {
    private static final long serialVersionUID = 1L;

    private String date;
    private String description;
    private Veterinarian veterinarian;

    public Visite(String date, String description, Veterinarian veterinarian) {
        this.date = date;
        this.description = description;
        this.veterinarian = veterinarian;
    }

    public Visite() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(Veterinarian veterinarian) {
        this.veterinarian = veterinarian;
    }

    // Property methods for JavaFX binding
    public StringProperty dateProperty() {
        return new SimpleStringProperty(date);
    }

    public StringProperty descriptionProperty() {
        return new SimpleStringProperty(description);
    }

    public ObjectProperty<Veterinarian> veterinarianProperty() {
        return new SimpleObjectProperty<>(veterinarian);
    }
}















