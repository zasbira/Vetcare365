package org.example.interfacee;


import org.example.entites.Pets;


import java.util.List;

public interface IPetsimp {
    public boolean ajouterPet(Pets an);
    public boolean supprimerPet(int id);
    public boolean modifierPet(Pets an, int id);
    public Pets getPetsId(int id);
    public List<Pets> getPets();
    public List<Pets> afficherPetsNom(String nom);
}