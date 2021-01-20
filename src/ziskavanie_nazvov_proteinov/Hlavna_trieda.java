package ziskavanie_nazvov_proteinov;

import java.util.Set;

public class Hlavna_trieda {

	public static void main(String[] args) {
		/*
		// Ziskavanie proteinov
		Parsovanie_xml px = new Parsovanie_xml();
		px.spracujUniprot();
		*/
		
		Nacitanie_ziskanych_nazvov.nacitajProteiny();
		
		/*
		// Pozn. Nepouziva sa
		Parsovanie_csv pc = new Parsovanie_csv();
		Set<String> nazvy = pc.vyberStlpec("../zoznamy_proteinov/proteinatlas.tsv", "\t", 2);
		pc.zapisStlpec(nazvy, "../zoznamy_proteinov/nazvy-proteinatlas.txt");
		*/
		
	}

}
