package testovanie_metod;

import netriedene.MyData;
import predspracovanie_textov.Parsovanie_povodnych_korpusov;
import predspracovanie_textov.Predspracovanie_udajov_na_testovanie;

public class Hlavna_trieda {

	public static void main(String[] args) {
		// Co treba urobit ppredtym nez sa da spustit nasledovne testovanie
		//1. Predspracovat korpus (modul predspracovania extov)
		//2. Najst interakcie (modul hladania interakcii). Uz tu mus byt nastavena max dlzka vety		
		
		//---------- Predspracuje testovacie korpusy --------
		// Je to iba jednorazovy proces, netreba spustat pri kazdom testovaní
		/*
		Predspracovanie_udajov_na_testovanie punt = new Predspracovanie_udajov_na_testovanie();
		//punt.predspracujHuBerlin(); // Pozn, nie je ti standardna sada		
		punt.predspracujKorpusy("AIMed", "korpus_AIMed", -1);
		punt.predspracujKorpusy("BioInfer", "korpus_BioInfer", -1);
		punt.predspracujKorpusy("IEPA", "korpus_IEPA", -1);		
		punt.predspracujKorpusy("LLL", "korpus_LLL", -1); // Pozn. Naostro 150
		punt.predspracujKorpusy("Hprd50", "korpus_Hprd50", -1);
		punt.predspracujKorpusy("PICAD", "korpus_PICAD", -1);
		*/
		//---------- Samotne otestovanie metody -----------------
		// Otestuje metodu na vzorovuch korpusoch
		/*
		//String metoda = "grgt"; //co1, co2018
		//String metoda = "co1";
		String metoda = "co2018";
		MyData.maxDlzkaVety = 150;
		Otestovanie_metody om = new Otestovanie_metody();		
		//om.otestujMetodu("korpus_LLL", metoda); // (427 viet)
		//om.otestujMetodu("korpus_IEPA", metoda);  
		//om.otestujMetodu("korpus_PICAD", metoda);  
		//om.otestujMetodu("korpus_AIMed", metoda); 
		//om.otestujMetodu("korpus_BioInfer", metoda);
		om.otestujMetodu("korpus_Hprd50", metoda); 
		
		//om.otestujMetodu("pomocny");
								
		//om.otestujGRGT("korpus_hu_berlin_maly");		
		//om.otestujGRGT("korpus_hu_berlin");
		*/			 		
		//----------- Ine sposoby testovania ---------------
		/*
		Otestovanie_metody om = new Otestovanie_metody();
		//om.otestujPodlaVzorov();
		om.otestujBezNazvovInterakcii();
		*/
		// Priprava na zistenie potencialu
		
		
		
		
	}
	
	
	
}





















































