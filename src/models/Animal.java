package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class representing an Animal/Pet
 */
public class Animal {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty espece;
    private final SimpleStringProperty sexe;
    private final SimpleIntegerProperty age;
    private final ObservableList<Visite> visites;
    private final SimpleIntegerProperty proprietaireId;

    public Animal(int id, String nom, String espece, String sexe, int age, int proprietaireId) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.espece = new SimpleStringProperty(espece);
        this.sexe = new SimpleStringProperty(sexe);
        this.age = new SimpleIntegerProperty(age);
        this.proprietaireId = new SimpleIntegerProperty(proprietaireId);
        this.visites = FXCollections.observableArrayList();
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getEspece() {
        return espece.get();
    }

    public String getSexe() {
        return sexe.get();
    }

    public int getAge() {
        return age.get();
    }

    public int getProprietaireId() {
        return proprietaireId.get();
    }

    public ObservableList<Visite> getVisites() {
        return visites;
    }

    // Property getters for TableView binding
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public SimpleStringProperty especeProperty() {
        return espece;
    }

    public SimpleStringProperty sexeProperty() {
        return sexe;
    }

    public SimpleIntegerProperty ageProperty() {
        return age;
    }

    public SimpleIntegerProperty proprietaireIdProperty() {
        return proprietaireId;
    }

    // Setters
    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setEspece(String espece) {
        this.espece.set(espece);
    }

    public void setSexe(String sexe) {
        this.sexe.set(sexe);
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    // Method to add a visit to the animal
    public void addVisite(Visite visite) {
        this.visites.add(visite);
    }

    @Override
    public String toString() {
        return nom.get() + " (" + espece.get() + ")";
    }
}
