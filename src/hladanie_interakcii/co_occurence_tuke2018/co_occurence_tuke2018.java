package hladanie_interakcii.co_occurence_tuke2018;

import java.util.HashSet;
import java.util.Set;

import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

public class co_occurence_tuke2018 {
	
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
				Set<String> anotovanePom = vratAnotovanePPI(veta);
				if(anotovanePom.size() > 0) anotovane.addAll(anotovanePom);
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
			} else {
				System.out.println("Nenaslo ziatnu PPI.");
			}							
		}
	}

	public void test() {
		String veta = "1k), suggesting that silencing <protein>TRB3</protein> <interakcia>enhances</interakcia> <protein>insulin</protein> sensitivity that coincides with the observation in ref.";
		Set<String> navrat = vratAnotovanePPI(veta);
		
		for(String veta1 : navrat) {
			System.out.println(veta1);
		}				
	}
	
	private Set<String> vratAnotovanePPI(String veta) {
		int maxPocetMoznosti = 200;
		Set<String> anotovane = new HashSet<>();
		
		Set<String> proteiny = Spracovanie_textu.vratObsahZnaciek(veta, "<protein>", "</protein>");
		Set<String> interakcie = Spracovanie_textu.vratObsahZnaciek(veta, "<interakcia>", "</interakcia>");
		proteiny.remove("");
		interakcie.remove("");
		
		// Vyberie z vety znacky
		String bezZnaciek = veta;
		bezZnaciek = bezZnaciek.replace("<protein>", "");
		bezZnaciek = bezZnaciek.replace("</protein>", "");
		bezZnaciek = bezZnaciek.replace("<interakcia>", "");
		bezZnaciek = bezZnaciek.replace("</interakcia>", "");
		
		// Podmienky
			// Ak je elementov prilis, nerobi to kvoli vysokym narokom (casovym, pamatovym)
		int pocetP = 0;
		int pocetI = 0;
		
		for(String protein : proteiny) {
			Set<Integer> pozicie = Spracovanie_textu.vratPozicie(bezZnaciek, protein);
			pocetP = pozicie.size() + pocetP;
		}
		
		for(String interakcia : interakcie) {
			Set<Integer> pozicie = Spracovanie_textu.vratPozicie(bezZnaciek, interakcia);
			pocetI = pozicie.size() + pocetI;
		}
		
		if(pocetP * pocetP * pocetI > maxPocetMoznosti) return anotovane;
			
			// Prili vela nazvov elementov
		if(proteiny.size() * proteiny.size() * interakcie.size() > maxPocetMoznosti) return anotovane;
		
			// Podmienka kedy PPI vo vete nie je
		if(proteiny.size() < 2 || interakcie.size() < 1) return anotovane;
		
		
		for(String interakcia : interakcie) {
			Set<Integer> pozicie = Spracovanie_textu.vratPozicie(bezZnaciek, interakcia);
			for(Integer poziciaInterakcia : pozicie) {
				String vlavo = "";				
				String vpravo = "";
				
				int vlavoP = -1;
				int vpravoP = 1000000; // nejake velmi vysoke cislo
				
				for(String protein : proteiny) {
					Set<Integer> pozicieProtein = Spracovanie_textu.vratPozicie(bezZnaciek, protein);
					for(Integer poziciaProtein : pozicieProtein) {
						if(poziciaProtein < poziciaInterakcia) { // Podmetova cast vety (vlavo)
							if(poziciaProtein > vlavoP) {
								vlavoP = poziciaProtein;
								vlavo = protein;
							}
						} else { // Predmetova cast vety (vpravo)
							if(poziciaProtein < vpravoP) {
								vpravoP = poziciaProtein;
								vpravo = protein;
							}							
						}						
					}					
				}
								
				// Ked na jednej strane nie je nic, tak PPI pri tejto interakcii nie je
				if(vlavoP == -1 || vpravoP == 1000000) continue;
				
				//System.out.println(vlavoP + " - " + vpravoP);
				
				Set<String> proteinyS = new HashSet<>(); 
				proteinyS.add(vlavo);
				proteinyS.add(vpravo);
				Set<String> interakcieS = new HashSet<>();
				interakcieS.add(interakcia);
				anotovane.addAll(zapisAnotacie(bezZnaciek, proteinyS, interakcieS));				
			}									
		}
		
		return anotovane;
	}
	
	private Set<String> zapisAnotacie(String vetaBezZnaciek, Set<String> proteiny, Set<String> interakcie) {
		//System.out.println("Zapisuje anotacie.");
		//System.out.println("Proteiny: " + proteiny);
		//System.out.println("Interakcie: " + interakcie);
		Set<String> anotovane = new HashSet<>();
		int i=0; int j=0;
		for(String protein1 : proteiny) {
			i++;
			for(String protein2 : proteiny) {
				j++;
				//if(j <= i) continue; // same so sebou sa neanotuje (duplikovane sa zrusia ulozenim do mnoziny)
				for(String interakcia : interakcie) {
					//System.out.println(i + " - " + interakcia + " - " + j);
					//System.out.println(protein1 + " - " + interakcia + " - " + protein2);
					if(protein1.equals(protein2)) continue;
					String anotovanaVeta = vetaBezZnaciek;
					anotovanaVeta = anotovanaVeta.replace(protein1, "<protein>"+protein1+"</protein>");
					anotovanaVeta = anotovanaVeta.replace(protein2, "<protein>"+protein2+"</protein>");
					anotovanaVeta = anotovanaVeta.replace(interakcia, "<interakcia>"+interakcia+"</interakcia>");
					anotovane.add(anotovanaVeta);
				}
			}
		}
		
		return anotovane;			
	}
	
}


























































