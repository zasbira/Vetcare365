@echo on
echo === VetCare 360 Complete Database Demo ===

REM Compile the comprehensive demo app
echo Compiling VetCare360Demo.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls -cp lib\mysql-connector-java-8.0.28.jar VetCare360Demo.java

REM Run the application
echo Running VetCare360 Complete Demo...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Demo

pause
