@echo on
echo === VetCare 360 - Simple JavaFX Test ===

REM Clean previous class files
echo Cleaning previous files...
del SimpleVetApp.class >nul 2>&1

REM Compile the simple test application
echo Compiling SimpleVetApp.java...
javac -cp ".;%CD%\javafx-sdk-21.0.7\lib\*" SimpleVetApp.java

REM Run the application with explicit JavaFX modules
echo Running simple JavaFX test...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp "." SimpleVetApp

pause
