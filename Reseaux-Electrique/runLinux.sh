#!/bin/bash

# -------------------------------------------------------------------
# @author Mounir Lamouchi
# Description: Script de compilation et lancement sous Linux
# -------------------------------------------------------------------

# 1. Remplacer la valeur ci-dessous par le chemin vers le dossier 'lib' du SDK JavaFX
PATH_TO_FX="/home/lecteur/Téléchargements/openjfx-21.0.9_linux-x64_bin-sdk/javafx-sdk-21.0.9/lib"

echo "1. Nettoyage du dossier bin..."
rm -rf bin
mkdir bin

echo "2. Compilation des fichiers Java..."
javac -d bin \
      --module-path $PATH_TO_FX \
      --add-modules javafx.controls,javafx.graphics \
      $(find src -name "*.java")

# Vérification si la compilation a réussi
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation."
    exit 1
fi

echo "3. Copie des assets..."
cp -r src/assets bin/

echo "4. Lancement de l'application..."
java --module-path "$PATH_TO_FX:bin" \
     --add-modules javafx.controls,javafx.graphics \
     -m reseau.electrique/vue.MainApp