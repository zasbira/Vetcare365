package org.example;

import org.example.dao.OwnersDao;
import org.example.dao.PetsDao;
import org.example.dao.VisitDao;
import org.example.dao.ViterinaireDao;
import org.example.entites.Owner;
import org.example.entites.Pets;
import org.example.entites.Visite;
import org.example.entites.Viterinaire;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PetsDao petsDao = new PetsDao();
        OwnersDao ownersDao = new OwnersDao();
        VisitDao visiteDao = new VisitDao();
        ViterinaireDao viterinaireDao = new ViterinaireDao();

        while (true) {
            System.out.println("----------------------------------------------------------");
            System.out.println("1- Ajouter un propriétaire");
            System.out.println("2- Modifier un propriétaire");
            System.out.println("3- Supprimer un propriétaire");
            System.out.println("4- Recherche par ID");
            System.out.println("5- Recherche par nom");
            System.out.println("6- Lister tous les propriétaires");
            System.out.println("7- Ajouter un Pets");
            System.out.println("8- Modifier un Pets");
            System.out.println("9- Supprimer un Pets");
            System.out.println("10- Recherche par ID");
            System.out.println("11- Recherche par nom");
            System.out.println("12- Lister tous les Pets");

            System.out.println("13- Ajouter une visite");
            System.out.println("14- Modifier une visite");
            System.out.println("15- Supprimer une visite");
            System.out.println("16- Recherche par ID de visite");
            System.out.println("17- Lister toutes les visites");

            System.out.println("18- Ajouter un vétérinaire");
            System.out.println("19- Modifier un vétérinaire");
            System.out.println("20- Supprimer un vétérinaire");
            System.out.println("21- Recherche par ID de vétérinaire");
            System.out.println("22- Recherche par nom de vétérinaire");
            System.out.println("23- Lister tous les vétérinaires");
            System.out.println("24- Quitter");
            System.out.println("----------------------------------------------------------");

            System.out.println("Entrez votre choix: ");
            int choix = sc.nextInt();
            sc.nextLine();  // Consume the newline left by nextInt()

            switch (choix) { case 1:
                // Add a new owner
                System.out.println("Saisissez le prénom du propriétaire: ");
                String firstName = sc.nextLine();
                System.out.println("Saisissez le nom de famille du propriétaire: ");
                String lastName = sc.nextLine();
                System.out.println("Saisissez le city du propriétaire: ");
                String city = sc.nextLine();
                System.out.println("Saisisser l'adress de proprietaire: ");
                String address = sc.nextLine();
                System.out.println("Saisissez le telephone du proprietaire: ");
                int telephone = sc.nextInt();

                Owner newOwner = new Owner(0, firstName, lastName,city,address,telephone,null);  // Assuming 0 is a placeholder for ID
                if (ownersDao.createOwner(newOwner)) {
                    System.out.println("Le propriétaire " + firstName + " " + lastName + " a bien été ajouté.");
                } else {
                    System.out.println("Erreur lors de l'ajout du propriétaire.");
                }
                break;

                case 2:
                    // Update an owner
                    System.out.println("Saisissez l'ID du propriétaire à modifier: ");
                    int updateOwnerId = sc.nextInt();
                    sc.nextLine();  // Consume the newline
                    System.out.println("Saisissez le nouveau prénom: ");
                    String newFirstName = sc.nextLine();
                    System.out.println("Saisissez le nouveau nom: ");
                    String newLastName = sc.nextLine();
                    System.out.println("Saisissez le nouvel city: ");
                    String newCity= sc.nextLine();
                    System.out.println("Saisissez le nouveau address: ");
                    String newAddress = sc.nextLine();
                    System.out.println("Saisissez le nouveau telephone: ");
                    int newTelephone = sc.nextInt();

                    Owner updatedOwner = new Owner(updateOwnerId, newFirstName, newLastName, newCity,newAddress,newTelephone,null );  // Pass updated details
                    if (ownersDao.updateOwner(updateOwnerId, updatedOwner)) {
                        System.out.println("Le propriétaire a été mis à jour.");
                    } else {
                        System.out.println("Erreur lors de la mise à jour du propriétaire.");
                    }
                    break;

                case 3:
                    // Delete an owner
                    System.out.println("Saisissez l'ID du propriétaire à supprimer: ");
                    int deleteOwnerId = sc.nextInt();
                    if (ownersDao.deleteOwner(deleteOwnerId)) {
                        System.out.println("Le propriétaire a été supprimé.");
                    } else {
                        System.out.println("Erreur lors de la suppression du propriétaire.");
                    }
                    break;

                case 4:
                    // Search owner by ID
                    System.out.println("Saisissez l'ID du propriétaire à rechercher: ");
                    int searchOwnerId = sc.nextInt();
                    Owner ownerById = ownersDao.getOwnerById(searchOwnerId);
                    if (ownerById != null) {
                        System.out.println("Propriétaire trouvé: " + ownerById);
                    } else {
                        System.out.println("Aucun propriétaire trouvé avec cet ID.");
                    }
                    break;

                case 5:
                    // Search owner by name
                    System.out.println("Saisissez le nom du propriétaire à rechercher: ");
                    String nameOwnerToSearch = sc.nextLine();
                    List<Owner> ownersByName = ownersDao.findOwnersByName(nameOwnerToSearch);
                    if (!ownersByName.isEmpty()) {
                        System.out.println("Propriétaires trouvés: ");
                        for (Owner owner : ownersByName) {
                            System.out.println(owner);
                        }
                    } else {
                        System.out.println("Aucun propriétaire trouvé avec ce nom.");
                    }
                    break;

                case 6:
                    // List all owners
                    List<Owner> allOwners = ownersDao.getAllOwners();
                    if (!allOwners.isEmpty()) {
                        System.out.println("Liste des propriétaires: ");
                        for (Owner owner : allOwners) {
                            System.out.println(owner);
                        }
                    } else {
                        System.out.println("Aucun propriétaire trouvé.");
                    }
                    break;
                case 7:
                    // Add a new pet
                    System.out.println("Saisissez le nom du Pets: ");
                    String name = sc.nextLine();
                    System.out.println("Saisissez type du Pets: ");
                    String type = sc.nextLine();
                    System.out.println("Saisissez la date de naissance du Pets (format YYYYMMDD): ");
                    int date_nais = sc.nextInt();
                    System.out.println("Saisissez l'ID du propriétaire: ");
                    int ownerId = sc.nextInt();

                    Pets newPet = new Pets(0, name, date_nais, ownerId, type);
                    if (petsDao.createPet(newPet)) {
                        System.out.println("L'animal " + name + " " + type + " a bien été ajouté.");
                    } else {
                        System.out.println("Erreur lors de l'ajout de l'animal.");
                    }
                    break;

                case 8:
                    // Update a pet
                    System.out.println("Saisissez l'ID du animal à modifier: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();  // Consume the newline
                    System.out.println("Saisissez le nouveau nom du Pets: ");
                    String newName = sc.nextLine();
                    System.out.println("Saisissez le nouveau type du Pets: ");
                    String newType = sc.nextLine();
                    System.out.println("Saisissez la nouvelle date de naissance du Pets (format YYYYMMDD): ");
                    int newDate = sc.nextInt();
                    System.out.println("Saisissez id de owner du Pets: ");
                    int newOwnerId = sc.nextInt();

                    Pets updatedPet = new Pets(updateId, newName, newDate, newOwnerId, newType);  // Assuming ownerId is 1
                    if (petsDao.updatePet(updateId, updatedPet)) {
                        System.out.println("L'animal a été mis à jour.");
                    } else {
                        System.out.println("Erreur lors de la mise à jour de l'animal.");
                    }
                    break;

                case 9:
                    // Delete a pet
                    System.out.println("Saisissez l'ID du animal à supprimer: ");
                    int deleteId = sc.nextInt();
                    if (petsDao.deletePet(deleteId)) {
                        System.out.println("L'animal a été supprimé.");
                    } else {
                        System.out.println("Erreur lors de la suppression de l'animal.");
                    }
                    break;

                case 10:
                    // Search pet by ID
                    System.out.println("Saisissez l'ID du animal à rechercher: ");
                    int searchId = sc.nextInt();

                    Pets petById = petsDao.getPetById(searchId);
                    if (petById != null) {
                        System.out.println("Animal trouvé: " + petById.getName());
                    } else {
                        System.out.println("Aucun animal trouvé avec cet ID.");
                    }
                    break;

                case 11:
                    // Search pets by name
                    System.out.println("Saisissez le nom du animal à rechercher: ");
                    String nameToSearch = sc.nextLine();
                    List<Pets> petsByName = petsDao.findPetsByName(nameToSearch);
                    if (!petsByName.isEmpty()) {
                        System.out.println("Animaux trouvés: ");
                        for (Pets pet : petsByName) {
                            System.out.println(pet.getName());
                        }
                    } else {
                        System.out.println("Aucun animal trouvé avec ce nom.");
                    }
                    break;

                case 12:
                    // List all pets
                    List<Pets> allPets = petsDao.getAllPets();
                    if (!allPets.isEmpty()) {
                        System.out.println("Liste des animaux: ");
                        for (Pets pet : allPets) {
                            System.out.println(pet.getName());
                        }
                    } else {
                        System.out.println("Aucun animal trouvé.");
                    }
                    break;



                case 13:
                    // Add a new visit
                    System.out.println("Saisissez la description de la visite: ");
                    String descriptionForVisit = sc.nextLine();
                    System.out.println("Saisissez la date de la visite (format YYYYMMDD): ");
                    int visitDate = sc.nextInt();
                    System.out.println("Saisissez l'ID du pet: ");
                    int petId = sc.nextInt();

                    // Fetch the pet by ID to get the actual Pets object (not just petId)
                    Pets petForVisit = petsDao.getPetById(petId);
                    if (petForVisit != null) {
                        Visite newVisite = new Visite(0, visitDate, descriptionForVisit, petForVisit, petId);
                        if (visiteDao.createVisite(newVisite)) {
                            System.out.println("La visite a bien été ajoutée.");
                        } else {
                            System.out.println("Erreur lors de l'ajout de la visite.");
                        }
                    } else {
                        System.out.println("Aucun animal trouvé avec cet ID.");
                    }
                    break;

                case 14:
                    // Update a visit
                    System.out.println("Saisissez l'ID de la visite à modifier: ");
                    int updateVisiteId = sc.nextInt();
                    sc.nextLine();  // Consume the newline
                    System.out.println("Saisissez la nouvelle description de la visite: ");
                    String newDescription = sc.nextLine();
                    System.out.println("Saisissez la nouvelle date de la visite (format YYYYMMDD): ");
                    int newVisitDate = sc.nextInt();
                    System.out.println("Saisissez l'ID du pet pour la visite: ");
                    int newPetId = sc.nextInt();

                    // Fetch the pet object associated with the newPetId
                    Pets updatedPetForVisit = petsDao.getPetById(newPetId);
                    if (updatedPetForVisit != null) {
                        Visite updatedVisite = new Visite(updateVisiteId, newVisitDate, newDescription, updatedPetForVisit, newPetId);
                        if (visiteDao.modifierVisite(updatedVisite, updateVisiteId)) {
                            System.out.println("La visite a été mise à jour.");
                        } else {
                            System.out.println("Erreur lors de la mise à jour de la visite.");
                        }
                    } else {
                        System.out.println("Aucun animal trouvé avec cet ID.");
                    }
                    break;

                case 15:
                    // Delete a visit
                    System.out.println("Saisissez l'ID de la visite à supprimer: ");
                    int deleteVisitId = sc.nextInt();
                    if (visiteDao.deleteVisite(deleteVisitId)) {
                        System.out.println("La visite a été supprimée.");
                    } else {
                        System.out.println("Erreur lors de la suppression de la visite.");
                    }
                    break;

                case 16:
                    // Search visit by ID
                    System.out.println("Saisissez l'ID de la visite à rechercher: ");
                    int searchVisitId = sc.nextInt();
                    Visite visitById = visiteDao.getVisiteById(searchVisitId);
                    if (visitById != null) {
                        System.out.println("Visite trouvée: " + visitById);
                    } else {
                        System.out.println("Aucune visite trouvée avec cet ID.");
                    }
                    break;

                case 17:
                    // List all visits
                    List<Visite> allVisites = visiteDao.getAllVisites();
                    if (!allVisites.isEmpty()) {
                        System.out.println("Liste des visites: ");
                        for (Visite visit : allVisites) {
                            System.out.println(visit);
                        }
                    } else {
                        System.out.println("Aucune visite trouvée.");
                    }
                    break;

                case 18:
                    // Add a new veterinarian
                    System.out.println("Saisissez le nom du vétérinaire: ");
                    String vetName = sc.nextLine();
                    System.out.println("Saisissez la spécialité du vétérinaire: ");
                    String vetSpecialty = sc.nextLine();

                    Viterinaire newViterinaire = new Viterinaire(0, vetName, vetSpecialty);  // Assuming 0 is a placeholder for ID
                    if (viterinaireDao.createViterinaire(newViterinaire)) {
                        System.out.println("Le vétérinaire " + vetName + " a bien été ajouté.");
                    } else {
                        System.out.println("Erreur lors de l'ajout du vétérinaire.");
                    }
                    break;

                case 19:
                    // Update a veterinarian
                    System.out.println("Saisissez l'ID du vétérinaire à modifier: ");
                    int updateVetId = sc.nextInt();
                    sc.nextLine();  // Consume the newline
                    System.out.println("Saisissez le nouveau nom du vétérinaire: ");
                    String newVetName = sc.nextLine();
                    System.out.println("Saisissez la nouvelle spécialité du vétérinaire: ");
                    String newVetSpecialty = sc.nextLine();

                    Viterinaire updatedViterinaire = new Viterinaire(updateVetId, newVetName, newVetSpecialty);
                    if (viterinaireDao.updateViterinaire(updateVetId, updatedViterinaire)) {
                        System.out.println("Le vétérinaire a été mis à jour.");
                    } else {
                        System.out.println("Erreur lors de la mise à jour du vétérinaire.");
                    }
                    break;

                case 20:
                    // Delete a veterinarian
                    System.out.println("Saisissez l'ID du vétérinaire à supprimer: ");
                    int deleteVetId = sc.nextInt();
                    if (viterinaireDao.deleteViterinaire(deleteVetId)) {
                        System.out.println("Le vétérinaire a été supprimé.");
                    } else {
                        System.out.println("Erreur lors de la suppression du vétérinaire.");
                    }
                    break;

                case 21:
                    // Search veterinarian by ID
                    System.out.println("Saisissez l'ID du vétérinaire à rechercher: ");
                    int searchVetId = sc.nextInt();
                    Viterinaire vetById = viterinaireDao.getViterinaireById(searchVetId);
                    if (vetById != null) {
                        System.out.println("Vétérinaire trouvé: " +vetById);
                    } else {
                        System.out.println("Aucun vétérinaire trouvé avec cet ID.");
                    }
                    break;
                case 22:
                    // Search veterinarians by name
                    System.out.println("Saisissez le nom du vétérinaire à rechercher: ");
                    String vetNameToSearch = sc.nextLine();
                    List<Viterinaire> vetsByName = viterinaireDao.findViterinairesByName(vetNameToSearch);
                    if (!vetsByName.isEmpty()) {
                        System.out.println("Vétérinaires trouvés: ");
                        for (Viterinaire vet : vetsByName) {
                            System.out.println(vet);
                        }
                    } else {
                        System.out.println("Aucun vétérinaire trouvé avec ce nom.");
                    }
                    break;
                case 23:
                    // List all veterinarians
                    List<Viterinaire> allVets = ViterinaireDao.getAllViterinaires();
                    if (!allVets.isEmpty()) {
                        System.out.println("Liste des vétérinaires: ");
                        for (Viterinaire vet : allVets) {
                            System.out.println(vet);
                        }
                    } else {
                        System.out.println("Aucun vétérinaire trouvé.");
                    }
                    break;

                case 24:
                    System.out.println("Au revoir!");
                    return;

                default:
                    System.out.println("Choix invalide! Veuillez essayer à nouveau.");
            }
            }
        }
    }

