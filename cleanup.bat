@echo off
echo Cleaning up project directory...

REM Keep these files:
REM SimpleVetDashboard.java
REM SimpleVetDashboard.class
REM run-simple-dashboard.bat
REM javafx-sdk-21.0.7 (directory)
REM lib (directory)

REM Delete all other .java and .class files
del /q /f "*.java"
del /q /f "*.class"

REM Create exceptions for files we want to keep
for %%f in ("SimpleVetDashboard.java", "SimpleVetDashboard.class") do (
    if exist "%%f" (
        echo Keeping %%f
    )
)

REM Delete all batch files except run-simple-dashboard.bat
for %%f in (*.bat) do (
    if not "%%f"=="run-simple-dashboard.bat" (
        del /q /f "%%f"
    )
)

REM Delete other directories except those we need
for /d %%d in (*) do (
    if not "%%d"=="javafx-sdk-21.0.7" if not "%%d"=="lib" (
        rd /s /q "%%d"
    )
)

echo Cleanup complete!
pause
