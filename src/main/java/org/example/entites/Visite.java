package org.example.entites;

public class Visite {
    private int id;
    private int date;
    private String description;
    private Pets pet;  // Reference to the Pets object
    private int petId; // Additional petId field for the pet's ID

    // Constructor that accepts both the pet object and the petId
    public Visite(int id, int date, String description, Pets pet, int petId) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.pet = pet;
        this.petId = petId; // Store the petId separately
    }

    // Getter and setter for pet
    public Pets getPet() {
        return pet;
    }

    public void setPet(Pets pet) {
        this.pet = pet;
    }

    // Getter and setter for petId
    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    @Override
    public String toString() {
        return "Visite{" +
                "id=" + id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", pet=" + pet +
                ", petId=" + petId +  // Include petId in the output
                '}';
    }

    // Getter and setter for other fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
