@echo on
echo === VetCare 360 Application Launcher ===

REM Important: The approach we're using here is to treat the project root as the classpath root

REM Create output directory
rmdir /s /q out 2>nul
mkdir out 2>nul

REM Compile the main application with correct sourcepath and classpath
echo Compiling Java files...

REM First, let's update the package declaration in MainApp.java
echo Removing package declaration from MainApp.java...
powershell -Command "(Get-Content src\MainApp.java) | Where-Object {$_ -notmatch '^package'} | Set-Content src\MainApp.java"

REM Compile all files with the current directory as source root
javac -d out --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml src\MainApp.java src\controllers\*.java src\models\*.java src\*.java
echo Compilation completed with exit code: %ERRORLEVEL%

REM Copy resources and views
echo Copying resources and views...
mkdir out\resources\css 2>nul
mkdir out\resources\images 2>nul
mkdir out\views 2>nul
copy resources\css\*.* out\resources\css\ /Y
copy resources\images\*.* out\resources\images\ /Y
copy src\views\*.* out\views\ /Y

REM Download MySQL connector if needed
if not exist lib\mysql-connector-java-8.0.28.jar (
    echo MySQL connector not found. Downloading...
    mkdir lib 2>nul
    powershell -Command "(New-Object System.Net.WebClient).DownloadFile('https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar', 'lib\mysql-connector-java-8.0.28.jar')"
)

REM Run the application
echo Running application...
java --module-path "javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.fxml -cp out;resources;lib\mysql-connector-java-8.0.28.jar MainApp

echo Application exited with code: %ERRORLEVEL%
pause
