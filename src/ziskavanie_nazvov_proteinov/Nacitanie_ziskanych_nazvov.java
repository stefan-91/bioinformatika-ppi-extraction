package ziskavanie_nazvov_proteinov;

import java.util.HashSet;
import java.util.Set;

import uprava_suborov.Uprava_suborov;

public class Nacitanie_ziskanych_nazvov {

	public static Set<String> nacitajProteiny() {
		Set<String> proteiny = new HashSet<>();
		String root = "../materialy/nazvy_proteinov/nas_format/";
		proteiny.addAll(nacitajProteinyDokument(root + "recommendedFull" + ".txt"));
		proteiny.addAll(nacitajProteinyDokument(root + "recommendedShort" + ".txt"));
		proteiny.addAll(nacitajProteinyDokument(root + "alternativeFull" + ".txt"));
		proteiny.addAll(nacitajProteinyDokument(root + "alternativeShort" + ".txt"));
		proteiny.addAll(nacitajProteinyDokument(root + "submittedFull" + ".txt"));
		proteiny.addAll(nacitajProteinyDokument(root + "submittedShort" + ".txt"));
		//proteiny.addAll(nacitajProteinyDokument(root + genes + ".txt")); // geny neriesime
		
		System.out.println("Dohromady nacitalo " + proteiny.size() + " nazov proteinov.");
						
		return proteiny;
	}
	
	// Vynecha tie, ktore su rovnake ako stopwords
	public static Set<String> nacitajProteinyDokument(String nazovSub) {
		Set<String> proteiny = new HashSet<>();
		// Nacita zoznam proteinov		
		String protStr = Uprava_suborov.vratObsahSuboru(nazovSub);
		//String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_proteinov/nas_format/nazvy-proteinatlas.txt");
		
		String[] polozky = protStr.split("\n");
		for(String s : polozky) {
			String povodne = s;
			// Vynecha prazdne riadky
			s = s.trim();
			if(s.length() == 0) continue;
			s = s.toLowerCase();
						
			/*
			// Pozn. nepouziva sa
			// Vynecha viacslovne nazvy
			String[] slova = s.split(" ");
			if(slova.length > 1) {
				//System.out.println(s);
				//proteiny.add(s);
				continue; 
			}
			*/
			
			// Viacslovne nazvy prepoji cez pomlcku
			s = s.replace(" ", "-");
			
			//System.out.println(s);
			proteiny.add(s);
		}
		
		// Nacita stopwords
		String stopwordsAdr = "../materialy/stopwords/subory/";
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(stopwordsAdr);
		Set<String> stopWordsS = new HashSet<>();
		
		for(String subor : subory) {
			String text = Uprava_suborov.vratObsahSuboru(stopwordsAdr + subor);
			polozky = text.split("\n");
			for(String s : polozky) {
				s = s.trim();
				if(s.length() == 0) continue;
				s = s.toLowerCase();
				stopWordsS.add(s);
			}				
		}

		
		// Vypise co vyhodilo
		//for(String slovo : stopWordsS) {
		//	if(proteiny.contains(slovo)) {
		//		System.out.println("Vyhodilo " + slovo);
		//	}
		//}
	
		// Vyhodi stopword z mnoziny nazvov proteinov
		int pocet1 = proteiny.size();
		proteiny.removeAll(stopWordsS);
		int pocet2 = proteiny.size();
		System.out.println("Vyhodilo: " + (pocet1 - pocet2) + "/" + pocet1 + " nazvov proteinov. ");

		return proteiny;
	}
	
}


















































