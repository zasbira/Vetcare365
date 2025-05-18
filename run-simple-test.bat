@echo on
echo === Simple JavaFX Test ===

echo Compiling SimpleFXTest.java...
javac --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls SimpleFXTest.java

echo Running SimpleFXTest...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls SimpleFXTest

pause
