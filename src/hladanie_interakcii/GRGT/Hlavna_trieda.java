package hladanie_interakcii.GRGT;

public class Hlavna_trieda {

	public static void main(String[] args) {
		GRGT grgt = new GRGT();
		grgt.test();
		
		
		Ziskanie_skupin_interakcii zsi = new Ziskanie_skupin_interakcii ();
		zsi.ziskajSkupinyInterakcii();
		//zsi.otestujVelkuMnozinu();
		
	}

}
