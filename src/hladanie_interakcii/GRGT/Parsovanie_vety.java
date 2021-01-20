package hladanie_interakcii.GRGT;

import java.io.IOException;

import java.util.*;


import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import netriedene.MyData;
import netriedene.Spracovanie_textu;
import predspracovanie_textov.Znackovanie_viet;

/**
 * 
 * Prevedie vetu na graf
 *
 */
public class Parsovanie_vety {
	private Set<String> interakcie;
	
	private int maxDlzkaVety = MyData.maxDlzkaVety; // Pocet slov vo vete // Pozn. pri prevadzke ak ma rychlo zbehnut 50, pri hladani vzorov 150 
	
	// Stanfordsky parser
	private LexicalizedParser lp;
	private TreebankLanguagePack tlp;
	private GrammaticalStructureFactory gsf;
	
	public void nastavMaxDlzkuVety(int max) {
		maxDlzkaVety = max;
	}
	
	public Parsovanie_vety(/*Set<String> interakcie*/) {
		//this.interakcie = interakcie;
		
		// Popis modelov: https://stackoverflow.com/questions/36844102/stanford-parser-models
		//lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		//lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.caseless.ser.gz");
		//lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishFactored.ser.gz");
		
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();				
	}
	       
	public void testSkore() {
		String veta = "It has been reported that SNAI1 could binding with HDAC1.";

		Set<String> proteiny = new HashSet<>();
		proteiny.add("PAR3alpha"); proteiny.add("PAR6"); proteiny.add("MAD2L1");
		proteiny.add("EMT"); proteiny.add("HDAC1"); proteiny.add("SNAI1");
		proteiny.add("protein"); proteiny.add("myprotein"); proteiny.add("gene");
		proteiny.add("gerE"); proteiny.add("cwlH"); proteiny.add("NM23-H1"); proteiny.add("GzmA");
		proteiny.add("HCC"); proteiny.add("CDK1");
		
		Set<String> interakcie = new HashSet<>();
		interakcie.add("interact"); interakcie.add("controls"); interakcie.add("upregulated");
		interakcie.add("suppress"); interakcie.add("expressed"); interakcie.add("target");
		interakcie.add("complex"); interakcie.add("induced"); interakcie.add("mediated");
		interakcie.add("enhancement"); interakcie.add("depended"); interakcie.add("dependent");
		interakcie.add("effect"); interakcie.add("interaction"); interakcie.add("cleavage");
		interakcie.add("increased");
		
		Set<Vzor> kanonicke = najdiKanonickeTvary(veta, proteiny, interakcie);		
		System.out.println("Kanonicke tvary: " + kanonicke);		
	} 
	
	public void test() {
		String veta = "It has been reported that SNAI1 could interact with HDAC1.";
		
		LexicalizedParser lp;
		//lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishRNN.ser.gz");		
		
		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		Tree parse = lp.parse(veta);
		parse.pennPrint();
		System.out.println();

		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);
		System.out.println();	
	}	
	
	public void test1() {
		//String veta = "protein is considered to interact with protein";
		//String veta = "PAR3alpha is considered to interact with PAR6";
		//String veta = "The first PDZ domain of PAR3alpha is considered to interact with PAR6.";
		//String veta = "The expression data collected from 4 datasets from ONCOMINE indicated that MAD2L1 expression was upregulated in HCC tissues compared with the normal controls (all P < 0";	
		//String veta = "It has been reported that SNAI1 could interact with transcriptional repressor such as the PRC2 complex and HDAC1 on the EMT target gene promoter to suppress gene transcription 24.";
		//String veta = "protein -induced gene";
		//String veta = "protein -induced protein";
		//String veta = "protein never block protein";
		//String veta = "Photochemical cleavage of protein and the effect on the interaction with protein.";		
		//String veta = "NM23-H1 binds to SET and is released from inhibition by GzmA cleavage of SET";
		//String veta = "We also found the increased CDK1 expression in HCC samples and proved its prognostic value for cancer patients.";
		//String veta = "sigmaF is controlled by a regulatory cascade involving an anti-sigma factor, SpoIIAB, an anti-anti-sigma factor, SpoIIAA, and a membrane-bound phosphatase, SpoIIE, which converts the inactive, phosphorylated form of SpoIIAA back to the active form.";
		//String veta = "Most cot genes, and the gerE gene, are transcribed by sigmaK RNA polymerase.";
		//String veta = "The SpoIIE phosphatase indirectly activates sigmaF by dephosphorylating a protein (SpoIIAA-P) in the pathway that controls the activity of the transcription factor.";
		//String veta = "Pinin/DRS/memA interacts with SRp75, SRm300 and SRrp130 in corneal epithelial cells Three SR-rich proteins were identified that interact with the C-terminus of Pnn: SRp75 and SRm300, known components of spliceosome machinery, and a novel 130-kDa nuclear protein, SRrp130.";
		//String veta = "sspG transcription also requires the DNA binding protein GerE.";
		//String veta = "These results suggest that YfhP may act as a negative regulator for the transcription of yfhQ, yfhRs, sspE and yfhP.";
		//String veta = "Six coEMs (Turquoise, Blue, Red, P urple, Lightyellow, and Chocolate modules) were associated with either SBP or DBP at P < 0.";
		String veta = "YfhP act as a regulator for the transcription of yfhQ";
		
		
		// <protein>protein</protein> -induced <protein>protein</protein>
		//String veta = "Expression of the sigma(K)-dependent <protein>cwlH</protein> gene depended on <protein>gerE</protein>.";
		
		Set<String> proteiny = new HashSet<>();
		//proteiny.add("PAR3alpha"); proteiny.add("PAR6"); proteiny.add("MAD2L1");
		//proteiny.add("EMT"); proteiny.add("HDAC1"); proteiny.add("SNAI1");
		//proteiny.add("protein"); 
		//proteiny.add("myprotein"); proteiny.add("gene");
		//proteiny.add("gerE"); proteiny.add("cwlH"); proteiny.add("NM23-H1"); proteiny.add("GzmA");
		//proteiny.add("HCC"); proteiny.add("CDK1");
		//proteiny.add("sigma(K)");proteiny.add("cwlH");proteiny.add("gerE.");
		//proteiny.add("cot"); proteiny.add("gerE");
		//proteiny.add("SpoIIE"); proteiny.add("sigmaF");
		//proteiny.add("sspG"); proteiny.add("GerE");
		//proteiny.add("YfhP"); proteiny.add("yfhQ");
		
		Set<String> interakcie = new HashSet<>();
		//interakcie.add("interact"); interakcie.add("controls"); interakcie.add("upregulated");
		//interakcie.add("suppress"); interakcie.add("expressed"); interakcie.add("target");
		//interakcie.add("complex"); interakcie.add("induced"); interakcie.add("mediated");
		//interakcie.add("enhancement"); interakcie.add("depended"); interakcie.add("dependent");
		//interakcie.add("effect"); interakcie.add("interaction"); interakcie.add("cleavage");
		//interakcie.add("increased"); interakcie.add("Expression"); interakcie.add("dependent");
		//interakcie.add("depended"); //interakcie.add("controlled"); //interakcie.add("activates");		
		interakcie.add("requires");
		
		Set<Vzor> kanonicke = najdiKanonickeTvary(veta, proteiny, interakcie);		
		System.out.println("Kanonicke tvary: " + kanonicke);
		
	}
	
	private String predoslaVeta = null;
	private Collection<TypedDependency> predosleTdl = null;
	private Double predosleSkore = null;
	
	/**
	 * 
	 * @param veta
	 * @param proteiny
	 * @param interakcie
	 * @return
	 * 		NULL - veta je prilis dlha
	 * 		Prazdna mnozina - nenaslo kanonicke tvary
	 */
	public Set<Vzor> najdiKanonickeTvary(String veta, Set<String> proteiny, Set<String> interakcie) {		
		//System.out.println("Hladame interakcie medzi proteinmi " + proteiny);
		//System.out.println("Interakcie: " + interakcie);

		Set<Vzor> kanonicke = new HashSet<>();
		
		// Obmedzujeme dlzku vety lebo pri velmi dlhych vetach Stanfordsky parser vyhodi vynimku		
		String vetaPom = veta + "";
		for(String znak : Spracovanie_textu.oddelovace) {
			vetaPom = vetaPom.replace(znak, " ");
		}
		vetaPom = vetaPom.trim();
		int dlzkaVety = vetaPom.split(" ").length;
		
		if(predoslaVeta == null || !predoslaVeta.equals(veta)) {
			System.out.println("Dlzka vety: " + dlzkaVety);
		}
		if(dlzkaVety > maxDlzkaVety) {			
			return null; 
		}

		//System.out.println("Veta: " + veta);
		kanonicke = vratKanonicke(veta, proteiny, interakcie);		
		//System.out.println(kanonicke);
		
		//===== Vrati vysledok =====
		return kanonicke;		
	}	
	
// Pripravi vetu tak aby sa spravne analyzovala
private String predpripravNaAnalyzu(String veta) {
	// Osetri spojenie nazvu proteinu so slovom pre nejaky vztah cez pomlcku
	String[] sPomlckami = new String[] {"dependent", "induced", "mediated", "reactive", "derived", "associated"};	
	for(String slovo : sPomlckami) {
		veta = veta.replace("-"+slovo, " "+slovo);
	}

	// Posunie lomitka
	veta = veta.replace("/", ", "); // zameni za ekvivalent lebo lomitko nevie S. parser spracovat
	
	return veta;
}	
	
// Ocakava sa, ze mnozny proteiny budu mat presne dva prvky a interakcie jediny
private Set<Vzor> vratKanonicke(String veta, Set<String> proteiny, Set<String> interakcie) {
	Set<Vzor> kanonicke = new HashSet<>();
	double score = -1; //https://stackoverflow.com/questions/15431139/java-program-to-get-parse-score-of-a-sentence-using-stanford-parser
	
	//-----------------
	String neoznackovanaVeta = veta;
	String vetaPom = veta;
	Set<String> povodneProteiny = new HashSet<>();
	povodneProteiny.addAll(proteiny);
	
	// Vymeni nazvy proteinov za vseobecne znacky
	String vseobNazov = "protein";
	for(String protein : proteiny) {
		veta = veta.replace(protein, vseobNazov);
	}
	
	proteiny.clear();
	proteiny.add(vseobNazov);
		
	//-----------------	
	
	//================ Najde zavislosti ===============
	String vetaNaAnalyzu = predpripravNaAnalyzu(veta);
	
	Collection<TypedDependency> tdl = null;
	
	if(predoslaVeta != null && vetaPom.equals(predoslaVeta) == true) { // Ak mame analyzovat rovnaku vetu len s inymi proeinmi/interakciami
		tdl = predosleTdl;
		score = predosleSkore;
		//System.out.println("---Stacilo nacitat predoslu vetu");
	} else {
		//System.out.println("---Vety sa nerovnali");
		
		
		try {
		Tree tree = lp.parse(vetaNaAnalyzu);
		score = tree.score();	
		GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);				
		tdl = gs.typedDependenciesCCprocessed();				
		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Optimalizacia
	predosleTdl = tdl;
	predoslaVeta = vetaPom;
	predosleSkore = score;
	
	// Vypisanie vztahov vo vete
	//System.out.println(tdl);
	
	//Vypis na vizualizaciu: http://www.webgraphviz.com/
	
	System.out.print("digraph A {");
	for(TypedDependency td : tdl) {
		System.out.print("\"" + td.dep().word() + "-" + td.dep().index() + "\"");
		System.out.print(" -> ");
		System.out.print("\"" + td.gov().word() + "-" + td.gov().index() + "\"" + " ");
	}
	System.out.print("}\n");
	
	/*
	System.out.println(tdl);		
	System.out.println("==================");
	for(TypedDependency td : tdl) {
		System.out.println("1. uzol:");					
		System.out.println("ID: " +td.dep().index());
		System.out.println("Slovo: " +td.dep().word());
		//System.out.println("povodne slovo: " +td.dep().originalText());
		System.out.println("Druh slova: " +td.dep().tag());
		
		System.out.println();
		
		System.out.println("2. uzol:");
		System.out.println("ID: " +td.gov().index());
		System.out.println("Slovo: " +td.gov().word());
		//System.out.println("povodne slovo: " +td.dep().originalText());
		System.out.println("Druh slova: " +td.gov().tag());
		
		System.out.println();
		
		System.out.println("Nazov gramatickeho vztahu: " + td.reln()); // typ vztahu
		System.out.println("==================");
	}
	*/
	
	//============= Zapise zavislosti do podoby grafu =============
	Set<Uzol> proteinyUzly = new HashSet<>();
	Set<Uzol> interakcieUzly = new HashSet<>();
	
	Graf graf = new Graf();
	
	for(TypedDependency td : tdl) {
		//----- Startovny uzol -----
		Uzol u1 = new Uzol();
		u1.id = td.dep().index();
		u1.slovo = td.dep().word();
		u1.druhSlova = td.dep().tag();
		
		// Pozrie sa ci existuje a prida uzol
		if(graf.existujeUzol(u1) == true) {
			Uzol pom = graf.vratPodlaId(u1.id);
			if(pom != null) { // poistka
				u1 = pom;
			}
		} else {
			graf.pridajUzol(u1);
		}
		
		if(u1.slovo == null) continue; // Root zo Stanfordskeho parsera
		
		// Zisti ci je uzol protein
		if(u1.jeProtein == null) { // optimalizacia
			String nazov1 = u1.slovo;
			if(proteiny.contains(nazov1)) {
				u1.jeProtein = true;
				proteinyUzly.add(u1);
			} else {
				u1.jeProtein = false;
			}
		}
		
		// Zisti ci je uzol interakcia
		if(u1.jeInterakcia == null) { // optimalizacia
			String nazov2 = u1.slovo;
			if(interakcie.contains(nazov2)) {
				u1.jeInterakcia = true;
				interakcieUzly.add(u1);
			} else {
				u1.jeInterakcia = false;
			}
		}			
		
		// ----- Cielovy uzol -----
		Uzol u2 = new Uzol();
		u2.id = td.gov().index();
		u2.slovo = td.gov().word();
		u2.druhSlova = td.gov().tag();
		
		// Pozrie sa ci existuje a prida uzol
		if(graf.existujeUzol(u2) == true) {
			Uzol pom = graf.vratPodlaId(u2.id);
			if(pom != null) { // poistka
				u2 = pom;
			}
		} else {
			graf.pridajUzol(u2);
		}
		
		if(u2.slovo == null) continue; // Root zo Stanfordskeho parsera
					
		// Zisti ci je uzol protein
		if(u2.jeProtein == null) { // optimalizacia
			String nazov3 = u2.slovo;
			if(proteiny.contains(nazov3)) {
				u2.jeProtein = true;
				proteinyUzly.add(u2);
			} else {
				u2.jeProtein = false;
			}
		}
		
		// Zisti ci je uzol interakcia
		if(u2.jeInterakcia == null) { // optimalizacia
			String nazov4 = u2.slovo;
			if(interakcie.contains(nazov4)) {
				u2.jeInterakcia = true;
				interakcieUzly.add(u2);
			} else {
				u2.jeInterakcia = false;
			}
		}			
		
		// ----- Hrana medzi uzlami -----
		Hrana h = new Hrana();
		h.dlzkaHrany = 1; // Hodnota hrany je vsade rovnaka
		h.prvy = u1;
		h.druhy = u2;
		h.gramatickyVztah = td.reln().toString();
		
		u1.vychadzajuce.add(h);
		u2.vstupujuce.add(h);				
	}
	
	//System.out.println("Podavalo do grafu");
	
	//============= Najde kanonicky tvar interakcie =============

	//System.out.println(proteinyUzly);
	//System.out.println(interakcieUzly);
	
	
	// Najde najkratsie cesty medzi nimi		
	for(Uzol ui : interakcieUzly) { // Najde cesty ku kazdej interakcii (ak existuju)
		List<List<Uzol>> cestyUzly = new LinkedList<>();
		List<String> znacky = new ArrayList<>();
		
		// Najde najkratsie cesty
		for(Uzol up : proteinyUzly) {	
			//System.out.println("========================");
			//System.out.println("Hlada najkratsiu cestu: " + up + " --> " + ui);
			//System.out.println(veta);
			
			// Pokusi sa najst cestu v jednom smere
			List<Uzol> cesta = graf.najdiNajkratsiuCestu(up, ui, veta);
			
			// Este skusi opacny smer
			if(cesta == null) {
				cesta = graf.najdiNajkratsiuCestu(ui, up, veta);
			}
			
			if(cesta != null) {
				cestyUzly.add(cesta);
				//System.out.println(cesta);					
			}
		}			
		//System.out.println("Pocet ciest: " + cestyUzly.size());
		
		// Prevedie objekty-uzly v ceste na kanonicky retazec (String)
		for(List<Uzol> cesta : cestyUzly) {
			String hash = "";
			for(Uzol u : cesta) {					
				
				//----------- Vrati typ vztahu z vychadzajucej hrany ---------
					// Najde nasledovny uzol na ceste
				Uzol nasledovny = null;
				boolean bolNasUzol = false;
				for(Uzol u1 : cesta) {						
					if(bolNasUzol == true) { // hlada iba nasledovny
						nasledovny = u1;
						bolNasUzol = false;
						break;
					}
					if(u.equals(u1)) bolNasUzol = true; // Pozn. musi byt az na konci
				}
				//System.out.println(u + " --> " + nasledovny);
				
				
					// Najde hranu, ktora spaja tieto dva uzly (Stanfordsky parser robi vzdy jedinu hranu)
				Hrana naCeste = null;
				if(nasledovny != null) {
					for(Hrana h1 : u.vychadzajuce) {
						if(h1.druhy.equals(nasledovny)) {
							naCeste = h1;
							break;
						}
					}						
				}

					// Vrati z hrany typ vztahu
				String typVztahu = "";
				if(naCeste != null) {
					//System.out.println(naCeste);
					typVztahu = naCeste.gramatickyVztah;
				} // Inak je typ vztahu prazdny retazec
				
				
				//System.out.println("Typ vztahu: " + typVztahu );
				
				//---------- Urobi hash z danej cesty aby sa dal pouzit v nasej metode -----------
				String znacka = "";
				if(u.jeProtein == true) znacka = "PROTEIN";
				else if(u.jeInterakcia == true) znacka = "__" + u.slovo; // jedine slovo, ktore piseme (vid. popis metody)
				else znacka = "NEJAKE_SLOVO";
				
				znacka = znacka + "__" + typVztahu;					
				hash = hash + znacka; // vysledny hash					
			}
			znacky.add(hash);
			//System.out.println(hash);
			
		}
											
		// Spoji kazdy s kazdym okrem seba sameho
		// Je zarucene, ze sa vzdy jedna o dva ROZNE uzly/proteiny smerujuce do ROVNAKEJ interakcie
		// Spaja z obidvoch stran
		for(int i=0; i<znacky.size(); i++) {
			for(int j=0; j<znacky.size(); j++) {
				if(i >= j) continue; // same so sebou nespajame, ani nerobime to iste dvakrat
				String kanonicky1 = znacky.get(i) + znacky.get(j);
				String kanonicky2 = znacky.get(j) + znacky.get(i); // Pravidlo iba obratene
				/*
				kanonicky1 = kanonicky1.toLowerCase();
				kanonicky1 = kanonicky1.replace("nejake_slovo", "NEJAKE_SLOVO");
				kanonicky1 = kanonicky1.replace("protein_", "PROTEIN_");
				
				kanonicky2 = kanonicky2.toLowerCase();
				kanonicky2 = kanonicky2.replace("nejake_slovo", "NEJAKE_SLOVO");
				kanonicky2 = kanonicky2.replace("protein_", "PROTEIN_");
				*/				
				
				// Zapise do udajovej struktury, ktora ide na vystup
				String interakcia = "";
				for(String text : interakcie) {interakcia = text; break; }
				
				String oznackovanaVeta = Znackovanie_viet.vlastneOznackovanie(neoznackovanaVeta, povodneProteiny, interakcie, false);				
				//System.out.println("Proteiny na znackovanie: " + povodneProteiny);								
				System.out.println("Oznackovana veta: " + oznackovanaVeta + " s proteinmi " + povodneProteiny + " a s interakciami: " + interakcie);
				
				Vzor v1 = new Vzor();
				v1.vzor = kanonicky1.toLowerCase();
				v1.interakcia = interakcia;
				v1.parsovacieSkore = score;
				v1.proteiny.addAll(proteiny);
				v1.veta = oznackovanaVeta;
				kanonicke.add(v1);
				
				Vzor v2 = new Vzor();
				v2.vzor = kanonicky2.toLowerCase();
				v2.interakcia = interakcia;
				v2.parsovacieSkore = score;
				v2.proteiny.addAll(proteiny);
				v2.veta = oznackovanaVeta;
				kanonicke.add(v2);				
			}
		}			
	}
	//System.out.println("Kanonicke tvary: " + kanonicke);
	return kanonicke;	
}	
	
}












































