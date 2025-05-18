package org.example;

import org.example.dao.*;
import org.example.entites.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PetsDao petsDao = new PetsDao();
        OwnersDao ownersDao = new OwnersDao();
        VisitDao visitDao = new VisitDao();
        ViterinaireDao viterinaireDao = new ViterinaireDao();

        while (true) {
            System.out.println("\n------------ MENU ------------");
            System.out.println("1 - Ajouter un animal");
            System.out.println("2 - Modifier un animal");
            System.out.println("3 - Supprimer un animal");
            System.out.println("4 - Rechercher un animal par ID");
            System.out.println("5 - Rechercher un animal par nom");
            System.out.println("6 - Lister tous les animaux");
            System.out.println("7 - Ajouter un propriétaire");
            System.out.println("8 - Modifier un propriétaire");
            System.out.println("9 - Supprimer un propriétaire");
            System.out.println("10 - Rechercher un propriétaire par ID");
            System.out.println("11 - Rechercher un propriétaire par nom");
            System.out.println("12 - Lister tous les propriétaires");
            System.out.println("13 - Ajouter une visite");
            System.out.println("14 - Modifier une visite");
            System.out.println("15 - Supprimer une visite");
            System.out.println("16 - Lister toutes les visites");
            System.out.println("17 - Rechercher les visites d'un animal");
            System.out.println("18 - Ajouter un vétérinaire");
            System.out.println("19 - Modifier un vétérinaire");
            System.out.println("20 - Supprimer un vétérinaire");
            System.out.println("21 - Rechercher un vétérinaire par ID");
            System.out.println("22 - Lister tous les vétérinaires");
            System.out.println("23 - Quitter");
            System.out.print("Choix : ");
            int choix = sc.nextInt();
            sc.nextLine();

            try {
                switch (choix) {
                    case 1 -> {
                        System.out.print("Nom : ");
                        String name = sc.nextLine();
                        System.out.print("Type : ");
                        String type = sc.nextLine();
                        System.out.print("Date de naissance (AAAAMMJJ) : ");
                        int dateNais = sc.nextInt();
                        sc.nextLine();
                        System.out.print("ID propriétaire : ");
                        int ownerId = sc.nextInt();
                        sc.nextLine();
                        Pets newPet = new Pets(0, name, dateNais, ownerId, type);
                        if (PetsDao.createPet(newPet)) {
                            System.out.println("Animal ajouté.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 2 -> {
                        System.out.print("ID animal : ");
                        int petIdUpdate = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Nom : ");
                        String newName = sc.nextLine();
                        System.out.print("Type : ");
                        String newType = sc.nextLine();
                        System.out.print("Date naissance (AAAAMMJJ) : ");
                        int newDate = sc.nextInt();
                        sc.nextLine();
                        System.out.print("ID propriétaire : ");
                        int newOwnerId = sc.nextInt();
                        sc.nextLine();
                        Pets updatedPet = new Pets(petIdUpdate, newName, newDate, newOwnerId, newType);
                        if (PetsDao.updatePet(petIdUpdate, updatedPet)) {
                            System.out.println("Animal mis à jour.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 3 -> {
                        System.out.print("ID animal à supprimer : ");
                        int idDeletePet = sc.nextInt();
                        if (PetsDao.deletePet(idDeletePet)) {
                            System.out.println("Supprimé.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 4 -> {
                        System.out.print("ID : ");
                        int petId = sc.nextInt();
                        Pets p = PetsDao.getPetById(petId);
                        System.out.println(p != null ? p : "Introuvable.");
                    }
                    case 5 -> {
                        System.out.print("Nom : ");
                        String nomSearch = sc.nextLine();
                        List<Pets> petsByName = PetsDao.findPetsByName(nomSearch);
                        petsByName.forEach(System.out::println);
                    }
                    case 6 -> PetsDao.getAllPets().forEach(System.out::println);
                    case 7 -> {
                        System.out.print("Prénom : ");
                        String fn = sc.nextLine();
                        System.out.print("Nom : ");
                        String ln = sc.nextLine();
                        System.out.print("Adresse : ");
                        String adress = sc.nextLine();
                        System.out.print("Ville : ");
                        String city = sc.nextLine();
                        System.out.print("Téléphone : ");
                        int telephone = sc.nextInt();
                        sc.nextLine();
                        Owner o = new Owner(0, fn, ln, adress, city, telephone, null);
                        if (ownersDao.createOwner(o)) {
                            System.out.println("Ajouté.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 8 -> {
                        System.out.print("ID : ");
                        int idUpdateOwner = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Prénom : ");
                        String fnU = sc.nextLine();
                        System.out.print("Nom : ");
                        String lnU = sc.nextLine();
                        System.out.print("Adresse : ");
                        String adressU = sc.nextLine();
                        System.out.print("Ville : ");
                        String cityU = sc.nextLine();
                        System.out.print("Téléphone : ");
                        int telephoneU = sc.nextInt();
                        sc.nextLine();
                        Owner updatedOwner = new Owner(idUpdateOwner, fnU, lnU, adressU, cityU, telephoneU, null);
                        if (ownersDao.updateOwner(idUpdateOwner, updatedOwner)) {
                            System.out.println("Mis à jour.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 9 -> {
                        System.out.print("ID : ");
                        int idDel = sc.nextInt();
                        if (ownersDao.deleteOwner(idDel)) {
                            System.out.println("Supprimé.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 10 -> {
                        System.out.print("ID : ");
                        int idOwn = sc.nextInt();
                        Owner own = ownersDao.getOwnerById(idOwn);
                        System.out.println(own != null ? own : "Introuvable.");
                    }
                    case 11 -> {
                        System.out.print("Nom : ");
                        String nameOwner = sc.nextLine();
                        ownersDao.findOwnersByName(nameOwner).forEach(System.out::println);
                    }
                    case 12 -> ownersDao.getAllOwners().forEach(System.out::println);
                    case 13 -> {
                        System.out.print("Date visite (AAAAMMJJ) : ");
                        int dateVisite = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Description : ");
                        String desc = sc.nextLine();
                        System.out.print("ID animal : ");
                        int idAnimal = sc.nextInt();
                        sc.nextLine();
                        Pets pet = PetsDao.getPetById(idAnimal);
                        if (pet != null) {
                            Visite v = new Visite(0, dateVisite, desc, pet, idAnimal);
                            if (visitDao.createVisite(v)) {
                                System.out.println("Visite ajoutée.");
                            } else {
                                System.out.println("Erreur.");
                            }
                        } else {
                            System.out.println("Animal introuvable!");
                        }
                    }
                    case 14 -> {
                        System.out.print("ID visite : ");
                        int idVis = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Date (AAAAMMJJ) : ");
                        int dateUp = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Description : ");
                        String descUp = sc.nextLine();
                        System.out.print("ID animal : ");
                        int idPet = sc.nextInt();
                        sc.nextLine();
                        Pets petUp = PetsDao.getPetById(idPet);
                        if (petUp != null) {
                            Visite updated = new Visite(idVis, dateUp, descUp, petUp, idPet);
                            if (visitDao.updateVisite(idVis, updated)) {
                                System.out.println("Modifié.");
                            } else {
                                System.out.println("Erreur.");
                            }
                        } else {
                            System.out.println("Animal introuvable!");
                        }
                    }
                    case 15 -> {
                        System.out.print("ID visite à supprimer : ");
                        int idVsup = sc.nextInt();
                        if (visitDao.deleteVisite(idVsup)) {
                            System.out.println("Supprimée.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 16 -> visitDao.getAllVisites().forEach(System.out::println);
                    case 17 -> {
                        System.out.print("ID animal : ");
                        int idp = sc.nextInt();
                        visitDao.findVisitesByPetId(idp).forEach(System.out::println);
                    }
                    case 18 -> {
                        System.out.print("Nom : ");
                        String nomV = sc.nextLine();
                        System.out.print("Spécialité : ");
                        String sp = sc.nextLine();
                        Viterinaire vet = new Viterinaire(0, nomV, sp);
                        if (viterinaireDao.createViterinaire(vet)) {
                            System.out.println("Ajouté.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 19 -> {
                        System.out.print("ID : ");
                        int idVet = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Nom : ");
                        String nomVm = sc.nextLine();
                        System.out.print("Spécialité : ");
                        String spm = sc.nextLine();
                        Viterinaire vetMod = new Viterinaire(idVet, nomVm, spm);
                        if (viterinaireDao.updateViterinaire(idVet, vetMod)) {
                            System.out.println("Modifié.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 20 -> {
                        System.out.print("ID vétérinaire à supprimer : ");
                        int idV = sc.nextInt();
                        if (viterinaireDao.deleteViterinaire(idV)) {
                            System.out.println("Supprimé.");
                        } else {
                            System.out.println("Erreur.");
                        }
                    }
                    case 21 -> {
                        System.out.print("ID : ");
                        int idVetSearch = sc.nextInt();
                        Viterinaire foundVet = viterinaireDao.getViterinaireById(idVetSearch);
                        System.out.println(foundVet != null ? foundVet : "Introuvable.");
                    }
                    case 22 -> ViterinaireDao.getAllViterinaires().forEach(System.out::println);
                    case 23 -> {
                        System.out.println("Merci et à bientôt !");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur: " + e.getMessage());
                sc.nextLine(); // Clear invalid input
            }
        }
    }
}