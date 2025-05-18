@echo on
echo === VetCare 360 - Visites CRUD Operations ===

REM Compile the CRUD application
echo Compiling VisiteCRUD.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VisiteCRUD.java

REM Run the application
echo Running VisiteCRUD...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VisiteCRUD

pause
