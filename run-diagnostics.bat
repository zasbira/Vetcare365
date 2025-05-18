@echo on
echo === VetCare 360 - Diagnostic Tool ===

REM Compile the diagnostic application
echo Compiling TroubleshootApp.java...
javac -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar;%CD%\javafx-sdk-21.0.7\lib\*" TroubleshootApp.java

REM Run the diagnostic application
echo Running diagnostic tool...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp ".;%CD%\lib\mysql-connector-java-8.0.28.jar" TroubleshootApp

pause
