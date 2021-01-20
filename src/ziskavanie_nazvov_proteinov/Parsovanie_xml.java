package ziskavanie_nazvov_proteinov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import uprava_suborov.Uprava_suborov;

public class Parsovanie_xml {
	// Vystupne dokumenty
	private String recommendedFull_sub = "recommendedFull.txt";
	private String recommendedShort_sub = "recommendedShort.txt";
	private String alternativeFull_sub = "alternativeFull.txt";
	private String alternativeShort_sub = "alternativeShort.txt";
	private String submittedFull_sub = "submittedFull.txt";
	private String submittedShort_sub = "submittedShort.txt";
	private String genes_sub = "genes.txt";

	private String vystup = "../materialy/nazvy_proteinov/nas_format/";
	private Set<String> recommendedFull = new HashSet<>();
	private Set<String> recommendedShort = new HashSet<>();
	private Set<String> alternativeFull = new HashSet<>();
	private Set<String> alternativeShort = new HashSet<>();
	private Set<String> submittedFull = new HashSet<>();
	private Set<String> submittedShort = new HashSet<>();
	private Set<String> genes = new HashSet<>();
		
	public void spracujUniprot() {
		String vstup = "../materialy/nazvy_proteinov/originalne_formaty/uniprot/";
		
		
		Uprava_suborov.vymazObsahPriecinka(vystup);
		
		Set<String> obsah = Uprava_suborov.vratVsetkyNazvySuborov(vstup);
		
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + recommendedFull_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + recommendedShort_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + alternativeFull_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + alternativeShort_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + submittedFull_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + submittedShort_sub);
		uprava_suborov.Uprava_suborov.vytvorSubor(vystup + genes_sub);		
		
		for(String sub : obsah) {
			String vstupSub = vstup + sub;	
			//String vystupSub = vystup + sub;
					
			//if(vystupSub.endsWith(".xml")) {
			//	vystupSub = vystupSub.replace(".xml", ".txt");
			//}
						
			//String xml = uprava_suborov.Uprava_suborov.vratObsahSuboru(vstupSub);
			
			File file = new File(vstupSub);
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			    String line;
			    String xmlStr = "";
			    int pocitadlo = 0;
			    int pocetRiadkov = 0;
			    boolean zacalo = false;
			    while ((line = br.readLine()) != null) {
			    	//System.out.println(line);
			    	line = line.trim();

			    	if(line.startsWith("<entry")) {
			    		zacalo = true;
			    		xmlStr = line; // prepiseme predosle udaje
			    	} 
			    	else if(line.startsWith("</entry")) {
			    		//System.out.println();
			    		xmlStr = xmlStr + line;
			    		spracujEntry(xmlStr); // spracujeme lebo mame celu polozku			    					    		
			    		zacalo = false;
			    		pocitadlo++;
			    		/*
			    		if(pocitadlo % 10 == 0) {
			    			System.out.println("Spracovalo " + pocitadlo + " proteinov.");
			    		}
			    		*/
			    		
			    	} else {
			    		boolean mozeme = true;
			    		// Testy ci mozeme zapisovat
			    		if(zacalo == false) mozeme = false;
			    		
			    		// Nadbytocne informacie
			    		if(line.startsWith("<property type") && line.endsWith("/>")) mozeme = false;			    						    		
			    		else if(line.startsWith("<dbReference")) mozeme = false; 
			    		else if(line.startsWith("</dbReference")) mozeme = false; 
			    		else {
			    			//System.out.println(line);
			    			if(pocitadlo >= 716) {
			    				//System.out.println(line);
			    			}
			    		}
			    		if(mozeme == true) xmlStr = xmlStr + line + "\n"; // Obycajne pridame
			    		
			    		
			    	}
			    	
			    	pocetRiadkov++;
			    	if(pocetRiadkov % 10000 == 0) {			    		
			    		System.out.print("Spracovane proteiny: " + pocitadlo + ", pocet riadkov: " + pocetRiadkov);
			    		System.out.print(", Dlzka textu XML: " + xmlStr.length());
			    		//System.out.print(", Otvoreny tag entry: " + zacalo);
			    		System.out.println();
			    		
			    		if(pocitadlo >= 716) {
			    			//System.out.println(line);
			    		} 
			    		
			    	}
			    	
			    	//System.out.println(line);
			    	
			    }
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			
		}
				
	}
	
private void spracujEntry(String xml) {
	//System.out.println(xml);
	try {
		Document doc = Jsoup.parse(xml);			
		//System.out.println(doc.toString());		
		Elements polozky = doc.getElementsByTag("entry");
		
		for(Element elem : polozky) {
			Set<String> nazvy = new HashSet<>();
			
			//------------ Proteiny -------------
			Elements proteiny = elem.getElementsByTag("protein");
			for(Element protein : proteiny) {						
				// Odporucane nazvy
				Elements odporucane = protein.getElementsByTag("recommendedName");
				
				for(Element odporucany : odporucane) {
					Elements fullO = odporucany.getElementsByTag("fullName");
					for(Element myFull : fullO) {
						recommendedFull.add(myFull.text());
					}
					
					Elements shortO = odporucany.getElementsByTag("shortName");
					for(Element myShort : shortO) {
						recommendedShort.add(myShort.text());
					}						
				}
								
				
				// Alternativne nazvy
				Elements alternativne = protein.getElementsByTag("alternativeName");
				
				for(Element alternativny : alternativne) {
					Elements fullA = alternativny.getElementsByTag("fullName");
					for(Element myFull : fullA) {
						alternativeFull.add(myFull.text());
					}
					
					Elements shortA = alternativny.getElementsByTag("shortName");
					for(Element myShort : shortA) {
						alternativeShort.add(myShort.text());
					}	
					
					//if(alternativeShort.contains("DAMAGE")) {
					//	System.out.println(protein.toString());
					//}
					
				}
				
				// Submitted name
				Elements submitted = protein.getElementsByTag("submittedName");
				
				for(Element submittedE : submitted) {
					Elements fullO = submittedE.getElementsByTag("fullName");
					for(Element myFull : fullO) {
						submittedFull.add(myFull.text());
					}
					
					Elements shortO = submittedE.getElementsByTag("shortName");
					for(Element myShort : shortO) {
						submittedShort.add(myShort.text());
					}						
				}
									
			}

			//------------------ Geny ----------------
			
			// Pozn. Geny nezapisuje (GPI nehladame, iba PPI)
			Elements geny = elem.getElementsByTag("gene");
			for(Element gen : geny) {
				Elements mena = gen.getElementsByTag("name");
				for(Element meno : mena) {
					genes.add(meno.text());
				}
			}
						
			// Zapise nazvy					
			for(String nazov : recommendedFull) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + recommendedFull_sub, nazov);
			for(String nazov : recommendedShort) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + recommendedShort_sub, nazov);
			for(String nazov : alternativeFull) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + alternativeFull_sub, nazov);
			for(String nazov : alternativeShort) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + alternativeShort_sub, nazov);
			for(String nazov : submittedFull) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + submittedFull_sub, nazov);
			for(String nazov : submittedShort) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + submittedShort_sub, nazov);
			for(String nazov : genes) uprava_suborov.Uprava_suborov.pridajNaKoniecDoSuboru(vystup + genes_sub, nazov);
						
			recommendedFull.clear();
			recommendedShort.clear();
			alternativeFull.clear();
			alternativeShort.clear();
			submittedFull.clear();
			submittedShort.clear();
			genes.clear();
			
		}			
		
	} catch (Exception e) {
		e.printStackTrace();
	}	
}	
	
}























































