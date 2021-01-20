package netriedene;

import java.util.HashSet;
import java.util.Set;

import uprava_suborov.Uprava_suborov;

public class Nacitanie_udajov_zo_suboru {

	/*
	public static Set<String> nacitajProteiny() {
		Set<String> proteiny = new HashSet<>();
		
		//String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_proteinov/nas_format/uniprot-organism__Homo+sapiens+(Human)+[9606]_.txt");
		String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_proteinov/nas_format/nazvy-proteinatlas.txt");
		
		String[] polozky = protStr.split("\n");
		for(String s : polozky) {
			s = s.trim();
			if(s.length() == 0) continue;
			s = s.toLowerCase();
			proteiny.add(s);
		}			
		
		// Odoberie stopwords
		
		
		return proteiny;
	}
	*/
	
	public static Set<String> nacitajNazvyInterakcii() {
		Set<String> interakcie = new HashSet<>();
		
		// Nacita zoznam interakcii		
		String interakcieStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_interakcii/dohromady.txt");
		String[] polozkyInter = interakcieStr.split("\n");
		for(String s : polozkyInter) {
			s = s.trim();
			s = s.toLowerCase();
			if(s.length() == 0) continue;
			interakcie.add(s);
		}		
		
		System.out.println("Nacitali sme " + interakcie.size() + " nazvov pre interakciu, vratane vsetkych ich tvarov.");
		
		return interakcie;
	}
	
}


































































