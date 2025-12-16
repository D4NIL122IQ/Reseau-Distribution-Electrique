PROJET PAA - RÉSEAU DE DISTRIBUTION D’ÉLECTRICITÉ
-------------------------------------------------

• Membres du groupe :
    - Guidjou Danil
    - Hammouche Anis

• Présentation de l'algorithme de résolution automatique :
    L'algorithme implémenté pour la résolution automatique est basé sur la métaheuristique du
    Recuit Simulé (Simulated Annealing).

    1. Principe de fonctionnement :
       L'objectif est de minimiser une fonction de coût globale combinant la surcharge des
       générateurs et la dispersion de la charge. L'algorithme procède comme suit :
       - Il part d'une configuration initiale (le réseau importé).
       - À chaque itération, il propose une modification aléatoire (déplacer une maison vers
         un autre générateur).
       - Si la nouvelle configuration réduit le coût, elle est acceptée immédiatement.
       - Si le coût augmente, elle peut tout de même être acceptée avec une certaine probabilité
         dépendante d'un paramètre appelé "Température". Cela permet à l'algorithme de sortir
         des minimums locaux pour chercher une solution optimale globale.
       - La température diminue progressivement au fil du temps (refroidissement), rendant
         l'algorithme de plus en plus sélectif jusqu'à la convergence vers une solution stable.

    2. Méthodologie de recherche et réalisation :
       Pour réaliser cet algorithme, mes recherches sur internet se sont articulées autour de
       trois axes principaux :
       - Compréhension théorique : Consultation de ressources académiques (Wikipédia, cours
         d'optimisation combinatoire) pour comprendre l'analogie avec la thermodynamique et
         la loi de Boltzmann.
       - Architecture logicielle : Recherche de patterns Java pour implémenter une boucle de
         refroidissement efficace tout en manipulant des objets complexes (ReseauElectrique).
       - Paramétrage : Étude de forums techniques (StackOverflow, GitHub) pour déterminer comment
         ajuster les paramètres critiques (température initiale, facteur de refroidissement)
         afin d'équilibrer le temps de calcul et la qualité de l'optimisation.

• Fonctionnalités implémentées :
    - Enregistrement du réseau dans un fichier : Exportation de la configuration optimisée.
    - Création d'un réseau depuis un fichier .txt : Un parseur robuste capable de lire et
      d'instancier un ReseauElectrique. Il intègre une gestion d'erreurs précise (numérotation
      des lignes, erreurs de syntaxe, respect de l'ordre des instances).
    - Algorithme d'optimisation : Implémentation du recuit simulé pour équilibrer la charge
      et supprimer les surcharges.
    - Interface graphique : Réalisée avec JavaFX, comprenant deux vues :
        1. Vue Menu : choisir si on veut crée un reseau de distribution d'éléctricité manuellement 
          ou importer un fichier directement
        2. Vue d'import : Zone de Drag & Drop et sélecteur de fichiers pour l'importation.
        3. Vue de visualisation : Représentation graphique dynamique du réseau (nœuds et liens),
           affichage du coût en temps réel, réglage de la sévérité et contrôle de l'optimisation.
        4. Vue création : pour crée une instance de ReseauElectrique et pouvoir la manipuler graphiquement=

• Tests :
    La fiabilité du projet a été assurée par une batterie de tests rigoureux :
        1. Tests Unitaires (JUnit 5) :
           - Validation du modèle (Generateur, Maison, ReseauElectrique) pour garantir que les
             mises à jour de charge sont correctes lors des ajouts/suppressions.
           - Test du calculateur de coût pour vérifier le respect de la formule mathématique.
           - Test du Parser pour s'assurer que les exceptions (FiniPointException, OrdreInstanceException)
             sont levées au bon moment.
        2. Tests Fonctionnels :
           - Vérification de la convergence de l'algorithme d'optimisation sur des fichiers
             de test complexes.
           - Test d'intégrité de l'interface graphique (Drag & Drop, réactivité des boutons).
        3. Tests de Robustesse :
           - Simulation de fichiers corrompus ou mal formatés pour vérifier la stabilité du programme.

• Exécution du programme :

    1. Sur une machine macOS :
        - Tout d’abord, il faut télécharger la version 21 du JDK JavaFX (x64 pour les machines Intel ou aarch64 pour les machines Apple Silicon).
        - Copier le chemin absolu vers le dossier bin du JDK et le placer dans la variable PATH_TO_FX du fichier runMacOS.sh.
        - Ouvrir un terminal et se situer à la racine du projet.
        - Lancer les commandes suivantes :
            $ chmod +x runMacOS.sh   # pour ajouter la permission d’exécuter le script
            $ ./runMacOS.sh          # exécuter le script

    2. Sur une machine windows :
        - Tout d’abord, il faut télécharger la version 21 du SDK JavaFX (version Windows x64).
                - Copier le chemin absolu vers le dossier bin du SDK et le placer dans la variable PATH_TO_FX du fichier runWindows.bat.
                - Ouvrir une invite de commande (CMD) ou un PowerShell et se situer à la racine du projet.
                - Lancer la commande suivante :
                    > .\runWindows.bat       # exécuter le script de lancement
-------------------------------------------------
© 2025 - Projet PAA - Anis Hammouche & Danil Guidjou