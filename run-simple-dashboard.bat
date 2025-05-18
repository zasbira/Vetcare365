@echo on
echo === VetCare 360 - Simple Dashboard with Dark Sidebar ===

REM Clean previous class files
echo Cleaning previous compiled files...
del SimpleVetDashboard.class >nul 2>&1

REM Compile the application with absolute classpath
echo Compiling SimpleVetDashboard.java...
javac -cp ".;%CD%\javafx-sdk-21.0.7\lib\*" SimpleVetDashboard.java

REM Run the application with absolute paths
echo Running Simple VetCare 360 Dashboard...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp "." SimpleVetDashboard

pause
