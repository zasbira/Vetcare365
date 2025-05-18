@echo off
echo === VetCare 360 - Simple Dashboard with Dark Sidebar ===

echo Cleaning project directory...

REM Keep essential files
echo Keeping essential files:
echo - SimpleVetDashboard.java
echo - SimpleVetDashboard.class
echo - run-simple-dashboard.bat
echo - javafx-sdk-21.0.7 directory
echo - lib directory

REM Delete all other files and directories
for %%f in (*.java) do (
    if not "%%f"=="SimpleVetDashboard.java" (
        del /q /f "%%f"
    )
)

for %%f in (*.class) do (
    if not "%%f"=="SimpleVetDashboard.class" (
        del /q /f "%%f"
    )
)

for %%f in (*.bat) do (
    if not "%%f"=="run-simple-dashboard.bat" (
        del /q /f "%%f"
    )
)

for %%f in (*.md *.pub pom.xml) do (
    del /q /f "%%f"
)

for /d %%d in (*) do (
    if not "%%d"=="javafx-sdk-21.0.7" if not "%%d"=="lib" (
        rd /s /q "%%d"
    )
)

REM Compile and run the application
echo Compiling SimpleVetDashboard.java...
javac -cp ".;%CD%\javafx-sdk-21.0.7\lib\*" SimpleVetDashboard.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running Simple VetCare 360 Dashboard...
java --module-path "%CD%\javafx-sdk-21.0.7\lib" --add-modules javafx.controls,javafx.graphics,javafx.base -cp "." SimpleVetDashboard

pause
