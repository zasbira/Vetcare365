@echo on
echo === VetCare 360 Fixed Launcher ===

REM Create necessary directories
mkdir out 2>nul
mkdir lib 2>nul

REM Download MySQL connector if needed
if not exist lib\mysql-connector-java-8.0.28.jar (
    echo MySQL connector not found. Downloading...
    powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar', 'lib\mysql-connector-java-8.0.28.jar')"
)

REM Clean the output directory
echo Cleaning output directory...
rmdir /s /q out 2>nul
mkdir out 2>nul

REM Compile all Java files preserving package structure
echo Compiling Java files with correct package structure...
javac -d out --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp lib\mysql-connector-java-8.0.28.jar src\controllers\*.java src\models\*.java src\*.java

REM Copy resources and views to their correct locations
echo Copying resources and views...
mkdir out\resources\css 2>nul
mkdir out\resources\images 2>nul
mkdir out\views 2>nul
copy resources\css\*.* out\resources\css\ /Y
copy resources\images\*.* out\resources\images\ /Y
copy src\views\*.* out\views\ /Y

REM Run the application with proper classpath
echo Running VetCare360 application...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp out;resources;lib\mysql-connector-java-8.0.28.jar src.MainApp

echo Application exited with code: %ERRORLEVEL%
pause
