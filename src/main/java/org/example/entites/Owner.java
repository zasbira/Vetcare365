package org.example.entites;

import java.util.List;

public class Owner {
    private int id;
    private String firstName;
    private String lastName;
    private String adress;
    private String city;
    private int telephone;
    private List<Pets> pets;  // Change from Pets to List<Pets>

    public Owner(int id, String firstName, String lastName, String adress, String city, int telephone, List<Pets> pets) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.adress = adress;
        this.city = city;
                this.telephone = telephone;
                this.pets = pets;
    }

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

    public  String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", adress='" + adress + '\'' +
                ", city='" + city + '\'' +
                ", telephone=" + telephone +
                ", pets=" + pets +
                '}';
    }

    public List<Pets> getPets() {
        return pets;
    }

    public void setPets(List<Pets> pets) {  // Make sure this accepts List<Pets>
        this.pets = pets;
    }
}
