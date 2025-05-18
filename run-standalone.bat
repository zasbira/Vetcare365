@echo on
echo === VetCare 360 Standalone Launcher ===

REM Compile the standalone app
echo Compiling StandaloneApp.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp lib\mysql-connector-java-8.0.28.jar StandaloneApp.java

REM Run the application
echo Running VetCare360 Standalone App...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp .;lib\mysql-connector-java-8.0.28.jar StandaloneApp

pause
