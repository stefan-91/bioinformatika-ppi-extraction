package stahovanie_clankov;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import databazaSQLite.Databaza;
import netriedene.MyData;
import uprava_suborov.Uprava_suborov;

public class StahovaniePMC {
	// Adresare v korpuse
	private String clankyTXT = "../korpus/originalne_texty";
	private String clankyPDF = "../korpus/originalne_texty_PDF";	
	private String urlPMC = "https://www.ncbi.nlm.nih.gov/pmc/articles/";	
	private Databaza db;
		
	// Ak sa nepodari stiahnut clanok, tak neulozi nic
	public void stiahniClanky(List<String> idClankov, String kategoria) {
		db  = new Databaza();
		Sprava_metadat sm = new Sprava_metadat ();
		
		int uzStiahnute = 0;
		int pocitadlo = 0;
		for(String id : idClankov) { // Pre kazde ID clanku			
			// Urci umiestnenia zdroja a ciela
			String nazovSuboru = "pmc-" + id; // Zatial bez pripony
			String url = urlPMC + id;
			
			// ulozi dopyt (pozn. musi ist este pred kontrolou ci bol clanok stiahnuty)
			sm.pridajDopyt(url, kategoria);
									
			// Pozrie sa ci ten clanok nahodou nestahovalo
			String sql = "SELECT * FROM Stiahnute_clanky WHERE url = '" + url + "';";
			//System.out.println(sql);
			String[][] pole = db.vratPole(sql);
			if(pole.length > 0) {
				uzStiahnute++;
				
				// Pocitadla
				if(pocitadlo % 5 == 0) {
					System.out.println("Uz stiahnute " + pocitadlo + "/" + idClankov.size());
				}
				
				pocitadlo++;				
				
				continue;
			} else {
				db.aktualizujDB("INSERT INTO Stiahnute_clanky (url) VALUES ('" + url + "')");
			}
			
			// Stiahne zo zdroja do ciela v danej forme
			String html = stiahniHTML(url); // Stiahne iba raz			
			stiahniTXT(html, nazovSuboru);
			//stiahniPDF(url, nazovSuboru);
			stiahniURL(url, nazovSuboru);
			stiahniTeloClankuHTML(html, nazovSuboru);
			//stiahniHTML(html, nazovSuboru);
			
			// Pocitadla
			if(pocitadlo % 1 == 0) {
				System.out.println(pocitadlo + "/" + idClankov.size());
			}
			
			pocitadlo++;
		}
		
		db.ukonciSpojenie();
		sm.myDestructor(); // ukonci spojenie do databazy
		System.out.println("Uz bolo skor stiahnutych " + uzStiahnute + "/" + idClankov.size() + " clankov.");
	}
	
	//------------ Stahovanie URL -----------
	// Uklada URL do databazy	
	public void stiahniURL(String url, String nazovSuboru) {	
		String urlPdf = url + "/pdf/";

		String sql = "INSERT OR IGNORE INTO Originalne_texty_URL (nazov_suboru, url) VALUES('"+nazovSuboru+"','"+urlPdf+"')";
		db.aktualizujDB(sql);		
	}

	//------------ Stahovanie PDF -----------
	public void stiahniPDF(String url, String nazovSuboru) {		
		String urlPdf = url + "/pdf/";
		//System.out.println(urlPdf);
		try {
			String cesta = (new File(clankyPDF)).getCanonicalPath() + "/" + nazovSuboru + ".pdf";
			Uprava_suborov.vymazSubor(cesta); // Pre istotu vymaze ak by existoval
			download(urlPdf, new File(cesta));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	//Zdroj: https://stackoverflow.com/questions/19309300/how-to-download-a-pdf-file-programmatically-from-a-webpage-with-html-extension
    public static void download(final String url, final File destination) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        final FileOutputStream output = new FileOutputStream(destination, false);
        final byte[] buffer = new byte[2048];
        int read;
        final InputStream input = connection.getInputStream();
        while((read = input.read(buffer)) > -1)
            output.write(buffer, 0, read);
        output.flush();
        output.close();
        input.close();
    }	
	
    //----------- Stahovanie tela clanku aj s HTML znackami --------
    
	public void stiahniTeloClankuHTML(String html, String nazovSuboru) {
		//System.out.println(html);
		
		if(html != null) {
			Document doc = Jsoup.parse(html);
			Element stranka = doc.getElementsByClass("page").first();
			
			//------------ Spracuje elementy --------------
			
			String nadpis = stranka.getElementsByClass("content-title").first().text();
			
			// Vyhodi vnutro z odsekov aby nic nebranilo najdeniu zhody
			String odsekyText = "";
			Elements odseky = stranka.getElementsByTag("p");
			for(Element odsek : odseky) {
				String odsekText = odsek.text();
				odsekyText = odsekyText + "<p>" + odsekText + "</p>";				
			}
			
			//String vystup = "<html><head><head><body>" + stranka.toString() + "</body></html>";
			String vystup = "<html><head><head><body>" 
							+ "<h1>" + nadpis + "</h1>" 
							+ odsekyText 
							// U - pridat este zdroje
							+ "</body></html>";

			//--------------------------------------------
			
			String cesta = "";
			
			try {
				cesta = (new File(clankyTXT)).getCanonicalPath();
				cesta = MyData.ineFormatyClankov + "/" + nazovSuboru + "_upravenyHTML.html";
				//System.out.println(cesta);
				if(Uprava_suborov.existujeSubor(cesta)) { // Pre istotu vymaze ak by existoval				
					Uprava_suborov.vymazSubor(cesta); 
				}
				Uprava_suborov.vytvorSubor(cesta);
				Uprava_suborov.pridajNaKoniecDoSuboru(cesta, vystup);
				
			} catch (IOException e) {					
				e.printStackTrace();
			}				
			
			//ulozClanok(text, cesta);
		}		
	}	
	
	//----------- Stahovanie TXT -----------
	// Ulozi cely HTML
	private void stiahniHTML(String html, String nazovSuboru) {
		String text = html;
		//System.out.println(text);
		
		if(text != null) {											
			String cesta = "";
			
			try {
				//cesta = (new File(clankyTXT)).getCanonicalPath();
				cesta = MyData.ineFormatyClankov + "/" + nazovSuboru + ".html";
				//System.out.println(cesta);
				if(Uprava_suborov.existujeSubor(cesta)) { // Pre istotu vymaze ak by existoval				
					Uprava_suborov.vymazSubor(cesta); 
				}
				Uprava_suborov.vytvorSubor(cesta);
				Uprava_suborov.pridajNaKoniecDoSuboru(cesta, text);
				
			} catch (Exception e) {					
				e.printStackTrace();
			}				
			
			//ulozClanok(text, cesta);
		}		
	}
	
	private void stiahniTXT(String html, String nazovSuboru) {
		String text = vratText(html);
		//System.out.println(text);
		
		if(text != null) {											
			String cesta = "";
			
			try {
				cesta = (new File(clankyTXT)).getCanonicalPath();
				cesta = cesta + "/" + nazovSuboru + ".txt";
				//System.out.println(cesta);
				if(Uprava_suborov.existujeSubor(cesta)) { // Pre istotu vymaze ak by existoval				
					Uprava_suborov.vymazSubor(cesta); 
				}
				Uprava_suborov.vytvorSubor(cesta);
				Uprava_suborov.pridajNaKoniecDoSuboru(cesta, text);
				
			} catch (IOException e) {					
				e.printStackTrace();
			}				
			
			//ulozClanok(text, cesta);
		}		
	}	
	
	// Stiahne cely clanok aj s HTML
	public String stiahniHTML(String url) {
		String text = null;
		
		try {
			// agent je potrebny kvoli javascriptu
			text = 
                Jsoup
                .connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                .get().html();                		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		return text;
	}
	
	// Priklad: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5966874/
	// Stiahne iba text clanku
	public String vratText(String html) {
		String text = null;
		
		try {
			// agent je potrebny kvoli javascriptu
			text = Jsoup.parse(html).text();					
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
		return text;
	}
	
}
