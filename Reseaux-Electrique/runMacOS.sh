
#Remplacer la valeur du PATH_TO_FX par le chemin vers le dossier 'lib' du SDK JavaFX
PATH_TO_FX="/Users/danilguidjou/Documents/L3PC/S5/PAA/ProjetRXElct/Reseaux-Electrique/javafx-sdk-17.0.17_aarch/lib"

rm -rf bin
mkdir bin

# compilation 
javac -d bin \
      --module-path $PATH_TO_FX \
      --add-modules javafx.controls,javafx.graphics \
      $(find src -name "*.java")

# Vérification si la compilation a réussi
if [ $? -ne 0 ]; then
    exit 1
fi

# copie des assets
cp -r src/assets bin/

# éxecution
echo "4. Lancement de l'application..."
java --module-path $PATH_TO_FX:bin \
     --add-modules javafx.controls,javafx.graphics \
     -m reseau.electrique/vue.MainApp