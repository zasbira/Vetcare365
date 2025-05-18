@echo on
echo === VetCare 360 - Proprietaires CRUD Operations ===

REM Compile the CRUD application
echo Compiling PropCRUD.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar PropCRUD.java

REM Run the application
echo Running PropCRUD...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar PropCRUD

pause
