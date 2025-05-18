@echo on
echo === VetCare 360 - Unified Application ===

REM Compile the unified application
echo Compiling VetCare360Unified.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Unified.java

REM Run the application
echo Running VetCare 360...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Unified

pause
