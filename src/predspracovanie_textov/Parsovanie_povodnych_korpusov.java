package predspracovanie_textov;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uprava_suborov.Uprava_suborov;

/**
 * 
 * Prevadza rozne korpusy na nas jednotny format (parsuje z XML a pod.)
 *
 */
public class Parsovanie_povodnych_korpusov {
	//private Set<String> interakcie = new HashSet<>();
	private Znackovanie_viet pt;
	
	public Parsovanie_povodnych_korpusov() {
		pt = new Znackovanie_viet();
		pt.pridajProtein("protein");
		/*
		List<String> subory = new LinkedList<>();
		subory.add("../materialy/nazvy_interakcii/12859_2009_2963_MOESM1_ESM.txt");
		subory.add("../materialy/nazvy_interakcii/hu_berlin.txt");

		for(String subor : subory) {
			// Nacita zoznam interakcii
			String protStr = Uprava_suborov.vratObsahSuboru(subor);				
			String[] polozky = protStr.split("\n");
			for(String s : polozky) {
				s = s.trim();
				if(s.length() == 0) continue;
				interakcie.add(s);
			}			
		}
		*/		
	}
	
	// korpus BioCreative PPi
	// Interakcie su nespravne interpretovane, preto ich oznackujeme s vlastnymi prostriedkami
	public void spracujKorpusFormatu3(String korpus) {
		String vstup = "../materialy/ine_korpusy/" + korpus + "/originalne_subory/";
		String vystup = "../materialy/ine_korpusy/" + korpus + "/nas_format/";
		
		Uprava_suborov.vymazObsahPriecinka(vystup);
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstup);
		
		for(String sub : obsah) {
			String vstupSub = vstup + sub;	
			String vystupSub = vystup + sub;
					
			if(vystupSub.endsWith(".xml")) {
				vystupSub = vystupSub.replace(".xml", ".txt");
			}
			
			uprava_suborov.Uprava_suborov.vytvorSubor(vystupSub);
			String xml = uprava_suborov.Uprava_suborov.vratObsahSuboru(vstupSub);
			
			try {
				Document doc = Jsoup.parse(xml);			
				//System.out.println(doc.toString());
				Element vetyVsetko = doc.getElementsByTag("sentences").first();
				Elements vety = vetyVsetko.getElementsByTag("sentence");
				
				for(Element veta : vety) {					
					Elements slova = veta.children();
					String vetaStr = "";
					for(Element slovo : slova) {
						String nazovTagu = slovo.tagName();
						//System.out.println(nazovTagu);
						if(nazovTagu.equals("interactor")) {
							vetaStr = vetaStr + " " + "<interakcia>" + slovo.text() + "<interakcia>"; 
						} 
						else if(nazovTagu.equals("gene")) {
							vetaStr = vetaStr + " " + "<protein>" + slovo.text() + "<protein>"; 
						} 
						else {
							vetaStr = vetaStr + " " + slovo.text();
						}
					}
					
					vetaStr = vetaStr.trim();
					//System.out.println(vetaStr);
					uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystupSub, vetaStr);	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
		
		System.out.println("Spracovalo povodny korpus: " + korpus);	
	}	
	
	// korpus PICAD
	// Interakcie su nespravne interpretovane, preto ich oznackujeme s vlastnymi prostriedkami
	public void spracujKorpusFormatu2(String korpus) {
		String vstup = "../materialy/ine_korpusy/" + korpus + "/originalne_subory/";
		String vystup = "../materialy/ine_korpusy/" + korpus + "/nas_format/";
		
		Uprava_suborov.vymazObsahPriecinka(vystup);
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstup);
		
		for(String sub : obsah) {
			String vstupSub = vstup + sub;	
			String vystupSub = vystup + sub;
					
			if(vystupSub.endsWith(".xml")) {
				vystupSub = vystupSub.replace(".xml", ".txt");
			}
			
			uprava_suborov.Uprava_suborov.vytvorSubor(vystupSub);
			String xml = uprava_suborov.Uprava_suborov.vratObsahSuboru(vstupSub);
			
			try { // V rucnom korpuse je chyba
				Document doc = Jsoup.parse(xml);			
				//System.out.println(doc.toString());
				Element vetyVsetko = doc.getElementsByTag("corpus").first();
				Elements vety = vetyVsetko.getElementsByTag("sentence");
				
				for(Element veta : vety) {					
					String text = veta.attr("text");
					
					Elements trojice = veta.getElementsByTag("triplet");
					for(Element trojica : trojice) {
						try {
						//System.out.println(trojica.toString());
						int poziciaP1 = Integer.parseInt(vratHodnotuAtributuBezUvodzoviek(trojica.toString(), "e1")) - 1;
						int poziciaP2 = Integer.parseInt(vratHodnotuAtributuBezUvodzoviek(trojica.toString(), "e2")) - 1;
						int poziciaI = Integer.parseInt(vratHodnotuAtributuBezUvodzoviek(trojica.toString(), "iwpos")) - 1;
					
						String[] textArr = text.split(" ");
						textArr[poziciaP1] = "<protein>" + textArr[poziciaP1] + "</protein>";
						textArr[poziciaP2] = "<protein>" + textArr[poziciaP2] + "</protein>";
						//textArr[poziciaI] = "<interakcia>" + textArr[poziciaI] + "</interakcia>"; // Interakcie neznackujeme
						
						String vysledok = "";
						for(String slovo : textArr) {
							vysledok = vysledok + " " + slovo;
						}
						vysledok = vysledok.trim();
						uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystupSub, vysledok);	
						//System.out.println(vysledok);
						} catch(Exception e) {
							//e.printStackTrace();
							//System.out.println("Bola chyba v " + trojica.toString());
							//System.out.println("Chybny text " + text);
							//System.out.println("Chybne zapisany udaj");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
		
		System.out.println("Spracovalo povodny korpus: " + korpus);	
	}
	
	// Plati iba pre format PICAD
	private String vratHodnotuAtributuBezUvodzoviek(String tag, String nazovAtr) {
		String hodnota = null;
		tag = tag.replace("=", " ");
		String[] polozky = tag.split(" ");		
		boolean meno = false;
		for(String polozka : polozky) {
			if(nazovAtr.equals(polozka)) {
				meno = true;
				continue;
			}
			
			if(meno == true) {
				hodnota = polozka;
				break;
			}							
		}
		
		hodnota = hodnota.replace("\"", "");
		
		return hodnota;
	}
	
	// Zo zdroja Hu. Berlin
	public void spracujKorpusFormatu1(String korpus) {
		String vstup = "../materialy/ine_korpusy/" + korpus + "/originalne_subory/";
		String vystup = "../materialy/ine_korpusy/" + korpus + "/nas_format/";
		
		Uprava_suborov.vymazObsahPriecinka(vystup);
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstup);
		
		for(String sub : obsah) {
			String vstupSub = vstup + sub;	
			String vystupSub = vystup + sub;
					
			if(vystupSub.endsWith(".xml")) {
				vystupSub = vystupSub.replace(".xml", ".txt");
			}
			
			uprava_suborov.Uprava_suborov.vytvorSubor(vystupSub);
			String xml = uprava_suborov.Uprava_suborov.vratObsahSuboru(vstupSub);
			
			try {
				Document doc = Jsoup.parse(xml);			
				//System.out.println(doc.toString());
				Element vetyVsetko = doc.getElementsByTag("collection").first();
				Elements vety = vetyVsetko.getElementsByTag("document");
				
				for(Element veta : vety) {
					//System.out.println(veta.toString());										
					Elements interakcie = veta.getElementsByTag("relation");
					for(Element interakcia : interakcie) { // Pre kazdu interakciu
						//System.out.println(interakcia.toString());
						String text = veta.getElementsByTag("text").first().text();
						
						// Oznackuje prvky interakcie						
						Elements ucastnici = interakcia.getElementsByTag("node");
						Integer[] pozicie = new Integer[ucastnici.size()];
						Map<Integer,Integer> pozicieMap = new HashMap();
						int pocitadlo = 0;
						for(Element ucastnik : ucastnici) {
							// Najde poziciu ucastnika interakcie
							String ucastnikID = ucastnik.attr("refid");
							Element ucastnikInfo = veta.getElementById(ucastnikID);
							Element pozicia = ucastnikInfo.getElementsByTag("location").first(); // je iba jeden tag, preto first 							
							
							Integer offset = Integer.parseInt(pozicia.attr("offset"));
							Integer dlzka = Integer.parseInt(pozicia.attr("length"));
							
							pozicieMap.put(offset, dlzka);
							
							pozicie[pocitadlo] = offset;
							pocitadlo++;																					
						}
						
						// Prida znacky podla pozicie (pridava od konca aby sa nezmenili zistene pozicie)						
						Arrays.sort(pozicie);
						//System.out.println(Arrays.toString(pozicie));
						for(int i=pozicie.length-1; i>=0; i--) {
							int pozicia1 = pozicie[i];
							int pozicia2 = pozicie[i] + pozicieMap.get(pozicie[i]);
							// Prida zaciatocny a koncovy tag (znacku)
							text = text.substring(0, pozicia2) + "</protein>" + text.substring(pozicia2); //najprv musime pridat koncovu znacku
							text = text.substring(0, pozicia1) + "<protein>" + text.substring(pozicia1);							
						}
						
						// Ak je viac viet, tak ich rozdeli
						String[] vetyArr = text.split("\\.");
						
						for(String vetaS : vetyArr) {
							if(vetaS.trim().length() == 0) continue;															
							vetaS = vetaS + ".";
							
							// Zapise vetu
							System.out.println(text);
							uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystupSub, vetaS);								
						}
						
					
					}
				}			
				
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}
		
		System.out.println("Spracovalo povodny korpus: " + korpus);
	}
	
	public Set<String> spracujHuBerlin() {
		String cesta = "../materialy/vzory_interakcii/oznackovane_vety/korpus_hu_berlin/hu_berlin_oznackovane_vety.txt"; // Kam zapisujeme vysledne vzory
		uprava_suborov.Uprava_suborov.vymazSubor(cesta);
		uprava_suborov.Uprava_suborov.vytvorSubor(cesta);
		
		List<String> subory = new LinkedList<>();
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_ij00ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_in00ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_in01ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_in11ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_in20ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_iv00ul.xml");
		subory.add("../materialy/vzory_interakcii/nestrukturovane/intactPatterns_iv11ul.xml");
		
		int pocitadlo = 0;
		for(String subor : subory) {

			String xml = uprava_suborov.Uprava_suborov.vratObsahSuboru(subor);
			
			try {
				Document doc = Jsoup.parse(xml);			
				//System.out.println(doc.toString());
				Element frazyVsetko = doc.getElementsByTag("phrases").first();
				Elements frazy = frazyVsetko.getElementsByTag("phrase");
				
				for(Element e : frazy) {
					
					String veta = spracujVetu(e.text()); // Da prez znacky z originalneho korpusu
					veta = pt.spracujVetu(veta, true, true, false); // oznackuje vetu					
					
					uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(cesta, veta);
					pocitadlo++;
					if(pocitadlo % 100 == 0) {
						System.out.println("Prevadzame vzory z nestrukturovaneho formatu: " + pocitadlo);
					}
				}			
				
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		System.out.println("Pocet spracovanych vzorov: " + pocitadlo);
		
		
		return null;
	}
	
	private String spracujVetu(String veta) {
		String znackaProteinu = "protein";
		veta = veta.replace("ANY/PTN", znackaProteinu + "/PTN");
		
		String vetaVystup = "";		
		String[] texty = veta.split(" ");		
		for(String text : texty) {
			String[] dvojica = text.split("/");
			String slovo = dvojica[0].trim();
			/*
			// znackovanie
			if(slovo.equals(znackaProteinu)) slovo = "<protein>protein</protein>";
			if(interakcie.contains(slovo.toLowerCase())) slovo = "<interakcia>" + slovo.toLowerCase() + "</interakcia>";
			*/			
			vetaVystup = vetaVystup + " " + slovo;
		}		
		vetaVystup = vetaVystup.trim();
					
		return vetaVystup;
	}	
	
	// Nasledne sme skopirovali udaje zo standardneho vystupu do TXT suboru
	public void extrahujNazvyInterakcii() {
		Set<String> interakcie = new HashSet<>();
		
		List<String> subory = new LinkedList<>();
		subory.add("../materialy/nazvy_interakcii/nestrukturovane/hu_berlin/iadjectives.txt");
		subory.add("../materialy/nazvy_interakcii/nestrukturovane/hu_berlin/inouns.txt");
		subory.add("../materialy/nazvy_interakcii/nestrukturovane/hu_berlin/iverbs.txt");
		
		for(String subor : subory) {
			String text = uprava_suborov.Uprava_suborov.vratObsahSuboru(subor);
			String[] riadky = text.split("\n");
			
			for(String riadok : riadky) {
				//System.out.println(riadok);
				String[] ntice = riadok.split("\t");
				//System.out.println(ntice[0]);
				String nazov = ntice[0];
				nazov = nazov.trim();
				
				if(nazov.length() == 0) continue;
				System.out.println(nazov);
				
				if(nazov.contains("?")) continue; // nejasna interakcia
				
				nazov = nazov.replace("#", "");
				nazov = nazov.replace("#", "");
				
				interakcie.add(nazov);
//				System.out.println(nazov);
			}								
		}
		
		System.out.println("Naslo " + interakcie.size() + " interakcii.");
	
	}
	
	
}

















































