@echo on
echo === VetCare 360 Dashboard ===

REM Compile the dashboard app
echo Compiling VetCare360Dashboard.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp lib\mysql-connector-java-8.0.28.jar VetCare360Dashboard.java

REM Run the application
echo Running VetCare360 Dashboard...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Dashboard

pause
