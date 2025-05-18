package org.example.interfacee;


import org.example.entites.Pets;
import org.example.entites.Visite;


import java.util.List;

public interface IVisitimp {
    public boolean ajouterVisite(Visite an);
    public boolean supprimerVisite(int id);
    public boolean modifierVisite(Visite an, int id);
    public Pets getVisiteId(int id);
    public List<Pets> getVisites();
    public List<Pets> afficherVisiteNom(String nom);
}