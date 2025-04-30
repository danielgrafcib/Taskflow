@echo off
setlocal enabledelayedexpansion
echo Compilation du projet TaskFlow...

:: Définition des variables
set "PROJECT_ROOT=%~dp0"
set "SRC_DIR=%PROJECT_ROOT%src"
set "BUILD_DIR=%PROJECT_ROOT%build\classes"
set "DIST_DIR=%PROJECT_ROOT%dist"
set "LIB_DIR=%PROJECT_ROOT%lib"

:: Tuer les processus Java qui pourraient bloquer le fichier JAR
echo Fermeture des instances TaskFlow en cours d'exécution...
taskkill /F /IM "java.exe" /FI "WINDOWTITLE eq TaskFlow*" 2>nul
timeout /t 2 /nobreak >nul

:: Trouver Java dans le registre
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK" /v "CurrentVersion" 2^>nul') do set "JAVA_VERSION=%%~b"
for /f "tokens=2*" %%a in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK\%JAVA_VERSION%" /v "JavaHome" 2^>nul') do set "JAVA_PATH=%%~b"

if not defined JAVA_PATH (
    echo Java n'est pas trouvé. Veuillez installer le JDK.
    exit /b 1
)

:: Création des répertoires nécessaires
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"
if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
if not exist "%DIST_DIR%\lib" mkdir "%DIST_DIR%\lib"

:: Nettoyage des anciens fichiers
echo Nettoyage des fichiers précédents...
del /F /Q "%DIST_DIR%\TaskFlow.jar" 2>nul
rd /S /Q "%BUILD_DIR%" 2>nul
mkdir "%BUILD_DIR%"
rd /S /Q "%DIST_DIR%\lib" 2>nul
mkdir "%DIST_DIR%\lib"

:: Création d'une liste de fichiers sources
set "SOURCES="
for /r "%SRC_DIR%" %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""

:: Compilation des sources Java
echo Compilation des fichiers sources...
"%JAVA_PATH%\bin\javac" -encoding UTF-8 -d "%BUILD_DIR%" -cp "%LIB_DIR%\*" %SOURCES%

if errorlevel 1 (
    echo Erreur lors de la compilation !
    exit /b 1
)

:: Copie des bibliothèques
echo Copie des bibliothèques...
xcopy /Y "%LIB_DIR%\*.jar" "%DIST_DIR%\lib\"

:: Vérification et copie des ressources
if exist "%SRC_DIR%\taskflow\ui\icons" (
    echo Copie des icônes...
    xcopy /E /I /Y "%SRC_DIR%\taskflow\ui\icons" "%BUILD_DIR%\taskflow\ui\icons"
    xcopy /E /I /Y "%SRC_DIR%\taskflow\ui\icons" "%DIST_DIR%\icons"
)

:: Création du fichier MANIFEST.MF temporaire
echo Manifest-Version: 1.0> "%BUILD_DIR%\MANIFEST.MF"
echo Main-Class: taskflow.TaskFlowApp>> "%BUILD_DIR%\MANIFEST.MF"
echo Class-Path: lib/mysql-connector-j-9.1.0.jar lib/flatlaf-3.4.1.jar lib/flatlaf-extras-3.4.1.jar lib/commons-dbutils-1.8.1.jar lib/jcalendar-1.3.2.jar lib/miglayout-core.jar lib/miglayout-swing.jar lib/swing-datetime-picker-1.2.0.jar lib/swing-glasspane-popup-1.5.0.jar lib/swing-toast-notifications-1.0.2.jar lib/itextpdf-5.5.13.jar lib/jfreechart-1.5.3.jar lib/jsvg-1.4.0.jar lib/flatlaf-fonts-roboto-2.137.jar>> "%BUILD_DIR%\MANIFEST.MF"

:: Création du JAR
echo Création du fichier JAR...
cd "%BUILD_DIR%"
"%JAVA_PATH%\bin\jar" cfm "%DIST_DIR%\TaskFlow.jar" MANIFEST.MF .
cd "%PROJECT_ROOT%"

if errorlevel 1 (
    echo Erreur lors de la création du JAR !
    exit /b 1
)

echo.
echo Build terminé avec succès !
echo L'application se trouve dans le dossier 'dist'.
echo Pour lancer l'application :
echo 1. Assurez-vous que MySQL Server est en cours d'exécution
echo 2. Double-cliquez sur dist\TaskFlow.jar
echo.
pause

