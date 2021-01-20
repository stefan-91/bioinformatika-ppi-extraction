package testovanie_metod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hladanie_interakcii.GRGT.GRGT;
import hladanie_interakcii.co_occurence_jednoduche.Co_occurence_1;
import hladanie_interakcii.co_occurence_tuke2018.co_occurence_tuke2018;
import netriedene.Spracovanie_textu;
import predspracovanie_textov.Znackovanie_viet;
import uprava_suborov.Uprava_suborov;

/**
Testovanie

- Každý korpus je iný. Jeden nemá v sebe názvy interakcií, iný má iba všeobecné názvy proteínov.
- Meria to celu metodu: schopnost najdenia nazvu proteinu, najdenie interakcie.

- Nejde o testovanie hladania nazvovo entit ale o testovanie hladania interakcii
Preto nazvy proteinov pre jednoduchost oznacujeme jednotnou znackou

* Nas format
- Nazvy proteinov maju jednotnu znacku
- Pri interakcii je pridana znacka
- Aby sa odlisilo viac interakcii v jednej vete, 
tak v toto pripade je jedna veta zapisana viackrat, raz pre kazdu interakciu, ktora je v nej zaznamenana 

* Neoznacena veta
- Nazvy proteinov maju jednotnu znacku
Ine znacky to nema.

* Postup
- Previesť anotovaný korpus na náš formát.
- Pripravit neoznačené vety.
- Zbehnúť môj nástroj nad danými vetami
- Zapísať výsledok môjho nástroja do môjho formátu.
- Obidva výsledky porovnať.

*/
public class Otestovanie_metody {
		
	// Over potencial dalsieho zovseobecnovania
	public void otestujBezNazvovInterakcii() {
		// Nacita vzory
		System.out.println("Nacitava nase vzory");
		Set<String> nase = new HashSet<>();
		nase.add("../materialy/vzory_interakcii/hash/korpus_BioInfer_vzory.txt");
		//nase.add("../materialy/vzory_interakcii/hash/vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/korpus_AIMed_vzory.txt");
		//nase.add("../materialy/vzory_interakcii/hash/pomocne_vzory_kopia.txt");		
		nase.add("../materialy/vzory_interakcii/hash/korpus_Hprd50_vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/korpus_IEPA_vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/korpus_LLL_vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/korpus_PICAD_vzory.txt");		
		
		Set<String> naseVzory = new HashSet<>();
		for(String cesta : nase) {
			naseVzory.addAll(nacitajVzory(cesta));
		}

		System.out.println("Nacitava testovacie vzory");
		Set<String> testovacie = new HashSet<>();
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_Hprd50_vzory.txt");
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_IEPA_vzory.txt");
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_LLL_vzory.txt");
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_PICAD_vzory.txt");
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_AIMed_vzory.txt");
		//testovacie.add("../materialy/vzory_interakcii/hash/korpus_BioInfer_vzory.txt");
		testovacie.add("../materialy/vzory_interakcii/hash/vzory.txt");		
		testovacie.add("../materialy/vzory_interakcii/hash/pomocne_vzory_kopia.txt");
		
		Set<String> testovacieVzory = new HashSet<>();
		for(String cesta : testovacie) {
			testovacieVzory.addAll(nacitajVzory(cesta));
		}
						
		System.out.println("Pocet testovacich: " + testovacieVzory.size() + ", pocet nasich: " + naseVzory.size());
		
		// Nacita nazvy interakcii
		System.out.println("Nacitava nazvy interakcii");
		Set<String> interakcie = new HashSet<>();
		interakcie.addAll(nacitajVzory("../materialy/nazvy_interakcii/dohromady.txt"));
		
		// Vyhodi interakcie zo vzorov
		Set<String> testovacieBez = vyhodInterakcie(testovacieVzory, interakcie);		
		Set<String> naseBez = vyhodInterakcie(naseVzory, interakcie);
						
		System.out.println("Pocet testovacich: " + testovacieBez.size() + ", pocet nasich: " + naseBez.size());
		// Vypocita pokrytie
		zistiPkrytieVzorov(naseBez, testovacieBez);		
		
	}
	
	private Set<String> vyhodInterakcie(Set<String> vzory, Set<String> interakcie) {			
		Set<String> naseBez = new HashSet<>();
		int viacInterakcii = 0;
		for(String vzor : vzory) {
			vzor = vzor.toLowerCase();
			String povodny = vzor;			
			boolean nasloI = false;
			int zmeny = 0;
			Set<String> zmenene = new HashSet<>();
			
			for(String interakcia : interakcie) {
				if(vzor.contains(interakcia) == true) {
					vzor = vzor.replace(interakcia, "INTERAKCIA");					
					nasloI = true;
					zmeny++;
					zmenene.add(interakcia);					
				}
			}
			naseBez.add(vzor);
			
			// Kontrolne vypisy
			if(nasloI == false) { // Ak by bola chyba, vzor interakcie nemoze existovat
				System.out.println("CHYBA: vzor nema interakciu: " + vzor);
			}
			
			if(zmeny> 2) {
				//System.out.println("Viac interakcii: " + zmenene + ", Vzor: " + povodny);
				viacInterakcii++;
			}
		}	
		
		System.out.println("Vzory kde je viac interakcii: " + viacInterakcii);
		
		return naseBez;
	}
	
	// Iba porovna najdene vzory z korpusov, resp. ci su testovacie vzory pokryte nasimi 
	public void otestujPodlaVzorov() {
		// Nacita vzory
		System.out.println("Nacitava nase vzory");
		Set<String> nase = new HashSet<>();
		nase.add("../materialy/vzory_interakcii/hash/korpus_BioInfer_vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/korpus_AIMed_vzory.txt");
		nase.add("../materialy/vzory_interakcii/hash/pomocne_vzory_kopia.txt");
		
		Set<String> naseVzory = new HashSet<>();
		for(String cesta : nase) {
			naseVzory.addAll(nacitajVzory(cesta));
		}

		System.out.println("Nacitava testovacie vzory");
		Set<String> testovacie = new HashSet<>();
		testovacie.add("../materialy/vzory_interakcii/hash/korpus_Hprd50_vzory.txt");
		testovacie.add("../materialy/vzory_interakcii/hash/korpus_IEPA_vzory.txt");
		testovacie.add("../materialy/vzory_interakcii/hash/korpus_LLL_vzory.txt");
				
		Set<String> testovacieVzory = new HashSet<>();
		for(String cesta : testovacie) {
			testovacieVzory.addAll(nacitajVzory(cesta));
		}
			
		System.out.println("Pocet testovacich: " + testovacieVzory.size() + ", pocet nasich: " + naseVzory.size());
		
		//----------- Zisti hodnoty ---------
		zistiPkrytieVzorov(naseVzory, testovacieVzory);
		
	}
	
	private void zistiPkrytieVzorov(Set<String> nase, Set<String> testovacie) {
		int maxDlzka = 0;
		for(String nasVzor : nase) {
			int dlzka = zistiDlzkuVzoru(nasVzor);
			if(dlzka > maxDlzka) maxDlzka = dlzka; 
		}
		System.out.println("Max. dlzka je " + maxDlzka);
		
		int naslo = 0;
		int nenaslo = 0;
		int pridlhe = 0;
		Set<Integer> pasujeS = new HashSet<>();
		Set<Integer> nepasujeS = new HashSet<>();
		
		for(String testovaci : testovacie) {
			int dlzka = zistiDlzkuVzoru(testovaci);
			/*
			if(maxDlzka < dlzka) {
				System.out.println("Presiahla sa max dlzka " + dlzka);
				pridlhe++;
				continue; // hu_berlin sme nacitavali iba do rucitej dlzky 
			}
			*/
			if(nase.contains(testovaci) == true) {
				naslo++;
				pasujeS.add(dlzka);
			}
			else {
				nenaslo++;			
				nepasujeS.add(dlzka);
				// Pozri sa na dlzku
				//zistiDlzkuVzoru(testovaci);
			}
		}
		
		double priemerPasujuceho = zistiPriemer(pasujeS);
		double priemerNepasujuceho = zistiPriemer(nepasujeS);
		
		System.out.println("Koniec testu hladania vzorov");
		System.out.println("Pokryte: " + naslo + ", nepokryte: " + nenaslo);
		System.out.println("Pridlhe vzory: " + pridlhe);
		System.out.println("Priemer pasujuceho: " +  priemerPasujuceho + ", priemer nepasujuceho: " + priemerNepasujuceho);
			
	}
	
	// Zisti aritmeticky priemer
	private double zistiPriemer(Set<Integer> cisla) {
		double priemer = 0;
		double sucet = 0;
		double pocet = cisla.size();
		
		for(Integer cislo : cisla) {
			sucet = sucet + cislo;
		}		
		priemer = sucet / pocet;
		
		return priemer;
	}
	
	// Priklad vstupu: PROTEIN__dobj__mediated__PROTEIN__compoundNEJAKE_SLOVO__nsubj__mediated__
	private int zistiDlzkuVzoru(String vzor) {
		int dlzka = 0;
		
		String[] slovaArr = vzor.split("_");
		//System.out.println(Arrays.toString(slovaArr));
		for(String slovo : slovaArr) {
			slovo = slovo.trim();
			if(slovo.length() == 0) continue;
			dlzka++;
		}
		
		return dlzka;
	}
	
	private Set<String> nacitajVzory(String cesta) {
		Set<String> vzory = new HashSet<>();

		String obsahVzory = Uprava_suborov.vratObsahSuboru(cesta);
		String[] slova = obsahVzory.split("\n");	
		
		for(String slovo : slova) {
			slovo = slovo.trim();
			if(slovo.length() == 0) continue;
			vzory.add(slovo);
		}		
		System.out.println("Nacitalo " + vzory.size() + " vzorov.");		
		
		return vzory;
	}
	
	public void otestujMetodu(String testovaciAdresar, String nazovMetody) {
		String cisteVetyAdr = "../materialy/testovacie_udaje/"+testovaciAdresar+"/ciste_neoznacene_vety/";
		String oznackovaneNazvyAdr = "../materialy/testovacie_udaje/"+testovaciAdresar+"/pomocny/";	
		String naseVysledkyAdr = "../materialy/testovacie_udaje/"+testovaciAdresar+"/nase_vysledky/";
		String testovacieVysledkyAdr = "../materialy/testovacie_udaje/"+testovaciAdresar+"/testovacie_vysledky/";
		
		// 1. Predspracuje ciste vety (oznackuje nazvy proteinov a interakcii)
		
		// Vymaze predosle vysledky		
		Uprava_suborov.vymazObsahPriecinka(oznackovaneNazvyAdr);
		
		Znackovanie_viet pt = new Znackovanie_viet();
		pt.pridajProtein("protein"); // Znacka pre proteiny lebo korpus ma iba vseobecny nazov pre vsetky proteiny					
		pt.predpriprav(cisteVetyAdr, oznackovaneNazvyAdr, true, false, true, false); // nehlada nove proteiny
		
		// 2. Nad predspracovanym suborom spusti vyhladavanie interakcii
		
		Uprava_suborov.vymazObsahPriecinka(naseVysledkyAdr);
		
		// ---------- Vyberame metodu na vyhodnotenie -----------
		
		if(nazovMetody.equals("grgt")) {
			// Metoda GRGT
			GRGT grgt = new GRGT();
			grgt.najdiInterakcie(cisteVetyAdr, oznackovaneNazvyAdr, naseVysledkyAdr);			
		}

		if(nazovMetody.equals("co1")) {
			// Metoda jednoduche co-occurence
			Co_occurence_1 co1 = new Co_occurence_1();
			co1.najdiInterakcie(cisteVetyAdr, oznackovaneNazvyAdr, naseVysledkyAdr);			
		}
		
		if(nazovMetody.equals("co2018")) {
			// Metoda co-occurence z TUKE 2018
			co_occurence_tuke2018 co_tuke2018 = new co_occurence_tuke2018();
			co_tuke2018.najdiInterakcie(cisteVetyAdr, oznackovaneNazvyAdr, naseVysledkyAdr);			
		}
		
		
	
		
		//--------------------------------
		
		// Vyhodi znacky pre interakcie lebo ich nema ani testovaci korpus
		vymazNadbytocneAnotacie(naseVysledkyAdr);
		vymazNadbytocneSubory(naseVysledkyAdr); 
		
		// 3. Vyhodnoti vysledok
		vypocitajFSkore(naseVysledkyAdr, testovacieVysledkyAdr);
				
	}
	
	private void vypocitajFSkore(String naseVysledkyAdr, String testovacieVysledkyAdr) {
		float tp = 0;
		float fp = 0;
		float fn = 0;
		
		Set<String> testovacie = nacitajObsahDoMnoziny(testovacieVysledkyAdr);
		Set<String> nase = nacitajObsahDoMnoziny(naseVysledkyAdr); 
		System.out.println("Nasli sme " + nase.size() + " PPI");
		System.out.println("mame " + testovacie.size() + " testovacich viet");
		
		/*
		String veta = "Finally, we show that proper localization of SpoIVA required the expression of one or more genes which, like <protein>spoIVA</protein>, are under the control of the mother cell transcription factor <protein>sigmaE</protein>."; 
		veta = Spracovanie_textu.rozsirZnacky(veta);
		System.out.println("Rozsirene o znacky: " + veta);		
		if(true) return;
		*/

		for(String riadok : nase) { // Rozdeli to co sme nasli na spravne a nespravne
			// True positive - objektivne existuje a aj sme to nasli
			boolean naslo = false;
			for(String riadokT : testovacie) {
				String riadokTOrig = riadokT;
				riadokT = Spracovanie_textu.rozsirZnacky(riadokT);				
				//System.out.println("Rozsirene o znacky: " + riadokT);
				if(riadokT.contains(riadok)) { // Pouziva sa "contains" lebo v povodnom korpuse moze byt viac viet spolu
					tp++;
					naslo = true;
					//System.out.println("True positive je: " + riadok);	
					break;
				}
			}
			// False positive - objektivne neexistuje ale predsa sme to nasli
			if(naslo == false) {											
				fp++;
				//System.out.println("False positive je: " + riadok);				
			}
		}
		
		// False negative - este sa pozrie co sme vobec nenasli ale predsa tam bolo
		// Zoberieme to, co sme nasli a s tymto odoberieme to co je v testovacich, to co ostane je false negative
		int pocitadloPrazdnychViet = 0;
		for(String riadok : testovacie) {
			// False negative NIE JE tam kde nie su ziadne znacky - to je to, kde sme nieco nedokazali
			int prot = Spracovanie_textu.vratPocetVyskytov(riadok, "<protein>");
			//int inter = Spracovanie_textu.vratPocetVyskytov(riadok, "<interakcia>"); // Interakcie neobsahuje			
			if(prot < 2 /*|| inter < 1*/) {
				//System.out.println("Nema PPI: " + prot /*+ ", " + inter*/);
				pocitadloPrazdnychViet++;
				continue;
			}

			// Pre kazde z testovacich sa pozrie ci bolo najdene
			riadok = Spracovanie_textu.rozsirZnacky(riadok); // opat upravujeme testovacie pre pripad rovnakeho nazvu proteinu na viaceruch miestach
			boolean naslo = false;
			for(String riadokN : nase) {
				if(riadok.contains(riadokN)) { // Pouziva sa "contains" lebo v povodnom korpuse moze byt viac viet spolu
					naslo = true;
					break;
				}				
			}			
			if(naslo == false) {
				System.out.println("False negative je: " + riadok);	
				fn++;
			}
		}
		
		System.out.println("Pocet viet bez PPI: " + pocitadloPrazdnychViet + "/" + testovacie.size());
		
		System.out.println("TP: " + tp + ", FP: " + fp + ", FN: " + fn);
		
		float precision = (tp) / (tp + fp);
		float recall = (tp) / (tp + fn);
		System.out.println("Precision: " + precision + ", recall: " + recall);
		
		float FSkore = 2 * ( (precision * recall) / (precision + recall) );
		System.out.println("F-skore nad testovacim korpusom je " + FSkore);		
	}		
	
	// Testovanie metody s interakciami
	public void otestujGRGT(String korpus) {
		String cisteVetyAdr = "../materialy/testovacie_udaje/"+korpus+"/ciste_neoznacene_vety/";
		String oznackovaneNazvyAdr = "../materialy/testovacie_udaje/"+korpus+"/pomocny/";	
		String naseVysledkyAdr = "../materialy/testovacie_udaje/"+korpus+"/nase_vysledky/";
		String testovacieVysledkyAdr = "../materialy/testovacie_udaje/"+korpus+"/testovacie_vysledky/";
		
		// 1. Predspracuje ciste vety (oznackuje nazvy proteinov a interakcii)
		/*
		// Vymaze predosle vysledky
		Uprava_suborov.vymazObsahPriecinka(oznackovaneNazvyAdr);
		
		Znackovanie_viet pt = new Znackovanie_viet();
		pt.pridajProtein("protein"); // Znacka pre proteiny lebo korpus ma iba vseobecny nazov pre vsetky proteiny			
		pt.predpriprav(cisteVetyAdr, oznackovaneNazvyAdr, true, false, true);
		*/
		// 2. Nad predspracovanym suborom spusti vyhladavanie interakcii
		/*
		// Pozor! Nad 140 000 vetami bezi hladanie interakcii niekolko hodin	
		Uprava_suborov.vymazObsahPriecinka(naseVysledkyAdr);
		GRGT grgt = new GRGT();
		grgt.najdiInterakcie(cisteVetyAdr, oznackovaneNazvyAdr, naseVysledkyAdr);		
		
		// Vyhodi znacky pre interakcie lebo ich nema ani testovaci korpus
		//vymazNadbytocneAnotacie(naseVysledkyAdr);
		vymazNadbytocneSubory(naseVysledkyAdr); 
		*/					
		// 3. Vyhodnoti vysledok
		vypocitajFSkore(naseVysledkyAdr, testovacieVysledkyAdr);	
		
	}
	
private Set<String> nacitajObsahDoMnoziny(String cesta) {
	Set<String> mnoz = new HashSet<>();
	
	Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(cesta);
	
	for(String sub : obsah) {
		String text = Uprava_suborov.vratObsahSuboru(cesta + sub);
		String[] pole = text.split("\n");
		
		for(String riadok : pole) {
			riadok = riadok.trim();
			if(riadok.length() == 0) continue;
			mnoz.add(riadok);
		}		
	}
		
	return mnoz;
}	

private void vymazNadbytocneSubory(String cesta) {
	Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(cesta);
	// Vymaze nadbytocne
	for(String sub : obsah) {
		if(!sub.contains("anotacie")) {
			Uprava_suborov.vymazSubor(cesta + sub);
		}
	}	
}

private void vymazNadbytocneAnotacie(String cesta) {
	System.out.println(cesta);
	Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(cesta);
	for(String sub : obsah) {
		if(sub.contains("anotacie")) {
			String text = Uprava_suborov.vratObsahSuboru(cesta + sub);
			//System.out.println(text.subSequence(0, 100));						
			text = text.replace("<interakcia>", "");
			text = text.replace("</interakcia>", "");
			
			Uprava_suborov.vymazSubor(cesta + sub);
			Uprava_suborov.vytvorSubor(cesta + sub);
			Uprava_suborov.pridajNaKoniecDoSuboru(cesta+sub, text);			
		}
	}	
} 	
	
	
}

















































