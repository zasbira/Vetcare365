@echo on
echo === Direct JavaFX Launcher ===

echo Compiling DirectLauncher.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls DirectLauncher.java

echo Running DirectLauncher...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls DirectLauncher

pause
