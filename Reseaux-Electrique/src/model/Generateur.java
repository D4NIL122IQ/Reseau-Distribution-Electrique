package model;


public class Generateur {
	private String nomG;
	private int capaciteMax;
	private int chargeActu;  // pour l'instant cette variable sera mise a jour a chaque ajout d'une connexion
	
	public Generateur(String n, int cm) {
		nomG = n;
		capaciteMax = cm;
		chargeActu = 0;
	}
	
	
	/*
	 * representre la somme des demandes electrique des maisons connecter a ce generateur
	 */
	public void setChargeActu(int consoMaison) {
		chargeActu += consoMaison; 
	}
	
	 /**
     * Retire la consommation d'une maison de la charge actuelle.
     * La charge ne peut pas devenir négative.
     *
     * @param consoMaison consommation de la maison à soustraire
     */
    public void soustraireCharge(int consoMaison) {
        chargeActu -= consoMaison;
        // S'assurer qu'on ne tombe pas en dessous de 0
        if (chargeActu < 0) {
            chargeActu = 0;
        }
    }

	public int getChargeActu() {
		return chargeActu;
	}
	
	/*
	 * Modifier la vapaciter max d'un generateur 
	 */
	public void setCapaMax(int capa) {
		capaciteMax = capa;
	}

	public String getNomG() {
		return nomG;
	}

	public int getCapaciteMax() {
		return capaciteMax;
	}
	
	 /**
     * Vérifie l'égalité entre deux générateurs.
     * Deux générateurs sont considérés égaux si leurs noms sont identiques.
     *
     * @param o objet à comparer
     * @return true si les générateurs ont le même nom, false sinon
     */
	@Override
	public boolean equals(Object o) {
		if(o instanceof Generateur) {
			Generateur t = (Generateur) o;
			if(t.getNomG().equals(nomG)) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	
	@Override
	public String toString() {
		return "Generateur : " + nomG +" Capacite max : " + capaciteMax + "kwh\n";
	}

	
}
