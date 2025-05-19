/**
 * Fait par: [Votre Nom]
 * Date: [Date Actuelle]
 * Description: Gestion des données
 */
package org.vetjavafx.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String OWNERS_FILE = "owners.dat";
    private static List<Owner> ownersCache = new ArrayList<>();

    public static void saveOwners(List<Owner> owners) {
        try {
            ownersCache = new ArrayList<>(owners);
            

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(OWNERS_FILE))) {
                oos.writeObject(ownersCache);
                System.out.println("Owners saved successfully: " + ownersCache.size() + " owners");
            }
        } catch (IOException e) {
            System.err.println("Error saving owners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Owner> loadOwners() {
        File file = new File(OWNERS_FILE);
        if (!file.exists()) {
            System.out.println("No owners file found, creating new list");
            ownersCache = new ArrayList<>();
            return ownersCache;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(OWNERS_FILE))) {
            ownersCache = (List<Owner>) ois.readObject();
            System.out.println("Owners loaded successfully: " + ownersCache.size() + " owners");
            return new ArrayList<>(ownersCache);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading owners: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(ownersCache); // Return current cache if file read fails
        }
    }

    public static void updateOwner(Owner updatedOwner) {
        List<Owner> owners = loadOwners(); // Always get fresh data
        boolean found = false;
        
        for (int i = 0; i < owners.size(); i++) {
            if (owners.get(i).getId() == updatedOwner.getId()) {
                owners.set(i, updatedOwner);
                found = true;
                break;
            }
        }
        
        if (!found) {
            owners.add(updatedOwner);
        }
        
        saveOwners(owners);
    }

    public static void addPetToOwner(Owner owner, Pet pet) {
        owner.addPet(pet);
        updateOwner(owner);
    }

    public static void addVisitToPet(Owner owner, Pet pet, Visite visit) {
        pet.addVisite(visit);
        updateOwner(owner);
    }

    public static void deleteOwner(Owner ownerToDelete) {
        List<Owner> owners = loadOwners(); // Always get fresh data
        owners.removeIf(owner -> owner.getId() == ownerToDelete.getId());
        saveOwners(owners);
    }
} 