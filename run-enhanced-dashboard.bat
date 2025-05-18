@echo on
echo === VetCare 360 - Enhanced Dashboard ===

REM Clean previous class files
echo Cleaning previous compiled files...
del VetCare360Enhanced.class >nul 2>&1

REM Compile the application with absolute classpath
echo Compiling enhanced application...
javac -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar;%CD%\javafx-sdk-21.0.7\lib\*" VetCare360Enhanced.java

REM Run the application with absolute paths
echo Running enhanced VetCare 360...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar" VetCare360Enhanced

pause
