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
        1. Vue d'accueil : Zone de Drag & Drop et sélecteur de fichiers pour l'importation.
        2. Vue de visualisation : Représentation graphique dynamique du réseau (nœuds et liens),
           affichage du coût en temps réel, réglage de la sévérité et contrôle de l'optimisation.

-------------------------------------------------
© 2025 - Projet PAA - Anis Hammouche & Danil Guidjou