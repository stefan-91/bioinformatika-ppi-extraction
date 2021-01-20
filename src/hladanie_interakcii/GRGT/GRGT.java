package hladanie_interakcii.GRGT;

import java.util.HashSet;
import java.util.Set;

import netriedene.Spracovanie_textu;
import uprava_suborov.Uprava_suborov;

/**
 * 
 * Popis pouzitej metody: 
 * 		https://www.ncbi.nlm.nih.gov/pmc/articles/PMC6069288/
 *
 */
public class GRGT {
	private Parsovanie_vety pv = new Parsovanie_vety();
	private Set<String> vzory = new HashSet<>();
		
	public GRGT() {
		//============ Nacita vzory zo suboru ==============				
		//String subVzory = "../materialy/vzory_interakcii/hash/vzory.txt";
		
		Set<String> vzoryS = new HashSet<>();		
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_hu_berlin.txt"); // Hackenbergov korpus		
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_BioInfer_vzory.txt");
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_AIMed_vzory.txt");
		vzoryS.add("../materialy/vzory_interakcii/hash/pomocne_vzory_kopia.txt");		
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_Hprd50_vzory.txt");
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_IEPA_vzory.txt");
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_LLL_vzory.txt");
		vzoryS.add("../materialy/vzory_interakcii/hash/korpus_PICAD_vzory.txt");		
		
		for(String subVzory : vzoryS) {
			System.out.println(subVzory);
			String obsahVzory = Uprava_suborov.vratObsahSuboru(subVzory);
			String[] slova = obsahVzory.split("\n");	
			
			for(String slovo : slova) {
				slovo = slovo.trim().toLowerCase();
				if(slovo.length() == 0) continue;
				vzory.add(slovo);
			}			
		}		
		System.out.println("Nacitalo " + vzory.size() + " vzorov.");		
	} 
	
	public void test() {
		String povodneTexty = "../materialy/testovacie_udaje/pomocny/originalne";
		String oznackovaneTexty = "../materialy/testovacie_udaje/pomocny/oznackovane";
		String vystup = "../materialy/testovacie_udaje/pomocny/vystup";
		
		najdiInterakcie(povodneTexty, oznackovaneTexty, vystup);
	}
	
	
	public void najdiInterakcie(String povodneTexty, String oznackovaneTexty, String vystup) {
		// Nazvy adresarov pre korpus				
		Uprava_suborov.vymazObsahPriecinka(vystup); // Vymaze obsah vysledkov po kazdom novom spusteni

		//if(true) return;
		//================ Hlada interakcie v suboroch podla vzorov ================
		
		// Prehlada subory a najde interakcie
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(oznackovaneTexty);		
		//System.out.println(subory);		
		
		int pocitadlo = 0;
		int pocitadloVety = 0;
		int prilisVelkeVety = 0;
		for(String sub : subory) {	
			// Pocitadlo kvoli vypisu
			pocitadlo++;
			System.out.println("Hlada interakcie v " + pocitadlo + "/" + subory.size());
			//if(pocitadlo < 924) continue; // Pozn. naostro zakomentovat
			
			String obsah = Uprava_suborov.vratObsahSuboru(oznackovaneTexty+"/"+sub);
			boolean naslo = false; // Ked nie su ziadne interakcie, tak ani nekopirujeme povodny subor
			String[] vety = obsah.split("\n");			
			Set<Vzor> kanonicke = new HashSet<>();
			System.out.println("Pocet viet v dokumente " + sub + ": " + vety.length);
			
			for(String veta : vety) {
				// Ak je va vstupe prazdny riadok
				veta = veta.trim();
				if(veta.length() == 0) continue;
				
				System.out.println("Hlada interakcie vo vete: " + veta);								
				Set<Vzor> kanonickePom = najdiKanonickeTvary(veta);
				if(kanonickePom != null) {
					pocitadloVety++;
					kanonicke.addAll(kanonickePom);
					//System.out.println("Kanonicke tvary: " + kanonicke);
				} else {
					prilisVelkeVety++;
				}
				//if(pocitadloVety == 2) break;// Pozn. Naostro zakomentovat
			}						
			
			//System.out.println("Kanonicke tvary: " + kanonicke);

			if(kanonicke != null && kanonicke.size() > 0) {
				// Hlada interakcie porovnavanim vzorov
				for(Vzor vzor : kanonicke) {
					if(vzory.contains(vzor.vzor)) {							
						naslo = true;
						System.out.println("Naslo Interakciu vo vete: " + vzor.veta + "\n" + "Vzor: " + vzor.vzor + "\n");
													
						String anotacia = vzor.veta; // Obsahuje iba oznackovane prvky, ktore su sucastou interakcie, nic navyse.
													
						//anotacia = "--------------------------" + anotacia + "";
						
						// Zapise anotaciu
						String nazovSub = sub.replace(".txt", "") + "-anotacie.txt";
						if(Uprava_suborov.existujeSubor(vystup+"/"+nazovSub) == false) {
							//System.out.println("Vytvara novy subor: " + nazovSub);
							Uprava_suborov.vytvorSubor(vystup+"/"+nazovSub);
						} {
							//System.out.println("Viac anotacii v subore: " + nazovSub);
						}			
						
						//System.out.println("Zapisuje do suboru: " + nazovSub + " \nobsah: " + anotacia);
						Uprava_suborov.pridajNaKoniecDoSuboru(vystup+"/"+nazovSub, anotacia);							
					} else {
						//System.out.println("Nenaslo interakciu.");						
					}
				}
									
				if(pocitadloVety % 10 == 0) {
					System.out.println("Pocet spracovanych viet: " + pocitadloVety);															
				}
			}
			
			// Prekopiruje povodny subor do rovnakeho adresara ako mame anotaciu
			if(naslo == true) {
				Uprava_suborov.vytvorSubor(vystup+"/"+sub);
				String obsah1 = Uprava_suborov.vratObsahSuboru(povodneTexty+"/"+sub);								
				Uprava_suborov.pridajNaKoniecDoSuboru(vystup+"/"+sub, obsah1);					
			} else {
				System.out.println("NENASLO interakciu");
			}	
			
		}
		
		double podiel = (prilisVelkeVety+0.0) / (pocitadloVety + prilisVelkeVety);
		podiel = podiel * 100;
		System.out.println("Pocet prilis dlhych viet je " + prilisVelkeVety + " s percentualnym zastupenim " + podiel + "%");
		
		// Vybera potencialne vety (min. 2 proteiny + min. 1 interakcia)
		// Vytiahne vzory
		// Porovna vzory a ak sa rovnaju, tak ich zapise		
	}
	
	/**
	 * 
	 * @param veta
	 * 		Oznackovana veta
	 * @return
	 * 		
	 * 
	 */
	private Set<Vzor> najdiKanonickeTvary(String veta) {
		int maxPocetIteracii = 100; // Aby nebolo prilis vela elementov
		 
		//System.out.println("Hladame vzory: " + veta);
		
		// Da prec znacky
		String vetaBezZnaciek = dajPrecZnacky(veta);
		//System.out.println(vetaBezZnaciek);
						
		Set<String> proteiny = Spracovanie_textu.vratObsahZnaciek(veta, "<protein>", "</protein>");
		//System.out.println(proteiny);
		
		Set<String> interakcie = Spracovanie_textu.vratObsahZnaciek(veta, "<interakcia>", "</interakcia>");
		//System.out.println(interakcie);	
		
		// Vyhodi prazdne slova (Pozn. Povod je nejaka chyba pri znackovani)
		proteiny.remove("");
		interakcie.remove("");	

		// Realizujeme hladanie vzorov vzdy medzi dvoma proteinmi a jednou interakciou
		//System.out.println("Interakcie: " + interakcie);
		//System.out.println("Proteiny: " + proteiny);
		/*
		// Pozn. Nepouziva sa
		Set<String> kanonicke = null;
		for(String interakcia : interakcie) {					
			// Skrati vetu
			Set<String> znacky = new HashSet<>();
			znacky.add("<protein>protein</protein>");
			//znacky.add("<protein>"+protein2+"</protein>");
			String intN = "<interakcia>"+interakcia+"</interakcia>";
			//System.out.println("Riesi interakciu: " + intN);
			znacky.add(intN); 
			//System.out.println("Povodna veta: " + veta);
			String skratena = Spracovanie_textu.skratPoZnacky(veta, znacky);
			//System.out.println("Skratena veta: " + skratena);					
			
			// Pokusi sa najst vzor so skratenou vetou
			Set<String> interakciaS = new HashSet<>();
			interakciaS.add(interakcia);
			skratena = dajPrecZnacky(skratena);
			kanonicke = pv.najdiKanonickeTvary(skratena, proteiny, interakciaS);
		}								
		*/
		
		// Ak nenajde vzor pri skratenej, necha zbehnut celu vetu
			// Zisti pocet vyskytov 		
		int pocetProt = Spracovanie_textu.vratPocetVyskytov(veta, "<protein>");
		int pocetInter = Spracovanie_textu.vratPocetVyskytov(veta, "<interakcia>");
		//System.out.println("Pocet proteinov: " + pocetProt + ", pocet inter.: " + pocetInter + " | " + veta);
		
		Set<Vzor> kanonicke = new HashSet<>();
		if(pocetProt >= 2 && pocetInter >= 1) { // Analyzuje iba vety v ktorych je dostatok elementov aby tam mohla byt interakcia
			// Skumame postupne trojicu za trojicou
			int pocetIteracii = 0;
			int i=0;
			for(String protein1 : proteiny) {
				i++;
				int j=0;
				if(pocetIteracii > maxPocetIteracii) break;
				
				for(String protein2 : proteiny) {
					j++;
					if(j >= i) continue; // Aby sme sa neopakovali ked davame kazde s kazdym
					//System.out.println(protein1 + ", " + protein2);
					if(pocetIteracii > maxPocetIteracii) break;
					
					for(String interakcia : interakcie) {
						pocetIteracii++;
						if(pocetIteracii > maxPocetIteracii) break;
						
						Set<String> proteinyPom = new HashSet<>();
						proteinyPom.add(protein1);
						proteinyPom.add(protein2);
						Set<String> interakciePom = new HashSet<>();
						interakciePom.add(interakcia);
						
						//System.out.println("Hlada interakcie: " + vetaBezZnaciek + proteinyPom + " " + interakciePom);						
						Set<Vzor> kanonickePom = pv.najdiKanonickeTvary(vetaBezZnaciek, proteinyPom, interakciePom);
						
						if(kanonickePom == null || kanonickePom.size() == 0) {
							//System.out.println("Nenaslo 1: " + veta);
							//System.out.println("Nenaslo 2: " + vetaBezZnaciek + proteinyPom + " " + interakciePom);						
						}
						else {
							kanonicke.addAll(kanonickePom);
							System.out.println("Naslo vzor: ");
							System.out.println("Kanonicke: " + kanonickePom);
							for(Vzor v : kanonicke) {
								//System.out.println("Vzor: " + v.vzor);
								//System.out.println("Veta: " + v.veta);	
							}
						}
					}
				
				}								
			}
			
			if(kanonicke == null || kanonicke.size() == 0) {
				/*
				System.out.println("Nenaslo 1: " + veta);
				System.out.println("Nenaslo 2: " + vetaBezZnaciek);
				System.out.println("Nenaslo 3: " + proteiny + " " + interakcie);
				*/															
			}
			
		} else {
			//System.out.println("Veta neobsahovala potrebne prvky: " + veta);
		}	

		//System.out.println("Vracia kanonicke.");
		return kanonicke;				
	}
	
public String dajPrecZnacky(String veta) {
	String vetaBezZnaciek = veta;
	String[] tagy = new String[] {"<protein>", "</protein>", "<interakcia>", "</interakcia>"};
	for(String tag : tagy) {
		vetaBezZnaciek = vetaBezZnaciek.replace(tag, "");
	}
	//System.out.println(vetaBezZnaciek);		
	return vetaBezZnaciek;
}	
	
// Spracuje vzory do hashov a ulozi do suboru aby sa nemuseli spracovavat pred kazdym spracovanim suborov	
public void spracujVzory(String korpusAdr, Integer offset, int maxDlzkaVety) {
	pv.nastavMaxDlzkuVety(maxDlzkaVety);
	
	String vzoryZnackovane = "../materialy/vzory_interakcii/oznackovane_vety/"+korpusAdr+"/";	
	
	String vzoryHash = "../materialy/vzory_interakcii/hash/"+korpusAdr+"_vzory.txt";	
	String nenasloVzory = "../materialy/vzory_interakcii/hash/"+korpusAdr+"_nenasloVzory.txt";
	String platneVety = "../materialy/vzory_interakcii/hash/"+korpusAdr+"_platneVety.txt";
	String skoreParsovania = "../materialy/vzory_interakcii/hash/"+korpusAdr+"_skore.txt";
	
	if(offset == null || offset == 0) { // Ked nie je null, tak nemozeme subory vymazat lebo do nich chceme naopak vkladat dalej kde sme skoncili		
		uprava_suborov.Uprava_suborov.vytvorSubor(vzoryHash);
		uprava_suborov.Uprava_suborov.vytvorSubor(nenasloVzory); // Pozn. na vyvoj
		uprava_suborov.Uprava_suborov.vytvorSubor(platneVety); // Pozn. na vyvoj
		uprava_suborov.Uprava_suborov.vytvorSubor(skoreParsovania); // Pozn. na vyvoj		
	}
	
	int pocitadloVetyV = 0;
	int nenasloVzor = 0;
	
	Set<String> suboryVzory = Uprava_suborov.vratVsetkyNazvySuborov(vzoryZnackovane);	
	for(String sub : suboryVzory) {				
		String obsah = Uprava_suborov.vratObsahSuboru(vzoryZnackovane+"/"+sub);
		String[] vety = obsah.split("\n");			
	
		for(String veta : vety) {
			// Pocitadla su na zaciatku aby zbytocne nepocitalo ked netreba
			pocitadloVetyV++;
														
			if(pocitadloVetyV < offset) {
				System.out.println("Hlada offset " + pocitadloVetyV + "/" + offset);
				continue;				
			}
			
			if(pocitadloVetyV % 10 == 0) {
				System.out.println("Nacitavanie vzorov: " + pocitadloVetyV + " viet.");
			}	
			
			Set<Vzor> kanonicke  = najdiKanonickeTvary(veta);
			
			if(kanonicke == null) {
				System.out.println("prilis dlha veta: " + veta);
				
				nenasloVzor++;
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(nenasloVzory, veta);
			} else 	if(kanonicke.size() == 0) {
				//System.out.println(", nenaslo vo vete vzor: " + veta);
				nenasloVzor++;
				uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(nenasloVzory, veta);
			}
			
			if(kanonicke != null) {
				//System.out.println("Naslo vzory");
				//System.out.println(vzory);
				// Zapise vzory do suboru
				for(Vzor vzor : kanonicke) {					
					uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vzoryHash, vzor.vzor);
					uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(platneVety, veta);	
					uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(skoreParsovania, vzor.parsovacieSkore + "");											
				}
				
			}

			//System.out.println("Veta: " + veta);
			//System.out.println("Kanonicke: " + kanonicke);
			//break; // Pozn. naostro zakomentovat
		}			 			
	}
	
	System.out.println("Pocet viet v ktorych nenaslo vzor: " + nenasloVzor );	
}	

	// Stara nepouzivana metoda
	public void najdiInterakcie1() {
		// Nacita zoznam interakcii
		Set<String> interakcie = new HashSet<>();
		String protStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_interakcii/12859_2009_2963_MOESM1_ESM.txt");
		String[] polozky = protStr.split("\n");
		for(String s : polozky) {
			s = s.trim();
			if(s.length() == 0) continue;
			interakcie.add(s);
		}	
		
		// Prehlada subory a najde interakcie
		String korpus2 = "../korpus/oznackovaneNazvy/";
		Set<String> subory = Uprava_suborov.vratVsetkyNazvySuborov(korpus2);		
		//System.out.println(subory);		
		
		int pocitadlo = 0; 
		for(String sub : subory) {
			boolean naslo = false;
			
			String obsah = Uprava_suborov.vratObsahSuboru(korpus2+"/"+sub);
			//System.out.println(obsah);
			String anotacie = "";
			
			String[] vety = obsah.split("\n");
			for(String veta : vety) {
				/*
				String[] slova = veta.split(" ");
				for(String slovo : slova) {
					slovo = slovo.trim();
					if(interakcie.contains(slovo)) {
						// U - vytvorit presnejsie riesenie
						// U - vyriesit aj situaciu ak existuje viac interakcii
						
						int protA = veta.indexOf("@protein_A_@");
						int protB = veta.lastIndexOf("@protein_B_@"); // musi byt posledny lebo znacka protB je vzdy pred protA pri danom prot.
						int interakciaIndex = veta.indexOf(slovo);
						
						if(protA < interakciaIndex && interakciaIndex < protB) {
							naslo = true;
							int protA2 = veta.lastIndexOf("@protein_A_@"); // najde posledny vyskyt
							int protB1 = veta.indexOf("@protein_B_@"); // najde prvy vyskyt
							
							String proteinA = veta.substring(protB1, protA);
							String proteinB = veta.substring(protB, protA2);
							
							// Dame prec znacky
							proteinA = proteinA.replace("@protein_B_@", "");
							proteinA = proteinA.replace("@protein_A_@", "");
							
							proteinB = proteinB.replace("@protein_B_@", "");							
							proteinB = proteinB.replace("@protein_A_@", "");
																			
							proteinA = proteinA.trim();
							proteinB = proteinB.trim();
							
							System.out.println(proteinA + " - " + slovo + " - " + proteinB);
												
							// Ulozi anotacie
								// Pekne zobrazi
							veta = veta.replace("@protein_B_@", " |PROTEIN -->| ");							
							veta = veta.replace("@protein_A_@", "");							
							
							veta = veta.replace(slovo, " |INTERACTION -->| " + slovo + "");														
							
							anotacie = anotacie
									+ "--------------------------\n"
									+ "Interakcia: \n"
									+ proteinA + " - " + slovo + " - " + proteinB + "\n"
									+ "Veta: \n"
									+ veta									
									+ "\n";																					
						}						
					}														
				}	
				*/			
			}
			
			// Zapise udaje do vysledneho korpusu
			if(naslo == true) {
				//if(true) continue;
				
				String korpus1 = "../korpus/originalne_texty/";
				String korpus3 = "../korpus/vysledky/";
				
				// Zapise poznamku				
				String nazovSub = sub.replace(".txt", "") + "-anotacie.txt";
				Uprava_suborov.vytvorSubor(korpus3+"/"+nazovSub);
				Uprava_suborov.pridajNaKoniecDoSuboru(korpus3+"/"+nazovSub, anotacie);
				
				// Prekopiruje povodny subor do rovnakeho adresara ako mame poznamku
				String obsah1 = Uprava_suborov.vratObsahSuboru(korpus1+"/"+sub);				
				Uprava_suborov.vytvorSubor(korpus3+"/"+sub);
				Uprava_suborov.pridajNaKoniecDoSuboru(korpus3+"/"+sub, obsah1);								
			}						
		}		
	}
	
	private class Zaznam {
		public String veta = "";
		//public String 
		
	}				
}

















































