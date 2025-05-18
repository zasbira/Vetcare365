@echo on
echo === VetCare 360 Simple Application ===

REM Compile the application
echo Compiling VetCare360Simple.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Simple.java

REM Run the application
echo Running VetCare360 Simple...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Simple

pause
