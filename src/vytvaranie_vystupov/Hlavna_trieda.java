package vytvaranie_vystupov;

import netriedene.MyData;

public class Hlavna_trieda {

	public static void main(String[] args) {
		//========= Export do XML ==========
		/*
		String vstupAdr = "../korpus/vysledky"; 
		String vystupAdr = "../korpus/samostatne_xml";
				
		Samostatne_xml sx = new Samostatne_xml();
		sx.vytvor(vstupAdr, vystupAdr);
		*/
		
		//========= Priprava na web ===========
		/*
		Pripravenie_na_web pnw = new Pripravenie_na_web();
		pnw.start();
		*/
		//========= Korpus plnych textov s anotaciami ============
		
		String vstupAdr = "../korpus/vysledky"; 
		String vystupAdr = "../korpus_textov/";
						 		 
		Korpus_textov kt = new Korpus_textov();
		kt.vytvor(vstupAdr, MyData.ineFormatyClankov, vystupAdr);
		
	}

}















































