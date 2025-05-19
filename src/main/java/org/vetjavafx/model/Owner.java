/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Classe propriétaire
 */
package org.vetjavafx.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Owner implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String phone;
    private ArrayList<Pet> pets = new ArrayList<>();

    public Owner(String firstName, String lastName, String address, String city, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.phone = phone;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }

    public void addPet(Pet newPet) {
        this.pets.add(newPet);
    }
}
