@echo off

:: 1. Remplacer la valeur ci-dessous par le chemin vers le dossier 'lib' du SDK JavaFX sur votre machine Windows
set PATH_TO_FX="C:\Users\anish\Downloads\openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib"

echo 1. Nettoyage du dossier bin...
if exist bin (
    rd /s /q bin
)
mkdir bin

echo 2. Compilation des fichiers Java...
:: Note : On liste les fichiers manuellement ou via une commande car find n'existe pas nativement sous Windows
dir /s /b src\*.java > sources.txt
javac -d bin --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.graphics @sources.txt

:: Vérification de la compilation
if %errorlevel% neq 0 (
    echo Erreur lors de la compilation.
    del sources.txt
    pause
    exit /b %errorlevel%
)
del sources.txt

echo 3. Copie des assets...
xcopy src\assets bin\assets /E /I /Y

echo 4. Lancement de l'application...
java --module-path %PATH_TO_FX%;bin --add-modules javafx.controls,javafx.graphics -m reseau.electrique/vue.MainApp

pause