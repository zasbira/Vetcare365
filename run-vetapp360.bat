@echo on
echo === VetCare 360 - All-In-One Application (VetApp360) ===

REM Clean previous class files
echo Cleaning previous compiled files...
del VetApp360.class >nul 2>&1

REM Compile the application with absolute classpath
echo Compiling VetApp360.java...
javac -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar;%CD%\javafx-sdk-21.0.7\lib\*" VetApp360.java

REM Run the application with absolute paths
echo Running VetCare 360 All-In-One Application...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar" VetApp360

pause
