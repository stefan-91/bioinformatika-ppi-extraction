package stahovanie_clankov;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * Hlada clanky na PubMed
 * 
 * Popis API:
 * 		https://www.ncbi.nlm.nih.gov/books/NBK25500/
 * 
 * ULOHY
 * - Urobit stahovanie z PMC: PDF, URL
 * - Urobit stahovanie z pubmed
 * 
 *
 */
public class Vyhladanie_id_clankov {

	public List<String> najdiIdClankov(String databaza, String term, int pocet) {				
		String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db="+databaza+"&term="+term+"&retmax="+pocet+"";

		List<String> idClankov = nacitajId(url);
				
		//System.out.println(idClankov);
		
		return idClankov;
	}
	
	private List<String> nacitajId(String url) {
		List<String> idClankov = new LinkedList<>();

		try {
			Document doc = Jsoup.connect(url).get();			
			//System.out.println(doc.toString());
			Element eList = doc.getElementsByTag("idList").first();
			Elements idsEl = eList.getElementsByTag("Id");
			
			for(Element e : idsEl) {
				idClankov.add(e.text());
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return idClankov;
	}
	
	
	
}
