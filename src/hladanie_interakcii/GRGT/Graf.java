package hladanie_interakcii.GRGT;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

public class Graf {
	private List<Uzol> uzly = new LinkedList<>();
	
	public boolean existujeUzol(Uzol uzol) {
		for(Uzol existujuci : uzly) {
			if(uzol.id == existujuci.id) {
				return true;
			}
		}
				
		return false;
	}
	
	public Uzol vratPodlaId(int id) {
		for(Uzol u : uzly) {
			if(u.id == id) return u; 
		}
		
		return null; // Ak nenajde nic
	}
	
	public void pridajUzol(Uzol u) {
		uzly.add(u);
	}
	
	// Pri hladani najkratsej cesty musime zmazat medzivysledky (inak pocita s nimi, teda zle)
	private void vymazPredchodcovNajkratsiaCesta() {
		for(Uzol u : uzly) {
			u.predchodcovia.clear(); 
		}		
	}
	
	/**
	 * Vsetky hrany maju rovnaku velkost, takze sa jedna iba o jednoduche 
	 * prehladavanie do sirky a najkratsia cesta je ta, v ktorej 
	 * dosiahneme ciel v najbllizsej iteracii
	 * 
	 * @return
	 * 		Najkratsou cestu uzlov v podobe zoznamu
	 */
	public List<Uzol> najdiNajkratsiuCestu(Uzol start, Uzol ciel, String veta) {
		// Vymaze medzivysledky v grafe
		// Musi byt na zaciatku lebo v metode sa vola return - potom by sa to vynechalo
		vymazPredchodcovNajkratsiaCesta();
		
		Set<Uzol> vybavene = new HashSet<>();
		Set<Uzol> objavene = new HashSet<>();
		
		// Log
		//System.out.println("=====================");
		//System.out.println("Start: " + start + ", ciel: " + ciel);
		//System.out.println("Veta: " + veta);
		
		objavene.add(start);
		boolean nasloCiel = false;
		int iteracie = 0;
		int pocet = 0;
		while(true) {
			// Vybavi objavene (objavi z nich nove uzly a zapise predchodcov)
			Set<Uzol> nove = new HashSet<>();
			for(Uzol u : objavene) {		
				// Pozrie sa na vsetky vychadzajuce uzly z naseho uzla
				for(Hrana h : u.vychadzajuce) {
					Uzol novy = h.druhy; // uzol na konci hrany, ktora ide z naseho uzla
					
					// Zapise uzol iba ak je uplne novy
					if(!vybavene.contains(novy) && !objavene.contains(novy)) {
						nove.add(novy);
						// Prida uzol z ktoreho sme sa sem dostali
						// Objavenym predchodcu nepiseme lebo by to nebola najkratsia cesta
						if(novy.predchodcovia.size() == 0) {
							novy.predchodcovia.add(u); // Zapisujeme iba jedneho predchodcu
							//System.out.println("Predchodca: " + u + " <-- " + novy);
						}
					}										
				}																
			}
									
			vybavene.addAll(objavene);
			objavene.clear();
			objavene.addAll(nove);
			nove.clear();
			
			//---- Riadiace operacie pre cyklus ----
			// Pozrie sa ci uz sme nasli cielovy uzol, ak ano, ukonci hladanie
			for(Uzol u : objavene) {
				if(u.equals(ciel)) {
					nasloCiel = true;
					break;
				}					
			}					
			if(nasloCiel == true) break;			
			
			if(pocet == vybavene.size()) { // Ak cesta neexistuje
				//System.out.println("Nedokazalo najst cestu");
				return null;
			} else {
				pocet = vybavene.size();				
			}
			
			//System.out.println("Iteracia: " + iteracie);
			iteracie++;
		}
				
		//if(nasloCiel == false) {
		//	System.out.println("NEnaslo cestu, start: " + start + ", ciel: " + ciel);
		//} else {
		//	System.out.println("Naslo cestu, start: " + start + ", ciel: " + ciel);			
		//}
		
		//System.out.println("Dlzka cesty je " + iteracie);
		
		// Spatne najde cestu podla ukazovatelov na predchodcov
		// U - urobit pre viac ciest
		//List<List<Uzol>> cesty = new LinkedList<>();
		Uzol aktualny = ciel;
		List<Uzol> cesta = new LinkedList<>();
		cesta.add(aktualny);
		Uzol predosly = aktualny;				
		while(true) {			
			for(Uzol u : aktualny.predchodcovia) {
				cesta.add(u);
				aktualny = u;
				//break; // Zatial hlada iba prveho predchodcu
			}			                            						
			
			if(aktualny.equals(start)) {
				// Pozn. aktualny sme uz vlozili predtym
				break;
			}	
			
			if(predosly.equals(aktualny)) {
				System.out.println("========================");
				System.out.println("Chybne zapisana cesta");
				System.out.println(cesta);				
				
				break;
			}			
			predosly = aktualny;			
		}
	
		// Prehodi poradie aby bolo smerom od proteinu k interacii		
		List<Uzol> cestaP = new LinkedList<Uzol>();
		for(Uzol u : cesta) {
			((LinkedList<Uzol>) cestaP).addFirst(u);
		}
		cesta = cestaP;
						
		//System.out.println("Cesta je " + cesta);
		
		return cesta;
	}
}
































































