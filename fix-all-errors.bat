@echo off
echo === VetCare 360 - Fix All Errors ===
echo.

echo Step 1: Fixing database structure...
mysql -u root -e "DROP DATABASE IF EXISTS db; CREATE DATABASE db;"
mysql -u root -e "USE db; CREATE TABLE veterinaires (id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(50) NOT NULL, prenom VARCHAR(50) NOT NULL, specialite VARCHAR(100), email VARCHAR(100), telephone VARCHAR(20));"
mysql -u root -e "USE db; CREATE TABLE proprietaires (id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(50) NOT NULL, prenom VARCHAR(50) NOT NULL, adresse VARCHAR(200), telephone VARCHAR(20));"
mysql -u root -e "USE db; CREATE TABLE animaux (id INT AUTO_INCREMENT PRIMARY KEY, nom VARCHAR(50) NOT NULL, espece VARCHAR(50) NOT NULL, sexe VARCHAR(20), age INT, proprietaire_id INT, FOREIGN KEY (proprietaire_id) REFERENCES proprietaires(id) ON DELETE CASCADE);"
mysql -u root -e "USE db; CREATE TABLE visites (id INT AUTO_INCREMENT PRIMARY KEY, date DATE NOT NULL, motif VARCHAR(100) NOT NULL, diagnostic TEXT, traitement TEXT, animal_id INT, veterinaire_id INT, FOREIGN KEY (animal_id) REFERENCES animaux(id) ON DELETE CASCADE, FOREIGN KEY (veterinaire_id) REFERENCES veterinaires(id) ON DELETE SET NULL);"

echo Step 2: Adding sample data...
mysql -u root -e "USE db; INSERT INTO veterinaires (nom, prenom, specialite, email, telephone) VALUES ('Dubois', 'Marie', 'Médecine générale', 'm.dubois@vetcare360.fr', '01 23 45 67 89'), ('Martin', 'Pierre', 'Chirurgie', 'p.martin@vetcare360.fr', '01 34 56 78 90'), ('Bernard', 'Sophie', 'Dermatologie', 's.bernard@vetcare360.fr', '01 45 67 89 01');"
mysql -u root -e "USE db; INSERT INTO proprietaires (nom, prenom, adresse, telephone) VALUES ('Martin', 'Jean', '15 Rue des Fleurs, 75001 Paris', '01 23 45 67 89'), ('Dubois', 'Marie', '27 Avenue Victor Hugo, 69002 Lyon', '04 56 78 90 12'), ('Petit', 'Sophie', '8 Rue du Commerce, 44000 Nantes', '02 34 56 78 90');"
mysql -u root -e "USE db; INSERT INTO animaux (nom, espece, sexe, age, proprietaire_id) VALUES ('Rex', 'Chien', 'Mâle', 5, 1), ('Minette', 'Chat', 'Femelle', 3, 1), ('Nemo', 'Poisson', 'Mâle', 1, 2);"
mysql -u root -e "USE db; INSERT INTO visites (date, motif, diagnostic, traitement, animal_id, veterinaire_id) VALUES (CURDATE(), 'Vaccination annuelle', 'Animal en bonne santé', 'Vaccin polyvalent', 1, 1), (CURDATE(), 'Consultation de suivi', 'Légère infection', 'Antibiotiques 7 jours', 2, 1);"

echo Step 3: Recompiling all applications...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCRUD.java
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar PropCRUD.java
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar AnimalCRUD.java

echo.
echo === All errors fixed! ===
echo You can now run any of these apps:
echo   run-vet-crud.bat    - Veterinarian management
echo   run-prop-crud.bat   - Owner management 
echo   run-animal-crud.bat - Pet management
echo.

pause
