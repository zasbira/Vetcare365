@echo on
echo === VetCare 360 - CRUD Operations ===

REM Compile the CRUD application
echo Compiling VetCRUD.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCRUD.java

REM Run the application
echo Running VetCRUD...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCRUD

pause
