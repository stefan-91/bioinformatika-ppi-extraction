package predspracovanie_textov;

public class Hlavna_trieda {

	public static void main(String[] args) {
		//---------- Prevadzka -----------
		
		String korpus1 = "../korpus/originalne_texty/"; 
		String korpus2 = "../korpus/oznackovaneNazvy/";
		
		Znackovanie_viet znackovanie = new Znackovanie_viet();			
		//znackovanie.predpriprav(korpus1, korpus2, false, true, true, true); // Prevadzka
		znackovanie.spocitajSlova(korpus1);
		
		//---------- Spracovanie povodnych korpusov do nasho formatu --------
		/*
		Parsovanie_povodnych_korpusov ppk = new Parsovanie_povodnych_korpusov();						
		//ppk.spracujHuBerlin();
		ppk.spracujKorpusFormatu1("LLL");
		ppk.spracujKorpusFormatu1("AIMed");
		ppk.spracujKorpusFormatu1("BioInfer");
		ppk.spracujKorpusFormatu1("Hprd50");				
		ppk.spracujKorpusFormatu1("IEPA");
		ppk.spracujKorpusFormatu2("PICAD");
		ppk.spracujKorpusFormatu3("BioCreAtIvE_ppi");
		*/
		//------------ Predspracovanie korpusov a hladanie pravidiel -------------
		/*
		//String korpus1 = "../korpus/originalne_texty/"; 
		//String korpus2 = "../korpus/oznackovaneNazvy/";
		
		Znackovanie_viet znackovanie = new Znackovanie_viet();			
		//znackovanie.predpriprav(korpus1, korpus2, false); // Prevadzka		
		znackovanie.predspracujNaHladanieInterakcii("BioInfer", "korpus_BioInfer", false);
		znackovanie.predspracujNaHladanieInterakcii("AIMed", "korpus_AIMed", false);
		znackovanie.predspracujNaHladanieInterakcii("Hprd50", "korpus_Hprd50", false);
		znackovanie.predspracujNaHladanieInterakcii("IEPA", "korpus_IEPA", false);
		znackovanie.predspracujNaHladanieInterakcii("LLL", "korpus_LLL", false);	
		znackovanie.predspracujNaHladanieInterakcii("PICAD", "korpus_PICAD", false);
		//znackovanie.predspracujNaHladanieInterakcii("BioCreAtIvE_ppi", "korpus_BioCreAtIvE_ppi", true);	
		*/
		//------------ Ine ------------------
		/*
		Parsovanie_povodnych_korpusov ppk = new Parsovanie_povodnych_korpusov();
		ppk.extrahujNazvyInterakcii();
		*/
		//---------- Testy ----------
		/*
		Znackovanie_viet zv  = new Znackovanie_viet();
		//zv.test1();
		zv.testOznackovaniaVety();
		*/
		
	}

}


















































