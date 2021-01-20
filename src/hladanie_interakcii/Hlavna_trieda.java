package hladanie_interakcii;

import java.util.Date;

import hladanie_interakcii.GRGT.GRGT;
import hladanie_interakcii.GRGT.Parsovanie_vety;
import hladanie_interakcii.co_occurence_jednoduche.Co_occurence_1;
import hladanie_interakcii.co_occurence_tuke2018.co_occurence_tuke2018;

public class Hlavna_trieda {
	
	public static void main(String[] args) {		
		int cas1 = time();
		
		//============ Prevadzka ============
		
		String korpus1 = "../korpus/originalne_texty/";
		String korpus2 = "../korpus/oznackovaneNazvy/";		
		String korpus3 = "../korpus/vysledky/";			
		
		/*
		GRGT grgt = new GRGT();		
		grgt.najdiInterakcie(korpus1, korpus2, korpus3);
		*/
		/*
		Co_occurence_1 co1 = new Co_occurence_1();
		co1.najdiInterakcie(korpus1, korpus2, korpus3);
		*/
		/*
		co_occurence_tuke2018 co1 = new co_occurence_tuke2018();
		co1.najdiInterakcie(korpus1, korpus2, korpus3);
		//co1.test();
		*/
		//============ Vytvorenie pravidiel/vzorov ==============
		/*
		// Hladanie vzorov	
		GRGT grgt = new GRGT();
		//grgt.spracujVzory("pomocne", 8530);
		grgt.spracujVzory("korpus_Hprd50", 0, 150); // (179 viet)
		grgt.spracujVzory("korpus_LLL", 0, 150); // (427 viet)		
		grgt.spracujVzory("korpus_IEPA", 0, 150);  // (777 viet)				
		grgt.spracujVzory("korpus_BioInfer", 0, 150); // (2500 viet)
		grgt.spracujVzory("korpus_AIMed", 0, 150); // (9700 viet)
		grgt.spracujVzory("korpus_PICAD", 0, 150); // (2500 viet)
		grgt.spracujVzory("korpus_BioCreAtIvE_ppi", 0, 150); // (2538 viet) // U - zistit preco nenaslo vzory
		*/
		/*
		Co_occurence_1 co1 = new Co_occurence_1();
		co1.najdiInterakcie();
		*/
		
		//=========================================
		/*
		Parsovanie_vety pv = new Parsovanie_vety();
		//pv.test();		
		pv.test1();
		//pv.testSkore();
		*/
		//=========================================
		
		int cas2 = time();		
		System.out.println("Program bezal " + (cas2 - cas1) + " sekund.");		
		
	}
	
	private static int time() {
		Date now = new Date();      
		Long longTime = new Long(now.getTime()/1000);
		return longTime.intValue();		
	}
	
}
