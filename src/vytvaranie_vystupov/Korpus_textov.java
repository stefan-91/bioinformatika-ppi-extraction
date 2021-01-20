package vytvaranie_vystupov;

import java.util.Set;

import databazaSQLite.Databaza;
import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

// Vytvori textovy korpus - v texte su anotacie
public class Korpus_textov {
	
	public void vytvor(String vstupAdr, String htmlCesta, String vystupAdr) {	
		Databaza db = new Databaza();
		Uprava_suborov.vymazObsahPriecinka(vystupAdr);
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstupAdr);		
		for(String sub : obsah) {
			boolean prvyZapis = false;
			//System.out.println(sub);
			if(!sub.contains("-anotacie")) continue;
			String vstupSub = vstupAdr + "/" + sub;
			
			//System.out.println(sub);
		
			String zaklad = sub.replace("-anotacie.txt", "");
			System.out.println(zaklad);
			
			String subHtmlCesta = htmlCesta + "/" + zaklad + "_upravenyHTML.html";
			
			if(Uprava_suborov.existujeSubor(subHtmlCesta) == false) continue;
			
			String obsahHTML = Uprava_suborov.vratObsahSuboru(subHtmlCesta);
			
			String anotacie = Uprava_suborov.vratObsahSuboru(vstupSub);
			String[] riadky = anotacie.split("\n");
									
			boolean zmena = false;
			for(String riadok : riadky) {
				zmena = true;
			
				String bezZnaciek = riadok.replace("<protein>", "");
				bezZnaciek = bezZnaciek.replace("</protein>", "");
				bezZnaciek = bezZnaciek.replace("<interakcia>", "");
				bezZnaciek = bezZnaciek.replace("</interakcia>", "");
				
				// Da prec znacky
				String noveZnacky = riadok.replace("<protein>", "<span class=\"protein\">");
				noveZnacky = noveZnacky.replace("</protein>", "</span>");
				noveZnacky = noveZnacky.replace("<interakcia>", "<span class=\"nazovInterakcie\">");
				noveZnacky = noveZnacky.replace("</interakcia>", "</span>");
				
				//<span class="protein">ATM</span>
				//<span class="nazovInterakcie">activity</span>

				obsahHTML = obsahHTML.replace(bezZnaciek, noveZnacky);
				
			}
			
			if(zmena == true) { // Zapisujeme iba ked je v texte anotacia
				String vystupSub = vystupAdr + zaklad + ".html";
				Uprava_suborov.vytvorSubor(vystupSub);
				
				if(prvyZapis == false) {
					//System.out.println("Zapisuje CSS do dokumentu " + vystupSub);
					obsahHTML = obsahHTML.replace("<head><body>", "</head><body>");					
					obsahHTML = obsahHTML.replace("<html><head>", "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
					
					Uprava_suborov.pridajNaKoniecDoSuboru(vystupSub, obsahHTML);
					System.out.println(vystupSub);
					prvyZapis = true;
				}								
			}			
		}
		
		String cssText = ".protein {\n" + 
				"	color: green;\n" + 
				"	text-decoration: underline;\n" + 
				"}\n" + 
				"\n" + 
				".nazovInterakcie {\n" + 
				"	color: red;\n" + 
				"	text-decoration: underline;\n" + 
				"}\n" + 
				"\n" + 
				"input {\n" + 
				"	margin-top: 5px;\n" + 
				"	margin-bottom: 5px;	\n" + 
				"}\n" + 
				"\n" + 
				".vypisPouziteho {\n" + 
				"	font-family: monospace; \n" + 
				"}";
		
		Uprava_suborov.vytvorSubor(vystupAdr + "style.css");
		Uprava_suborov.pridajNaKoniecDoSuboru(vystupAdr + "style.css", cssText);
		
		
		db.ukonciSpojenie();
	}
}
