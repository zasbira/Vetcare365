@echo off
title Installing VetCare Application Requirements
echo Installing all required components...
echo ----------------------------------------

echo Creating setup directory...
if not exist "setup" mkdir setup

echo Downloading 7-Zip (required for extraction)...
bitsadmin /transfer "7ZipDownload" /priority normal "https://www.7-zip.org/a/7z2400.exe" "setup\7z2400.exe"

if exist "setup\7z2400.exe" (
    echo Installing 7-Zip...
    start /wait "" "setup\7z2400.exe" /S
) else (
    echo ERROR: Failed to download 7-Zip
    pause
    exit /b
)

echo Downloading Java JDK 24...
bitsadmin /transfer "JavaDownload" /priority normal "https://download.oracle.com/java/24/archive/jdk-24.0.1_windows-x64_bin.exe" "setup\jdk-24.0.1_windows-x64_bin.exe"

if exist "setup\jdk-24.0.1_windows-x64_bin.exe" (
    echo Installing Java JDK 24...
    start /wait "" "setup\jdk-24.0.1_windows-x64_bin.exe" /s
) else (
    echo ERROR: Failed to download Java JDK
    pause
    exit /b
)

echo Downloading Maven...
bitsadmin /transfer "MavenDownload" /priority normal "https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.zip" "setup\apache-maven-3.9.5-bin.zip"

if exist "setup\apache-maven-3.9.5-bin.zip" (
    echo Extracting Maven...
    "setup\7z.exe" x "setup\apache-maven-3.9.5-bin.zip" -o"setup\maven" -y
) else (
    echo ERROR: Failed to download Maven
    pause
    exit /b
)

echo Downloading JavaFX SDK...
bitsadmin /transfer "JavaFXDownload" /priority normal "https://github.com/openjdk/jfx/releases/download/jfx-17.0.10/javafx-sdk-17.0.10.zip" "setup\javafx-sdk-17.0.10.zip"

if exist "setup\javafx-sdk-17.0.10.zip" (
    echo Extracting JavaFX SDK...
    "setup\7z.exe" x "setup\javafx-sdk-17.0.10.zip" -o"setup\javafx" -y
) else (
    echo ERROR: Failed to download JavaFX
    pause
    exit /b
)

echo Setting up environment variables...
setx JAVA_HOME "%CD%\setup\jdk-24.0.1_windows-x64_bin"
setx MAVEN_HOME "%CD%\setup\maven\apache-maven-3.9.5"
setx PATH "%PATH%;%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%CD%\setup\javafx\lib"

echo ----------------------------------------
echo Installation completed!
echo You can now run the application using the setup_and_run.bat file.
echo ----------------------------------------
pause
