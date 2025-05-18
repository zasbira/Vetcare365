package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class representing an Owner/Client of the veterinary clinic
 */
public class Proprietaire {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty prenom;
    private final SimpleStringProperty adresse;
    private final SimpleStringProperty telephone;
    private final ObservableList<Animal> animaux;

    public Proprietaire(int id, String nom, String prenom, String adresse, String telephone) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.adresse = new SimpleStringProperty(adresse);
        this.telephone = new SimpleStringProperty(telephone);
        this.animaux = FXCollections.observableArrayList();
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getPrenom() {
        return prenom.get();
    }

    public String getAdresse() {
        return adresse.get();
    }

    public String getTelephone() {
        return telephone.get();
    }

    public ObservableList<Animal> getAnimaux() {
        return animaux;
    }

    // Property getters for TableView binding
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public SimpleStringProperty prenomProperty() {
        return prenom;
    }

    public SimpleStringProperty adresseProperty() {
        return adresse;
    }

    public SimpleStringProperty telephoneProperty() {
        return telephone;
    }

    // Setters
    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public void setAdresse(String adresse) {
        this.adresse.set(adresse);
    }

    public void setTelephone(String telephone) {
        this.telephone.set(telephone);
    }

    // Method to add an animal to the owner
    public void addAnimal(Animal animal) {
        this.animaux.add(animal);
    }

    @Override
    public String toString() {
        return prenom.get() + " " + nom.get();
    }
}
