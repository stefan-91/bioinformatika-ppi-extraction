package vytvaranie_vystupov;

import java.util.Set;

import databazaSQLite.Databaza;
import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

public class Samostatne_xml {
	
	// Vygeneruje naraz vsetko do jedneho suboru
	public void vytvor(String vstupAdr, String vystupAdr) {	
		Databaza db = new Databaza();
		Uprava_suborov.vymazObsahPriecinka(vystupAdr);
		String vysledok = vystupAdr + "/" + "korpus_anotovane_ppi.xml";
		Uprava_suborov.vytvorSubor(vysledok);
		Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "<xml>");
		Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "<collection>");
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstupAdr);		
		for(String sub : obsah) {
			//System.out.println(sub);
			if(!sub.contains("-anotacie")) continue;
			String vstupSub = vstupAdr + "/" + sub;
			
			System.out.println(sub);
			
			String dokument = "";						
			// Ziska udaje na zapis
			String[] pomArr = sub.split("-");
			String id = pomArr[1];
			
			String sql = "SELECT ID FROM Dopyty WHERE ID LIKE '%"+id+"%'";
			System.out.println(sql);
			String[][] pole = db.vratPole(sql);
			String url = pole[0][0];
			dokument = dokument  + "<url>" + url + "</url>" + "\n";
			
			String text = Uprava_suborov.vratObsahSuboru(vstupSub);
			dokument = dokument + spracujAnotacie(text);

			// Zapise
			Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "<document>");
			Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, dokument);			
			Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "</document>");		
		}
		
		Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "</collection>");
		Uprava_suborov.pridajNaKoniecDoSuboru(vysledok, "</xml>");
		db.ukonciSpojenie();
	}
	
	private String spracujAnotacie(String text) {
		String anotacie = "";
		String[] riadky = text.split("\n");
		for(String riadok : riadky) {
			anotacie = anotacie + "<line> \n";
			Set<String> proteiny = Spracovanie_textu.vratObsahZnaciek(riadok, "<protein>", "</protein>");
			Set<String> interakcie = Spracovanie_textu.vratObsahZnaciek(riadok, "<interakcia>", "</interakcia>");
			
			// Da prec znacky
			String bezZnaciek = riadok.replace("<protein>", "");
			bezZnaciek = bezZnaciek.replace("</protein>", "");
			bezZnaciek = bezZnaciek.replace("<interakcia>", "");
			bezZnaciek = bezZnaciek.replace("</interakcia>", "");
			
			bezZnaciek = bezZnaciek.replace("&", "&#38;");
            bezZnaciek = bezZnaciek.replace("<", "&#60;");
            bezZnaciek = bezZnaciek.replace(">", "&#62;");
            bezZnaciek = bezZnaciek.replaceAll("[^\\x00-\\x7F]", ".");			
			
			anotacie = anotacie + "<text>" + bezZnaciek + "</text> \n";
			
			// Zapise anotacie proteinov
			for(String protein : proteiny) {
				Set<Integer> pozicie = Spracovanie_textu.vratPozicie(bezZnaciek, protein);
				for(Integer pozicia : pozicie) {
					anotacie = anotacie + "<anotation> \n";
					anotacie = anotacie + "<elem>" + protein + "</elem> \n";					
					anotacie = anotacie + "<info>protein</info> \n";
					anotacie = anotacie + "<location offset=\"" + pozicia + "\" length=\"" + protein.length() + "\" /> \n";
					anotacie = anotacie + "</anotation> \n";
				}					
			}
			
			// Zapise anotacie interakcii
			for(String interakcia : interakcie) {
				Set<Integer> pozicie = Spracovanie_textu.vratPozicie(bezZnaciek, interakcia);
				for(Integer pozicia : pozicie) {
					anotacie = anotacie + "<anotation> \n";
					anotacie = anotacie + "<elem>" + interakcia + "</elem> \n";
					anotacie = anotacie + "<info>interaction</info> \n";
					anotacie = anotacie + "<location offset=\"" + pozicia + "\" length=\"" + interakcia.length() + "\" /> \n";
					anotacie = anotacie + "</anotation> \n";
				}					
			}
			anotacie = anotacie + "</line> \n";
			
		}
		
		return anotacie;
	}
	
	
}



























































