@echo on
echo === VetCare 360 - Integrated Bootstrap Application ===

REM Create resources directories if they don't exist
mkdir resources\css 2>nul

REM Compile the integrated application
echo Compiling VetCare360Main.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Main.java

REM Run the application
echo Running VetCare 360 with Bootstrap UI...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp .;lib\mysql-connector-java-8.0.28.jar VetCare360Main

pause
