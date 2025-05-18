package org.example.interfacee;


import org.example.entites.Pets;
import org.example.entites.Visite;


import java.util.List;

public interface IViterinaireimp {
    public boolean ajouterViterinaire(Visite an);
    public boolean supprimerViterinaire(int id);
    public boolean modifierViterinaire(Visite an, int id);
    public Pets getViterinaireId(int id);
    public List<Pets> getViterinaires();
    public List<Pets> afficherViterinaireNom(String nom);
}