package org.example.entites;

public class Pets {
    private int id;
    private String name;
    private int dateNais;  // Using int for date (YYYYMMDD format)
    private int ownerId;
    private String type;

    // Constructor
    public Pets(int id, String name, int dateNais, int ownerId, String type) {
        this.id = id;
        this.name = name;
        this.dateNais = dateNais;
        this.ownerId = ownerId;
        this.type = type;
    }

    public Pets(int updateId, String newname, String newdate, String newtype) {
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDateNais() {
        return dateNais;
    }

    public void setDateNais(int dateNais) {
        this.dateNais = dateNais;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Pets{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateNais=" + dateNais +
                ", ownerId=" + ownerId +
                ", type='" + type + '\'' +
                '}';
    }
}
