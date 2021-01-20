package predspracovanie_textov;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uprava_suborov.Uprava_suborov;

public class Predspracovanie_udajov_na_testovanie {
	
	public void predspracujKorpusy(String korpus1, String korpus2, int maxDlzkaVety) {
		
		String poloformatAdr = "../materialy/ine_korpusy/"+korpus1+"/nas_format/";		
		String  testovacieVysledkyAdr = "../materialy/testovacie_udaje/"+korpus2+"/testovacie_vysledky/vysledky_nas_format_sub1.txt";		
		String cisteVetyAdr = "../materialy/testovacie_udaje/"+korpus2+"/ciste_neoznacene_vety/";
		predspracujPoloformat(poloformatAdr, testovacieVysledkyAdr, cisteVetyAdr, maxDlzkaVety);		
	}
	
	// Zo vzorov odstrani nazvy interakcii
	public void zovseobecniVzory() {
		
		
		
	}
	
	
	// Pozn. Tu pouzivame ako vstup poloformat, ktory sme vytvorili skor
	// Pouziva sa pre korpusy, ktore v sebe NEMAJU znacku interakcie
	public void predspracujPoloformat(String poloformatAdr, String  testovacieVysledkyAdr, String cisteVetyAdr, int maxDlzkaVety) {
		// Vymaze predosly obsah
		uprava_suborov.Uprava_suborov.vymazObsahPriecinka(testovacieVysledkyAdr);					
		uprava_suborov.Uprava_suborov.vymazObsahPriecinka(cisteVetyAdr);	
	
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(poloformatAdr);
		int pocitadlo = 0;
		int vacsieVety = 0;
		for(String sub : obsah) {
			// Vytvori vystupne subory				
			uprava_suborov.Uprava_suborov.vytvorSubor(testovacieVysledkyAdr + sub);					
			uprava_suborov.Uprava_suborov.vytvorSubor(cisteVetyAdr + sub);			
			
			String text = Uprava_suborov.vratObsahSuboru(poloformatAdr + sub);
			String[] riadky = text.split("\n");
			
			for(String riadok : riadky) {
				pocitadlo++;
				if(pocitadlo % 5 == 0) {
					System.out.println("Predspracuvava udaje z poloformatu: " + pocitadlo);
				}
				
				String[] slova = riadok.split(" ");
				if(maxDlzkaVety > 0) {
					if(slova.length > maxDlzkaVety) {
						vacsieVety++;
						continue;
					}
				}

				
				String oznackovanaVeta = riadok; // ostava povodne lebo uz sme to pripraili v poloformate
				// Da prec znacky interakcii lebo povodny korpus ich v sebe nema
				oznackovanaVeta = oznackovanaVeta.replace("<interakcia>", "");
				oznackovanaVeta = oznackovanaVeta.replace("</interakcia>", "");	
				oznackovanaVeta = oznackovanaVeta.replace("<protein>protein</protein>", "<protein>PROTEIN</protein>");
				
				String cistaVeta = riadok;
				// Da prec znacky interakcie
				cistaVeta = cistaVeta.replace("<interakcia>", "");
				cistaVeta = cistaVeta.replace("</interakcia>", "");								
				
				// Da prec znacky proteinu
				// Pozn. NEDAVA prec lebo v testovacich korpusoch si aj ine ako ludske proteiny				
				//cistaVeta = cistaVeta.replace("<protein>protein</protein>", "PROTEIN");
				
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(testovacieVysledkyAdr + sub, oznackovanaVeta);
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(cisteVetyAdr + sub, cistaVeta);
				
				
			}		
		}			
		System.out.println("Nepouzili sme " + vacsieVety + "/" + pocitadlo + " dlhsie ako " + maxDlzkaVety);
		
	}	
	
	// Pozn. Tu pouzivame ako vstup poloformat, ktory sme vytvorili skor
	public void predspracujHuBerlin() {
		// Miesta kam zapisujeme vystup
		String oznackovaneVety = "../materialy/testovacie_udaje/korpus_hu_berlin/testovacie_vysledky/vysledky_nas_format_sub1.txt";
		uprava_suborov.Uprava_suborov.vytvorSubor(oznackovaneVety);
		
		String cisteVety = "../materialy/testovacie_udaje/korpus_hu_berlin/ciste_neoznacene_vety/ciste_vety_sub1.txt";
		uprava_suborov.Uprava_suborov.vytvorSubor(cisteVety);		
		
		// Vstup
		List<String> zdroje = new LinkedList<>();
		zdroje.add("../materialy/testovacie_udaje/korpus_hu_berlin/originalny_format/poloformat.txt");

		int pocitadlo = 0;
		for(String subor : zdroje) {			
			String obsah = uprava_suborov.Uprava_suborov.vratObsahSuboru(subor);
			String[] riadky = obsah.split("\n");
			
			for(String riadok : riadky) {
				// Priklad riadka: <protein>protein</protein> <interakcia>regulatory</interakcia> subunit <protein>protein</protein>
				
				String oznackovanaVeta = riadok; // ostava povodne lebo uz sme to pripraili v poloformate
				// Da prec znacky interakcii lebo povodny korpus ich v sebe nema
				oznackovanaVeta = oznackovanaVeta.replace("<interakcia>", "");
				oznackovanaVeta = oznackovanaVeta.replace("</interakcia>", "");	
				oznackovanaVeta = oznackovanaVeta.replace("<protein>protein</protein>", "<protein>PROTEIN</protein>");
				
				String cistaVeta = riadok;
				// Da prec znacky interakcie
				cistaVeta = cistaVeta.replace("<interakcia>", "");
				cistaVeta = cistaVeta.replace("</interakcia>", "");								
				
				// Da prec znacky proteinu
				cistaVeta = cistaVeta.replace("<protein>protein</protein>", "PROTEIN");
				
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(oznackovaneVety, oznackovanaVeta);
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(cisteVety, cistaVeta);
				
				pocitadlo++;
				if(pocitadlo % 100 == 0) {
					System.out.println("Predspracuvava udaje z hu-berlin: " + pocitadlo);
				}				
			}
		}
		System.out.println("Pocet spracovanych riadkov: " + pocitadlo);						
	}	
	
}
