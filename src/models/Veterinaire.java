package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model class representing a Veterinarian
 */
public class Veterinaire {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty prenom;
    private final SimpleStringProperty specialite;
    private final SimpleStringProperty email;
    private final SimpleStringProperty telephone;

    public Veterinaire(int id, String nom, String prenom, String specialite, String email, String telephone) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.specialite = new SimpleStringProperty(specialite);
        this.email = new SimpleStringProperty(email);
        this.telephone = new SimpleStringProperty(telephone);
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

    public String getSpecialite() {
        return specialite.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getTelephone() {
        return telephone.get();
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

    public SimpleStringProperty specialiteProperty() {
        return specialite;
    }

    public SimpleStringProperty emailProperty() {
        return email;
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

    public void setSpecialite(String specialite) {
        this.specialite.set(specialite);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setTelephone(String telephone) {
        this.telephone.set(telephone);
    }

    @Override
    public String toString() {
        return prenom.get() + " " + nom.get();
    }
}
