package model;


public enum Consomation {
	BASSE(10), NORMAL(20), FORTE(40);
	
	private final int conso;
	
	/**
     * Constructeur du niveau de consommation.
     *
     * @param conso valeur numérique associée
     */
	private Consomation(int conso) {
		this.conso = conso;
	}
	
	/**
     * Renvoie la valeur numérique associée au niveau de consommation.
     *
     * @return valeur de consommation
     */
	public int getConso() {
		return this.conso;
	}
}
