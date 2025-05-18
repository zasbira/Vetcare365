@echo on
echo === VetCare 360 Standalone Vet App ===

REM Compile the standalone app
echo Compiling VetApp.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls -cp lib\mysql-connector-java-8.0.28.jar VetApp.java

REM Run the application
echo Running VetCare360 Standalone App...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls -cp .;lib\mysql-connector-java-8.0.28.jar VetApp

pause
