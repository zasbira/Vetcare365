package org.example.interfacee;

import org.example.entites.Owner;


import java.util.List;

public interface IOwnersimp {
    public boolean ajouterOwner(Owner et);
    public boolean supprimerOwner(int id);
    public boolean modifierOwnert(Owner et, int id);
    public Owner getOwnerId(int id);
    public List<Owner> getOwners();
    public List<Owner> afficherOwnersNom(String nom);
}