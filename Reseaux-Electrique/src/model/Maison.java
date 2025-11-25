package model;


public class Maison {
	private String nomM;
	private Consomation conso;
	
	public Maison(String n, Consomation c) {
		nomM = n;
		conso = c;
	}
	
	public void setConso(Consomation c ) {
		conso = c;
	}
	
	public String getNomM() {
		return nomM;
	}

	public Consomation getConso() {
		return conso;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Maison) {
			Maison t = (Maison) o;
			if(nomM.equals(t.getNomM())) {
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
		return "Maison : " + nomM + " Consomation : " + conso.getConso() + "kwh \n";
	}


}
