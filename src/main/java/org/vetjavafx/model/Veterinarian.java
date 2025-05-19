/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Classe vétérinaire
 */
package org.vetjavafx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Veterinarian {

    private final StringProperty name;
    private final StringProperty specialization;

    public Veterinarian(String name, String specialization) {
        this.name = new SimpleStringProperty(name);
        this.specialization = new SimpleStringProperty(specialization);
    }

    // Getter methods
    public String getName() {
        return name.get();
    }

    public String getSpecialization() {
        return specialization.get();
    }

    // Property methods for binding
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty specializationProperty() {
        return specialization;
    }
}
