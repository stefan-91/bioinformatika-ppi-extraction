package vytvaranie_vystupov;

import java.util.HashSet;
import java.util.Set;

import databazaSQLite.Databaza;
import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

/**
 * Pripravi udaje pre databazu na webe
 * 
 * Model databazy
 * 
 * * Interakcie
 * - id, nazov
 * 
 * * Proteiny
 * - id, nazov
 *
 * * Vety
 * - id, nazov, zdroj (url), id_interakcie, id_proteinu 1, id_proteinu 2 
 *
 */
public class Pripravenie_na_web {
	private String anotovaneVetyCesta = "../korpus/vysledky/";
	//private String proteiny = "";
	//private String interakcie = "";
	
	private Databaza db = new Databaza();
	private Databaza dbPracovna = new Databaza();
	
	public Pripravenie_na_web() {
		db.nastavCestuDB("../vystupy/databaza_na_web");
	}
	
	public void start() {
		// Nahra anotovane vety
		db.aktualizujDB("DELETE FROM Anotovane_vety;");
		db.aktualizujDB("DELETE FROM Pouzite_proteiny;");	
		db.aktualizujDB("DELETE FROM Pouzite_interakcie;");
		
		Set<String> interakcieNazvy = new HashSet<>();
		Set<String> proteinyNazvy = new HashSet<>();
		
		Set<String> anotovane = Uprava_suborov.vratVsetkyNazvySuborov(anotovaneVetyCesta);
		//System.out.println(anotovane);
		
		int pocitadlo = 0;
		String data = "";
		for(String anotovany : anotovane) {
			if(!anotovany.endsWith("-anotacie.txt")) continue;		
			
			System.out.println("Tvorba vystupu pre web: " + pocitadlo + "/" + (anotovane.size() / 2));
			pocitadlo++;
			
			// Najde url suboru
			String nazovSubOrig = anotovany.replace("-anotacie", "");
			nazovSubOrig = nazovSubOrig.replace(".txt", "");
			String sql = "SELECT url FROM Originalne_texty_URL WHERE nazov_suboru = '" + nazovSubOrig + "'";
			//System.out.println(sql);
			String[][] urlArr = dbPracovna.vratPole(sql);
			if(urlArr.length == 0) {
				System.err.println("Subor " + nazovSubOrig + " nie je zapisany v DB.");
				continue; // Bez URL nezapisujeme
			}
			
			String url = urlArr[0][0];
			//System.out.println(url);
			
			// Zapise vety do vystupnej databazy
			String obsah = Uprava_suborov.vratObsahSuboru(anotovaneVetyCesta + anotovany);
			String[] vety = obsah.split("\n");
			
			for(String veta : vety) {
				if(veta.trim().length() == 0) continue;
				
				//------ Spracovanie anotovanej vety ------
				
				proteinyNazvy.addAll(Spracovanie_textu.vratObsahZnaciek(veta, "<protein>", "</protein>"));
				interakcieNazvy.addAll(Spracovanie_textu.vratObsahZnaciek(veta, "<interakcia>", "</interakcia>"));
				
				data = data + ",(\"" + veta + "\",\"" + url + "\")";
				
				//-----------------------------------------
				
				if(data.length() > 10000) {
					vlozDoDB(data);
					data = "";
				}
			}
			//break; // Pozn. naostro zakomentovat
		}
		// Vlozi este zvysok
		if(data.length() > 0) {
			vlozDoDB(data);
			data = "";
		}	
		
		// Vlozi pouzite nazvy proteinov
		String dataP = "";		
		for(String slovo : proteinyNazvy) {
			dataP = dataP + ",('" + slovo + "')";
		}
		if(dataP.length() > 0) {
			dataP = dataP.substring(1);
			String sql = "INSERT OR IGNORE INTO Pouzite_proteiny (nazov) VALUES " + dataP;
			//System.out.println(sql);
			db.aktualizujDB(sql);
		}
		
		// Vlozi pouzite nazvy interakcii
		String dataI = "";	
		for(String slovo : interakcieNazvy) {
			dataI = dataI + ",('" + slovo + "')";
		}
		if(dataI.length() > 0) {
			dataI = dataI.substring(1);
			String sql = "INSERT OR IGNORE INTO Pouzite_interakcie (nazov) VALUES " + dataI;
			//System.out.println(sql);
			db.aktualizujDB(sql);
		}
		System.out.println("Koniec tvorby vystupu na web.");		
	}
	
	
	public void vlozDoDB(String data) {
		if(data.length() > 0) {
			data = data.substring(1);
			String sql = "INSERT OR IGNORE INTO Anotovane_vety (veta, url) VALUES " + data;
			db.aktualizujDB(sql);
		}		
	}
}



















































