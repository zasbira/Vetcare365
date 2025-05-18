@echo on
echo === VetCare 360 - All-In-One Application ===

REM Clean previous class files to avoid issues
echo Cleaning previous compiled files...
del VetCare360Final.class >nul 2>&1

REM Ensure directories exist
echo Creating necessary directories...
mkdir resources 2>nul

REM Compile the application with absolute classpath
echo Compiling the All-In-One VetCare360 application...
javac -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar;%CD%\javafx-sdk-21.0.7\lib\*" VetCare360Final.java

REM Run the application with absolute paths
echo Running VetCare 360 All-In-One...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar" VetCare360Final

pause
