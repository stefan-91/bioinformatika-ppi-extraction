import java.util.Arrays;
import java.util.List;

import hladanie_interakcii.GRGT.GRGT;
import hladanie_interakcii.co_occurence_jednoduche.Co_occurence_1;
import hladanie_interakcii.co_occurence_tuke2018.co_occurence_tuke2018;
import netriedene.MyData;
import netriedene.Reset_prevadzky;
import predspracovanie_textov.Parsovanie_povodnych_korpusov;
import predspracovanie_textov.Predspracovanie_udajov_na_testovanie;
import predspracovanie_textov.Znackovanie_viet;
import stahovanie_clankov.StahovaniePMC;
import stahovanie_clankov.Vyhladanie_id_clankov;
import testovanie_metod.Otestovanie_metody;
import vytvaranie_vystupov.Korpus_textov;
import vytvaranie_vystupov.Pripravenie_na_web;
import vytvaranie_vystupov.Samostatne_xml;

public class corpora2020 {
	public static void main(String[] args) {	
		boolean bHelp = false;	
		
		// Na vyvoj
		args = new String[]{"vytvaranie_pravidiel", "oznackuj"};
		//args = new String[]{"test"};
		System.out.println(Arrays.toString(args));
		
		if (args.length == 1 && args[0].equals("test")) { 
			System.out.println("Program je spustitelny!");
		}
		else if(true) {					
			if (args.length >= 2) { //"2" lebo definujeme minimalne typ procesu a cinnosti 
				try {
					spracujVstup(args);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				bHelp = true;
			}
			if ((args.length > 0) && (args[0].equalsIgnoreCase("help"))) {
				bHelp = true;
			}
			
			if (bHelp) {
				System.out.println("Nespravne zadane parametre.");
				return;			
			}
		}
	}
	
	private static void spracujVstup(String[] args) {
		//---------------  Prevadzka ---------------
		if(args[0].equals("prevadzka")) {
			// Stahovanie clankov z webu			
			if(args[1].equals("stiahnutie") && args.length == 3) {
				String kategoria = args[2]; // Napr: "Cardiovascular diseases ppi"
				
				Vyhladanie_id_clankov vic = new Vyhladanie_id_clankov();
				List<String> idClankov = vic.najdiIdClankov("PMC", kategoria, 100000); // Pozn. naostro "PMC", "ppi", "100000"
				StahovaniePMC spmc = new StahovaniePMC();
				spmc.stiahniClanky(idClankov, kategoria);				
			}
			
			// Znackovanie entit PPI v textoch 
			if(args[1].equals("predspracovanie") && args.length == 2) {
				String korpus1 = "../korpus/originalne_texty/"; 
				String korpus2 = "../korpus/oznackovaneNazvy/";				
				Znackovanie_viet znackovanie = new Znackovanie_viet();			
				znackovanie.predpriprav(korpus1, korpus2, false, true, true, true); // Prevadzka							
			}			
			
			// Hladanie interakcii			
			if(args[1].equals("hladanie_ppi") && args.length == 3) {
				String korpus1 = "../korpus/originalne_texty/";
				String korpus2 = "../korpus/oznackovaneNazvy/";		
				String korpus3 = "../korpus/vysledky/";			
				
				if(args[2].equals("grgt")) {
					MyData.maxDlzkaVety = 50; // 150 bezi pomalsie, najde toho viac
					GRGT grgt = new GRGT();		
					grgt.najdiInterakcie(korpus1, korpus2, korpus3);					
				} else if(args[2].equals("co-occurence-jednoduche")) {
					Co_occurence_1 co1 = new Co_occurence_1();
					co1.najdiInterakcie(korpus1, korpus2, korpus3);					
				} else if(args[2].equals("co-occurence-2018")) {
					co_occurence_tuke2018 co1 = new co_occurence_tuke2018();
					co1.najdiInterakcie(korpus1, korpus2, korpus3);						
				}					
			}				
			
			// Tvorba vystupov
			if(args[1].equals("vystup") && args.length == 3) {
				if(args[2].equals("xml")) {
					String vstupAdr = "../korpus/vysledky"; 
					String vystupAdr = "../korpus/samostatne_xml";							
					Samostatne_xml sx = new Samostatne_xml();
					sx.vytvor(vstupAdr, vystupAdr);		
				} else if(args[2].equals("web")) {					
					Pripravenie_na_web pnw = new Pripravenie_na_web();
					pnw.start();				
				} else if(args[2].equals("cele_texty")) {
					String vstupAdr = "../korpus/vysledky"; 
					String vystupAdr = "../korpus_textov/";									 		 
					Korpus_textov kt = new Korpus_textov();
					kt.vytvor(vstupAdr, MyData.ineFormatyClankov, vystupAdr);					
				}
			}			
						
			// Vymaze vsekty ziskane data v procesu prevadzka
			if(args[1].equals("restore_default") && args.length == 2) {
				Reset_prevadzky	 rp = new Reset_prevadzky();
				rp.vymazPrevadzku();
			}				
		} 
		//--------------- Vytvaranie pravidiel ---------------
		else if(args[0].equals("vytvaranie_pravidiel")) {
			// Prevod korpusov na nas format
			if(args[1].equals("na_nas_format")) {
				Parsovanie_povodnych_korpusov ppk = new Parsovanie_povodnych_korpusov();				
				if(args[2].equals("hackenberg")) {
					ppk.spracujHuBerlin();
				} else if(args[2].equals("lll")) {
					ppk.spracujKorpusFormatu1("LLL");
				} else if(args[2].equals("aimed")) {
					ppk.spracujKorpusFormatu1("AIMed");
				} else if(args[2].equals("bioinfer")) {
					ppk.spracujKorpusFormatu1("BioInfer");
				} else if(args[2].equals("hprd50")) {
					ppk.spracujKorpusFormatu1("Hprd50");
				} else if(args[2].equals("iepa")) {
					ppk.spracujKorpusFormatu1("IEPA");	
				} else if(args[2].equals("picad")) {
					ppk.spracujKorpusFormatu2("PICAD");	
				} else if(args[2].equals("biocreative")) {
					ppk.spracujKorpusFormatu3("BioCreAtIvE_ppi");	
				}
			}
				
			// Oznackovanie materialov z ktorych vytvarame pravidla
			if(args[1].equals("oznackuj")) {
				Znackovanie_viet znackovanie = new Znackovanie_viet();					
				znackovanie.predspracujNaHladanieInterakcii("BioInfer", "korpus_BioInfer", false);
				znackovanie.predspracujNaHladanieInterakcii("AIMed", "korpus_AIMed", false);
				znackovanie.predspracujNaHladanieInterakcii("Hprd50", "korpus_Hprd50", false);
				znackovanie.predspracujNaHladanieInterakcii("IEPA", "korpus_IEPA", false);
				znackovanie.predspracujNaHladanieInterakcii("LLL", "korpus_LLL", false);	
				znackovanie.predspracujNaHladanieInterakcii("PICAD", "korpus_PICAD", false);
				znackovanie.predspracujNaHladanieInterakcii("BioCreAtIvE_ppi", "korpus_BioCreAtIvE_ppi", false);				
			}
			
			// Vytvorenie pravidiel vo vetach
			if(args[1].equals("pravidla") && args.length == 3) {
				MyData.maxDlzkaVety = 150;
				GRGT grgt = new GRGT();				
				if(args[2].equals("hackenberg")) {
					grgt.spracujVzory("korpus_hu_berlin", 0, 150); // (427 viet)
				} else if(args[2].equals("lll")) {
					grgt.spracujVzory("korpus_LLL", 0, 150); // (427 viet)
				} else if(args[2].equals("aimed")) {
					grgt.spracujVzory("korpus_AIMed", 0, 100); // (9700 viet)
				} else if(args[2].equals("bioinfer")) {
					grgt.spracujVzory("korpus_BioInfer", 0, 100); // (2500 viet)
				} else if(args[2].equals("hprd50")) {
					grgt.spracujVzory("korpus_Hprd50", 0, 150); // (179 viet)
				} else if(args[2].equals("iepa")) {
					grgt.spracujVzory("korpus_IEPA", 0, 100);  // (777 viet)
				} else if(args[2].equals("picad")) {
					grgt.spracujVzory("korpus_PICAD", 0, 100); // (2500 viet)
				} else if(args[2].equals("biocreative")) {
					grgt.spracujVzory("korpus_BioCreAtIvE_ppi", 0, 150); // (2538 viet)	
				}
			}
			
		}
		//--------------- Vyhodnotenie metody ---------------
		else if(args[0].equals("vyhodnotnie_metody")) {
			// Prevod korpusov na nas format
			if(args[1].equals("na_nas_format")) {
				Parsovanie_povodnych_korpusov ppk = new Parsovanie_povodnych_korpusov();				
				if(args[2].equals("hackenberg")) {
					ppk.spracujHuBerlin();
				} else if(args[2].equals("lll")) {
					ppk.spracujKorpusFormatu1("LLL");
				} else if(args[2].equals("aimed")) {
					ppk.spracujKorpusFormatu1("AIMed");
				} else if(args[2].equals("bioinfer")) {
					ppk.spracujKorpusFormatu1("BioInfer");
				} else if(args[2].equals("hprd50")) {
					ppk.spracujKorpusFormatu1("Hprd50");
				} else if(args[2].equals("iepa")) {
					ppk.spracujKorpusFormatu1("IEPA");	
				} else if(args[2].equals("picad")) {
					ppk.spracujKorpusFormatu2("PICAD");	
				} else if(args[2].equals("biocreative")) {
					ppk.spracujKorpusFormatu3("BioCreAtIvE_ppi");	
				}
			}			
			
			
			// Prevod na format s vysledkami									
			// Odstranenie znaciek z viet
			if(args[1].equals("predspracovanie")) {
				Predspracovanie_udajov_na_testovanie punt = new Predspracovanie_udajov_na_testovanie();
				punt.predspracujHuBerlin();		
				punt.predspracujKorpusy("AIMed", "korpus_AIMed", -1);
				punt.predspracujKorpusy("BioInfer", "korpus_BioInfer", -1);
				punt.predspracujKorpusy("IEPA", "korpus_IEPA", -1);		
				punt.predspracujKorpusy("LLL", "korpus_LLL", -1); // Pozn. Naostro 150
				punt.predspracujKorpusy("Hprd50", "korpus_Hprd50", -1);
				punt.predspracujKorpusy("PICAD", "korpus_PICAD", -1);
				System.out.println("Ide predspracovanie");
			}

			// Spustenie metody na hladanie PPI nad testovacimi datami
			// Porovnanie vysledkov a vypocianie F-skore 
			if(args[1].equals("vyhodnot")) {
				String metoda = args[2]; //moznosti: grgt, co1, co2018
				Otestovanie_metody om = new Otestovanie_metody();	
				
				if(args[3].equals("lll")) {
					om.otestujMetodu("korpus_LLL", metoda);
				} else if(args[3].equals("aimed")) {
					om.otestujMetodu("korpus_AIMed", metoda); 
				} else if(args[3].equals("bioinfer")) {
					om.otestujMetodu("korpus_BioInfer", metoda);	
				} else if(args[3].equals("hprd50")) {
					om.otestujMetodu("korpus_Hprd50", metoda); 
				} else if(args[3].equals("iepa")) {
					om.otestujMetodu("korpus_IEPA", metoda); 
				} else if(args[3].equals("picad")) {
					om.otestujMetodu("korpus_PICAD", metoda);
				}
			}
		} 				
	}			
}












































