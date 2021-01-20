package hladanie_interakcii.co_occurence_jednoduche;

import java.util.HashSet;
import java.util.Set;

import hladanie_interakcii.GRGT.Vzor;
import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

public class Co_occurence_1 {

public void najdiInterakcie(String povodneTexty, String oznackovaneTexty, String vystup) {					
	Uprava_suborov.vymazObsahPriecinka(vystup); // Vymaze obsah vysledkov po kazdom novom spusteni

	// Prehlada subory a najde interakcie
	Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(oznackovaneTexty);		
	//System.out.println(subory);		
	
	int pocitadlo = 0;
	for(String sub : subory) {	
		// Pocitadlo kvoli vypisu
		pocitadlo++;
		System.out.println("Hlada interakcie v " + pocitadlo + "/" + subory.size());
		//if(pocitadlo < 924) continue; // Pozn. naostro zakomentovat
		
		String obsah = Uprava_suborov.vratObsahSuboru(oznackovaneTexty+"/"+sub);
		boolean naslo = false; // Ked nie su ziadne interakcie, tak ani nekopirujeme povodny subor
		String[] vety = obsah.split("\n");			
		Set<String> anotovane = new HashSet<>();
		System.out.println("Pocet viet v dokumente " + sub + ": " + vety.length);
		
		for(String veta : vety) {
			// Ak je va vstupe prazdny riadok
			veta = veta.trim();
			if(veta.length() == 0) continue;
			
			//System.out.println("Hlada interakcie vo vete: " + veta);					
			anotovane.addAll(vratAnotovanePPI(veta));
		}	
		
		if(anotovane.size() > 0) {
			//Zapise anotacie na vystup		
				// Zapise anotacie
			String nazovSub = sub.replace(".txt", "") + "-anotacie.txt";
			Uprava_suborov.vytvorSubor(vystup+"/"+nazovSub);		
			for(String veta : anotovane) {
				Uprava_suborov.pridajNaKoniecDoSuboru(vystup+"/"+nazovSub, veta);				
			}
				// Zapise originalny text
			Uprava_suborov.vytvorSubor(vystup+"/"+sub);
			String obsah1 = Uprava_suborov.vratObsahSuboru(povodneTexty+"/"+sub);								
			Uprava_suborov.pridajNaKoniecDoSuboru(vystup+"/"+sub, obsah1);
		}								
	}
}

private Set<String> vratAnotovanePPI(String veta) {
	int maxPocetMoznosti = 200;
	Set<String> anotovane = new HashSet<>();
	
	Set<String> proteiny = Spracovanie_textu.vratObsahZnaciek(veta, "<protein>", "</protein>");
	Set<String> interakcie = Spracovanie_textu.vratObsahZnaciek(veta, "<interakcia>", "</interakcia>");
	
	// Podmienky
		// Ak je elementov prilis, nerobi to kvoli vysokym narokom (casovym, pamatovym)
	if(proteiny.size() * proteiny.size() * interakcie.size() > maxPocetMoznosti) return anotovane;
				
		// Podmienka kedy PPi vo vete nie je
	if(proteiny.size() < 2 || interakcie.size() < 1) return anotovane;
		
	// Vyberie z vety znacky
	String bezZnaciek = veta;
	bezZnaciek = bezZnaciek.replace("<protein>", "");
	bezZnaciek = bezZnaciek.replace("</protein>", "");
	bezZnaciek = bezZnaciek.replace("<interakcia>", "");
	bezZnaciek = bezZnaciek.replace("</interakcia>", "");
	
	int i=0; int j=0;
	for(String protein1 : proteiny) {
		i++;
		for(String protein2 : proteiny) {
			j++;
			if(j == i) continue; // same so sebou sa neanotuje (duplikovane sa zrusia ulozenim do mnoziny)
			for(String interakcia : interakcie) {
				String anotovanaVeta = bezZnaciek;
				anotovanaVeta = anotovanaVeta.replace(protein1, "<protein>"+protein1+"</protein>");
				anotovanaVeta = anotovanaVeta.replace(protein2, "<protein>"+protein2+"</protein>");
				anotovanaVeta = anotovanaVeta.replace(interakcia, "<interakcia>"+interakcia+"</interakcia>");
				anotovane.add(anotovanaVeta);
			}
		}
	}
	
	return anotovane;	
}


	// Pozn. povodna nepouzivana funkcia
	public void najdiInterakcieStare() {
		// Nacita zoznam interakcii
		Set<String> interakcie = new HashSet<>();
		String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_interakcii/12859_2009_2963_MOESM1_ESM.txt");
		String[] polozky = protStr.split("\n");
		for(String s : polozky) {
			s = s.trim();
			if(s.length() == 0) continue;
			interakcie.add(s);
		}	
		
		// Prehlada subory a najde interakcie
		String korpus2 = "../korpus/oznackovaneNazvy/";
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(korpus2);		
		//System.out.println(subory);		
		
		int pocitadlo = 0; 
		for(String sub : subory) {
			boolean naslo = false;
			
			String obsah = Uprava_suborov.vratObsahSuboru(korpus2+"/"+sub);
			//System.out.println(obsah);
			String anotacie = "";
			
			String[] vety = obsah.split("\n");
			for(String veta : vety) {
				String[] slova = veta.split(" ");
				for(String slovo : slova) {
					slovo = slovo.trim();
					if(interakcie.contains(slovo)) {
						// U - vytvorit presnejsie riesenie
						// U - vyriesit aj situaciu ak existuje viac interakcii
						
						int protA = veta.indexOf("@protein_A_@");
						int protB = veta.lastIndexOf("@protein_B_@"); // musi byt posledny lebo znacka protB je vzdy pred protA pri danom prot.
						int interakciaIndex = veta.indexOf(slovo);
						
						if(protA < interakciaIndex && interakciaIndex < protB) {
							naslo = true;
							int protA2 = veta.lastIndexOf("@protein_A_@"); // najde posledny vyskyt
							int protB1 = veta.indexOf("@protein_B_@"); // najde prvy vyskyt
							
							String proteinA = veta.substring(protB1, protA);
							String proteinB = veta.substring(protB, protA2);
							
							// Dame prec znacky
							proteinA = proteinA.replace("@protein_B_@", "");
							proteinA = proteinA.replace("@protein_A_@", "");
							
							proteinB = proteinB.replace("@protein_B_@", "");							
							proteinB = proteinB.replace("@protein_A_@", "");
																			
							proteinA = proteinA.trim();
							proteinB = proteinB.trim();
							
							System.out.println(proteinA + " - " + slovo + " - " + proteinB);
												
							// Ulozi anotacie
								// Pekne zobrazi
							veta = veta.replace("@protein_B_@", " |PROTEIN -->| ");							
							veta = veta.replace("@protein_A_@", "");							
							
							veta = veta.replace(slovo, " |INTERACTION -->| " + slovo + "");														
							
							anotacie = anotacie
									+ "--------------------------\n"
									+ "Interakcia: \n"
									+ proteinA + " - " + slovo + " - " + proteinB + "\n"
									+ "Veta: \n"
									+ veta									
									+ "\n";																					
						}						
					}														
				}				
			}
			
			// Zapise udaje do vysledneho korpusu
			if(naslo == true) {
				//if(true) continue;
				
				String korpus1 = "../korpus/originalne_texty/";
				String korpus3 = "../korpus/vysledky/";
				
				// Zapise poznamku				
				String nazovSub = sub.replace(".txt", "") + "-anotacie.txt";
				Uprava_suborov.vytvorSubor(korpus3+"/"+nazovSub);
				Uprava_suborov.pridajNaKoniecDoSuboru(korpus3+"/"+nazovSub, anotacie);
				
				// Prekopiruje povodny subor do rovnakeho adresara ako mame poznamku
				String obsah1 = Uprava_suborov.vratObsahSuboru(korpus1+"/"+sub);				
				Uprava_suborov.vytvorSubor(korpus3+"/"+sub);
				Uprava_suborov.pridajNaKoniecDoSuboru(korpus3+"/"+sub, obsah1);								
			}						
		}		
	}
	
}



















































