package predspracovanie_textov;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;
import ziskavanie_nazvov_proteinov.Nacitanie_ziskanych_nazvov;

/**
 * 
 * Prida znacky ku proteinom a interakciam
 * 
 *
 */
public class Znackovanie_viet {
	private Set<String> proteiny = new TreeSet<>();
	private Set<String> interakcie = new HashSet<>();
	private String jednotnaZnackaProteinu = "protein";
	
	public Znackovanie_viet() {
		// Nacita zoznam proteinov
		/*
		//String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_proteinov/nas_format/uniprot-organism__Homo+sapiens+(Human)+[9606]_.txt");
		String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_proteinov/nas_format/nazvy-proteinatlas.txt");
		
		String[] polozky = protStr.split("\n");
		for(String s : polozky) {
			s = s.trim();
			if(s.length() == 0) continue;
			s = s.toLowerCase();
			proteiny.add(s);
		}
		*/		
		proteiny = Nacitanie_ziskanych_nazvov.nacitajProteiny();
		
		// Nacita zoznam interakcii		
		String interakcieStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_interakcii/dohromady.txt");
		String[] polozkyInter = interakcieStr.split("\n");
		for(String s : polozkyInter) {
			s = s.trim();
			s = s.toLowerCase();
			if(s.length() == 0) continue;
			interakcie.add(s);
		}	
	}

	// Pouziva sa v testovani
	public void pridajProtein(String protein) {
		protein = protein.toLowerCase();
		protein = protein.trim();
		proteiny.add(protein);
	}
	
	/**
	 * Pripravi korpus na hladanie interakcii:
	 * 1. Najde a oznackuje nazvy interakcii ak nie su
	 * 2. nazvy proteinov prepise na jednodnu znacku	 
	 * 
	 * Vstup: Proteiny uz musia byt oznackovane.
	 *
	 */
	public void predspracujNaHladanieInterakcii(String korpusAdr, String vystupAdr, boolean maInterakcie) {
		String vstupPoloformat = "../materialy/ine_korpusy/"+korpusAdr+"/nas_format/";
		String vystup = "../materialy/vzory_interakcii/oznackovane_vety/"+vystupAdr+"/";	
		//String naseVysledkyAdr = "../materialy/testovacie_udaje/"+korpusAdresar+"/nase_vysledky/";
		//String testovacieVysledkyAdr = "../materialy/testovacie_udaje/"+korpusAdresar+"/testovacie_vysledky/";
				
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(vstupPoloformat);		
		//System.out.println(subory);		

		for(String sub : subory) {				
			Uprava_suborov.vytvorSubor(vystup+"/"+sub);
			
			String obsah = Uprava_suborov.vratObsahSuboru(vstupPoloformat+"/"+sub);			
			String[] vety = obsah.split("\n");			
			
			for(String veta : vety) {
				String vetaZ = veta;
				// 1. Oznackuje interakcie ak treba
				if(maInterakcie == false) { // Ked nema interakcie, musia sa pridat
					vetaZ = spracujVetu(vetaZ, false, true, false);
				}
				
				// 2. Mena proetinov prevedie na jednotny nazov
				//vetaZ = naKanonickyProtein(vetaZ); // Pozn. Kvoli optimalizacii to robi az v samotnej metode 
				
				Uprava_suborov.pridajNaKoniecDoSuboru(vystup+"/"+sub, vetaZ);
			}
		}
		System.out.println("Koniec predspracovania korpusu " + korpusAdr);
	}	
	
	// Vsetky nazvy proteinov (uz oznackovanych) nahradi jednotnym slovom (kvoli Stanfordskemu parseru)
	private String naKanonickyProtein(String veta) {
		Set<String> proteiny = Spracovanie_textu.vratObsahZnaciek(veta, "<protein>", "</protein>");
		
		for(String protein : proteiny) {
			veta = veta.replace("<protein>"+protein+"</protein>", "<protein>"+jednotnaZnackaProteinu+"</protein>"); // pracuje aj so znackami aby nahradilo iba to co je v znackach (moze byt aj neoznackovany protein)
		}
		
		return veta;
	} 
	
	
	public void spocitajSlova(String vstupnyAdr) {
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(vstupnyAdr);		
		int pocetSlov = 0;
		int pocetViet = 0;
		int pocetZnakov = 0;
		
		for(String sub : subory) {					
			String obsah = Uprava_suborov.vratObsahSuboru(vstupnyAdr + "/" + sub);
			//System.out.println(obsah);
			
			// Kazdu vetu da na osobitny riadok
			obsah = obsah.replace(".", ".\n");			
			String[] vety = obsah.split("\n");
			for(String veta : vety) {
				pocetViet++;
				String[] slova = veta.split(" ");
				pocetSlov = pocetSlov + slova.length;
				
				for(String slovo : slova) {
					pocetZnakov = pocetZnakov + slovo.length();
				}
				
			}						
		}
		
		System.out.println("Pocet slov: " + pocetSlov);
		System.out.println("Pocet viet: " + pocetViet);
		System.out.println("Pocet znakov: " + pocetZnakov);
	}
	
	/**
	 * Oznackuje vsetky texty vo vybranom adresari. 
	 * 
	 * @param vstupnyAdr
	 * @param vystupnyAdr
	 * @param zapisujeVsetko
	 * 				true ak ma zapisat vsetky vety bez ohladu co je tam 
	 * 				false ak zapisuje iba perspektivne vety (napr. vetu bez proteinov nezapise) - kvoli optimalizacii
	 */
	public void predpriprav(String vstupnyAdr, String vystupnyAdr, boolean zapisujeVsetko, boolean prot, boolean inter, boolean optimalizovane) {		
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(vstupnyAdr);		
		//System.out.println(subory);

		int vetaNemaInterakciu = 0;
		int pocitadlo = 0; 
		int pocitadloViet = 0;
		for(String sub : subory) {
			pocitadlo++;
			boolean nasloNieco = false;
			Uprava_suborov.vytvorSubor(vystupnyAdr + sub); // Vytvori cielovy subor			
			String obsah = Uprava_suborov.vratObsahSuboru(vstupnyAdr + "/" + sub);
			//System.out.println(obsah);
			
			// Kazdu vetu da na osobitny riadok
			obsah = obsah.replace(".", ".\n");			
			String[] vety = obsah.split("\n");
			
			if(pocitadlo % 5 == 0) {
				System.out.println("Predpriprava textov: " + pocitadlo+"/"+subory.size());
			}			
			
			// Pozn. optimalizovana verzia
			String novyObsah = "";
			for(String veta : vety) { // Prechadza vetu po vete
				pocitadloViet++;

				if(pocitadloViet % 10 == 0) {
					//System.out.println("Predpriprava textov: " + pocitadloViet + "/" + vety.length + ", " + pocitadlo+"/"+subory.size());
				}

				String vetaPom = spracujVetu(veta, prot, inter, optimalizovane); // Pridava iba proteiny
				
				// Opravi chybu
				vetaPom = vetaPom.replace("<<protein>protein</protein>>", "<protein>");
				vetaPom = vetaPom.replace("</<protein>protein</protein>>", "</protein>");
				
				int poctyInter = vratPocetVyskytov(vetaPom, "<interakcia>");
				int poctyProt = vratPocetVyskytov(vetaPom, "<protein>");
				
				// Toto sme pouzili na dohladanie interakcii
				if(poctyInter == 0) {
					//System.out.println(veta);
					vetaNemaInterakciu++;
					/*
					if(pocitadloV > 0 && pocitadloV % 1000 == 0) {
						break;
					}
					*/
					
				}				
				
				// Zapise znacky
				if(poctyProt >= 2 && poctyInter >= 1) { // Iba vtedy zapisuje do suboru
					nasloNieco = true;
					novyObsah = novyObsah + vetaPom + "\n";
					
				} else { // ak nenaslo nic
					if(zapisujeVsetko == true) {
						nasloNieco = true;
						//System.out.println("Nenaslo nic: " + veta);
						novyObsah = novyObsah + vetaPom + "\n";	
					}
				}					
				
				if(pocitadloViet > 0 && pocitadloViet % 300 == 0) {
					Uprava_suborov.pridajNaKoniecDoSuboru(vystupnyAdr + sub, novyObsah);					
					novyObsah = "";
				}
			}
			

			/*
			// Dlho trva, museli sme optimalizovat
			// Prida znacku k nazvom proteinov
			for(String protein : proteiny) {
				if(obsah.contains(protein)) {
					//System.out.println("naslo protein: " + protein);
					String znacka = "@protein_B_@ " + protein + " @protein_A_@";
					obsah = obsah.replace(protein, znacka);
				}
			}
		   */
			
			// Zapise zvysok
			if(novyObsah.length() > 0) {				
				Uprava_suborov.pridajNaKoniecDoSuboru(vystupnyAdr + sub, novyObsah);							
			}

			// Pozn. Iba v pripade optimalizacie
			if(nasloNieco == false) {
				Uprava_suborov.vymazSubor(vystupnyAdr + sub);
			}
			
			//break;
			

		}
		
		System.out.println("Pocet viet, ktore nemaju slovo pre interakciu: " + vetaNemaInterakciu + "/" + pocitadloViet);
		System.out.println("Spracovali sme " + pocitadloViet + " viet ");
		
	}
	
private static int vratPocetVyskytov(String veta, String retazec) {
	String str = veta;
	String findStr = retazec;
	int lastIndex = 0;
	int count = 0;

	while(lastIndex != -1){

	    lastIndex = str.indexOf(findStr,lastIndex);

	    if(lastIndex != -1){
	        count ++;
	        lastIndex += findStr.length();
	    }
	}
	//System.out.println(count);
	return count;
}	

	public void test1() {
		String veta = "sigmaF is controlled by a regulatory cascade involving an anti-sigma factor, SpoIIAB, an anti-anti-sigma factor, SpoIIAA, and a membrane-bound phosphatase, SpoIIE, which converts the inactive, phosphorylated form of SpoIIAA back to the active form.";
		Set<String> proteiny = new HashSet<>();
		proteiny.add("SpoIIAA"); proteiny.add("sigmaF");  
		Set<String> interakcie = new HashSet<>();
		interakcie.add("controlled");
		
		String vystup = vlastneOznackovanie(veta, proteiny, interakcie, false);
		System.out.println(vystup);		
		
	}

	public void testOznackovaniaVety() {
		//String veta = "In this mutant, expression of the spoIIG gene, whose transcription depends on both sigma(A) and the phosphorylated Spo0A protein, Spo0A~P, a major transcription factor during early stages of sporulation, was greatly reduced at 43 degrees C.";
		//String veta = "The results indicate that <protein>PhoP~P</protein> is sufficient to repress the transcription of the <protein>tagA</protein> and tagD promoters and also to activate the transcription of the tuaA promoter.";
		String veta = "A low concentration of GerE activated cotB transcription by final sigma(K) RNA polymerase, whereas a higher concentration was needed to activate transcription of cotX or cotC.";
				
		Set<String> inter = new HashSet<>();
		//inter.add("expression");inter.add("results");inter.add("plays");inter.add("control");
		//inter.add("coupling");inter.add("activation");inter.add("repress");
		inter.add("activate");
		//inter.add("activated");
		
		Set<String> prot = new HashSet<>();
		//prot.add("sigma(A)"); prot.add("spoIIG");
		//prot.add("SpoIIE"); prot.add("sigmaF");

		String oznackovana = Znackovanie_viet.vlastneOznackovanie(veta, prot, inter, false);
		System.out.println("Povodna: " + veta);
		System.out.println("oznackovana: " + oznackovana);					
	}
	
	// Znackuje iba s vybranymi proteinymi a interakciami
	public static String vlastneOznackovanieMet1(String veta, Set<String> proteinyOrig, Set<String> interakcieOrig) {
		String vetaPovodna = veta; // Lebo neskor preposujeme "veta"

		//------------ Vsetko zmensi -------------
		veta = veta.toLowerCase().trim();
		
		// Zmensi proteiny a interakcie
		Map<String, String> maleVelke = new HashMap<>();
		Set<String> proteiny = new HashSet<>();
		for(String protein : proteinyOrig) {			
			String male = protein.toLowerCase().trim();
			maleVelke.put(male, protein);
			proteiny.add(male);
		}  
		
		Set<String> interakcie = new HashSet<>();
		for(String interakcia : interakcieOrig) {
			String male = interakcia.toLowerCase().trim();
			maleVelke.put(male, interakcia);
			interakcie.add(male);
		} 		
		
		//--------- Vrati pozicie slov a prida znacky -----------
		// Proteiny
		for(String slovo : proteiny) {
			String velke = maleVelke.get(slovo);
			Set<Integer> pozicie = Spracovanie_textu.vratPozicie(vetaPovodna, velke);
			//System.out.println(slovo + " - " + pozicie + ", veta: " + veta);			
			vetaPovodna = pridajZnacky(vetaPovodna, pozicie, velke, "protein"); 
		}
		
		// Interakcie
		for(String slovo : interakcie) {
			String velke = maleVelke.get(slovo);
			Set<Integer> pozicie = Spracovanie_textu.vratPozicie(vetaPovodna, velke);
			//System.out.println(slovo + " - " + pozicie + ", veta: " + veta);
			vetaPovodna = pridajZnacky(vetaPovodna, pozicie, velke, "interakcia"); 
		}				
		//System.out.println("Oznackovana veta: " + vetaPovodna);

		return vetaPovodna;
	}			
	
	/**
	 * Prida znacky na urcene pozicie
	 * Dba aby sa nerozdelovalo vo vnutri slov typu "<interakcia>depend</interakcia>ent" namiesto "<interakcia>dependent</interakcia>" 
	 * 
	 * @param veta
	 * @param pozicie
	 * @param slovo
	 * 			Slovo, ktore obklopime tagom
	 * @return
	 */
	private static String pridajZnacky(String veta, Set<Integer> pozicie, String slovo, String tag) {
	      Integer[] myArray = new Integer[pozicie.size()];
	      pozicie.toArray(myArray);
	      
	      Arrays.sort(myArray, Collections.reverseOrder()); // Usporiada zostupne
	      //System.out.println(Arrays.toString(myArray));
	      
	      for(Integer pozicia1 : myArray) {	    	  
	    	  Integer pozicia2 = pozicia1 + slovo.length();
	    	  
	    	  // Kontrola aby nevpisalo do vnutra slova
	    	  String znak1 = "";
	    	  if(pozicia1 == 0) znak1 = " "; // sme na zaciatku
	    	  else znak1 = veta.charAt(pozicia1 - 1) + "";
	    	  
	    	  String znak2 = "";	    	  
	    	  if(pozicia2 == veta.length()) znak2 = " "; // sme na konci
	    	  else znak2 = veta.charAt(pozicia2) + "";
	    			  
	    	  // Este mame oddelovace, ktore su ekvivalentne medzeram
	    	  String[] oddelovace = Spracovanie_textu.oddelovace;
	    	  for(String oddelovac : oddelovace) {
	    		  if(znak1.equals(oddelovac)) znak1 = " ";
	    		  if(znak2.equals(oddelovac)) znak2 = " "; 
	    	  }
	    	  	    	  	    	 
	    	  
	    	  if(znak1.equals(" ") && znak2.equals(" ")) { // sme niekde vo vnutri slova
	    		  //System.out.println("Vo vnutri slova: |"+znak1+"|"+znak2+"|");
		    	  // Samotne pridanie znaciek
		    	  veta = veta.substring(0, pozicia2) + "</" + tag + ">" + veta.substring(pozicia2, veta.length());
		    	  veta = veta.substring(0, pozicia1) + "<" + tag + ">" + veta.substring(pozicia1, veta.length());	    	  		      
	    	  }
	      }
	      //System.out.println("Oznackovana veta: " + veta);
	      
	      return veta;
	}
	
	
	public static String vlastneOznackovanie(String veta, Set<String> proteinyOrig, Set<String> interakcieOrig, boolean optimalizovanaVer) {
		String vetaS = "";
		if(optimalizovanaVer == true) vetaS = vlastneOznackovanieMet2(veta, proteinyOrig, interakcieOrig); // s rozdelenim vety podla medzier
		else vetaS = vlastneOznackovanieMet1(veta, proteinyOrig, interakcieOrig); // s poziciami
		/*
		if(!veta1.equals(veta2)) {
			
			System.out.println("Nezhoda znackovania viet");
			System.out.println("p         : " + veta);
			System.out.println("pozicie   : " + veta1);
			System.out.println("rozdelenie: " + veta2);			
		}
		*/
		
		return vetaS;
	}	
	
	// Znackuje iba s vybranymi proteinymi a interakciami
	public static String vlastneOznackovanieMet2(String veta, Set<String> proteinyOrig, Set<String> interakcieOrig) {
		String vetaPovodna = veta; // Lebo neskor prepisujeme premennu "veta"

		// Zmensi proteiny a interakcie
			// Pozn. Uz sa ocakava ze su zmensene
		Set<String> proteiny = proteinyOrig;
		/*
		Set<String> proteiny = new HashSet<>();
		for(String protein : proteinyOrig) {
			proteiny.add(protein.toLowerCase().trim());
		}  
		*/
		
		Set<String> interakcie = new HashSet<>();
		for(String interakcia : interakcieOrig) {
			interakcie.add(interakcia.toLowerCase().trim());
		} 		
		
		// Pouzivane zoznam lebo je dolezity pocet poloziek (aj rovnakych)
		Set<String> najdeneProteiny = new HashSet<>();
		Set<String> najdeneInterakcie = new HashSet<>();		
		
		//--------- Rozdeli vetu ------------
		String vetaUpr = veta;
		// Osetruje pripad ked je nieco v zatvorke: (SpoIIAA-P)
		String[] oddelovace = Spracovanie_textu.oddelovace;
		
		
		for(String oddelovac : oddelovace) {
			vetaUpr = vetaUpr.replace(oddelovac, "|");
		}				
		String[] slova = vetaUpr.split("\\|");

		//-----------------------------------
		
		for(String slovo : slova) {
			slovo = slovo.trim();
			
			Map<String, String> povodneM = new HashMap<>();
			Set<String> slovaS = new HashSet<>();
		
			povodneM.put(slovo.toLowerCase(), slovo);
			slovaS.add(slovo.toLowerCase());
			
			// Osetruje pripad ked je predtym pomlcka: "protein -dependent protein"
			if(slovo.startsWith("-") && slovo.length() > 1) {
				slovo = slovo.substring(1);
			}
			
			// Osetruje dalsi pripad s pomlckou: "protein-dependent protein" (bez medzery) 
			String[] slovaArr = slovo.split("-");										
			for(String slov : slovaArr) {
				if(slov.trim().length() == 0) continue;
				povodneM.put(slov.toLowerCase(), slov);
				slovaS.add(slov.toLowerCase());
			}

			for(String slovo1 : slovaS) {		
				String povodne = povodneM.get(slovo1); // nezmensene
				
				// Test na vyskyt proteinov
				if(proteiny.contains(slovo1)) { // optimalizovane hlada v HashSet
					najdeneProteiny.add(povodne);					
				}
				
				// Test na vyskyt interakcii
				if(interakcie.contains(slovo1)) { // optimalizovane hlada v HashSet
					najdeneInterakcie.add(povodne);					
				}						
			}	
		}

		String vetaPom = vetaPovodna;
		
		// Prida znacky k najdenym proteinom
		//System.out.println("Najdene proteiny: " + najdeneProteiny);
		for(String protein : najdeneProteiny) {
			String znacka = "<protein>" + protein + "</protein>";
			vetaPom = vetaPom.replace(protein, znacka);					
									
		}			
		
		// Prida znacky k najdenym interakciam
		for(String interakcia : najdeneInterakcie) {
			String znacka = "<interakcia>" + interakcia + "</interakcia>";
			vetaPom = vetaPom.replace(interakcia, znacka);											
		}			

		return vetaPom;
	}
	
	
	// Znackuje so vsetkymi proteinymi a interakciami
	public String spracujVetu(String veta, boolean znackujeProteiny, boolean znackujeInterakcie, boolean optimalizovane) {
		Set<String> proteinyPom = new HashSet<>();
		Set<String> interakciePom = new HashSet<>();		

		// Ak je parameter FALSE, ostanu mnoziny prazdne, takze ani nic neoznaci
		if(znackujeProteiny == true) proteinyPom = proteiny;
		if(znackujeInterakcie == true) interakciePom = interakcie;	

		String vysledok = vlastneOznackovanie(veta, proteinyPom, interakciePom, optimalizovane);
		return vysledok;
	}	
	
	/**
	 * 
	 * @param vstupnyAdr
	 * @param vystupnyAdr
	 * @param zapisujeVsetko
	 * 				true ak ma zapisat vsetky vety bez ohladu ci je tam 
	 * 				false ak zapisuje iba perspektivne vety (napr. vetu bez proteinov nezapise) - iba optimalizacia
	 */
	public void predpripravStara(String vstupnyAdr, String vystupnyAdr, boolean zapisujeVsetko) {
		
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(vstupnyAdr);		
		//System.out.println(subory);

		int pocitadlo = 0; 
		int pocitadloViet = 0;
		for(String sub : subory) {
			boolean nasloNieco = false;
			Uprava_suborov.vytvorSubor(vystupnyAdr + sub); // Vytvori cielovy subor			
			String obsah = Uprava_suborov.vratObsahSuboru(vstupnyAdr + "/" + sub);
			//System.out.println(obsah);
			
			// Kazdu vetu da na osobitny riadok
			obsah = obsah.replace(".", ".\n");			
			String[] vety = obsah.split("\n");
			
			// Pozn. optimalizovana verzia
			String novyObsah = "";
			for(String veta : vety) { // Prechadza vetu po vete
				pocitadloViet++;
				
				// Pouzivane zoznam lebo je dolezity pocet poloziek (aj rovnakych)
				Set<String> najdeneProteiny = new HashSet<>();
				Set<String> najdeneInterakcie = new HashSet<>();
				int poctyProt = 0;
				int poctyInter = 0;				
				
				String[] slova = veta.split(" ");
				for(String slovo : slova) {
					slovo = slovo.trim();
					
					// Test na vyskyt proteinov
					if(proteiny.contains(slovo)) { // optimalizovane hlada v HashSet
						najdeneProteiny.add(slovo);
						poctyProt++;
					}
					
					// Test na vyskyt interakcii
					if(interakcie.contains(slovo)) { // optimalizovane hlada v HashSet
						najdeneInterakcie.add(slovo);
						poctyInter++;
					}					
				}
				
				// Zapise znacky
				if(poctyProt >= 2 && poctyInter >= 1) { // Iba vtedy zapisuje do suboru
					nasloNieco = true;
					String vetaPom = veta;

					// Prida znacky k najdenym proteinom
					for(String protein : najdeneProteiny) {
						String znacka = "<protein>" + protein + "</protein>";
						vetaPom = vetaPom.replace(protein, znacka);					
												
					}
					
					// Prida znacky k najdenym interakciam
					for(String interakcia : najdeneInterakcie) {
						String znacka = "<interakcia>" + interakcia + "</interakcia>";
						vetaPom = vetaPom.replace(interakcia, znacka);											
					}					
					
					// Zapise oznackovanu vetu
					novyObsah = novyObsah + vetaPom + "\n";										
				} else { // ak nenaslo nic
					if(zapisujeVsetko == true) {
						nasloNieco = true;
						System.out.println("Nenaslo nic: " + veta);
						novyObsah = novyObsah + veta + "\n";	
					}
				}
				
				if(pocitadloViet > 0 && pocitadloViet % 300 == 0) {
					Uprava_suborov.pridajNaKoniecDoSuboru(vystupnyAdr + sub, novyObsah);					
					novyObsah = "";
				}
			}
			

			/*
			// Dlho trva, museli sme optimalizovat
			// Prida znacku k nazvom proteinov
			for(String protein : proteiny) {
				if(obsah.contains(protein)) {
					//System.out.println("naslo protein: " + protein);
					String znacka = "@protein_B_@ " + protein + " @protein_A_@";
					obsah = obsah.replace(protein, znacka);
				}
			}
		   */
			
			// Zapise zvysok
			if(novyObsah.length() > 0) {				
				Uprava_suborov.pridajNaKoniecDoSuboru(vystupnyAdr + sub, novyObsah);							
			}

			// Pozn. Iba v pripade optimalizacie
			if(nasloNieco == false) {
				Uprava_suborov.vymazSubor(vystupnyAdr + sub);
			}
			
			//break;
			
			if(pocitadlo % 1 == 0) {
				System.out.println(pocitadlo+"/"+subory.size());
			}
			
			pocitadlo++;
		}
		
		System.out.println("Spracovali sme " + pocitadloViet + " viet ");
		
	}
	
}
