package netriedene;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Funckei pee spracovanie textu su na jednom mieste aby sa predislo duplikovanemu kodu
 *
 */
public class Spracovanie_textu {
	public static String[] oddelovace = new String[] {" ","-", ".", ",", ";", ":", "(", ")", "~", "/"};
	
	public static Set<Integer> vratPozicie(String veta, String slovo) {
		Set<Integer> pozicie = new HashSet<Integer>();
		
		int index = veta.indexOf(slovo);
		while(index >= 0) {
			pozicie.add(index);
		    index = veta.indexOf(slovo, index+1);		    
		}

		return pozicie;
	}
	
	// Ak je rovnaky protein, tak nevieme, ktory je ten spravny
	public static String rozsirZnacky(String veta) {
		String zac = "<protein>";
		String konc = "</protein>";
		
		Set<String> prot = vratObsahZnaciek(veta, zac, konc);
		
		for(String p : prot) {
			//System.out.println("|"+p+"|");
			//System.out.println("Vyskyt "+p+": " + vratPocetVyskytov(veta,p));
			veta = veta.replace(zac+p+konc, p);
			veta = veta.replace(p, zac+p+konc);
		}
		
		return veta;
	}
	
	// Zdroj: https://stackoverflow.com/questions/767759/occurrences-of-substring-in-a-string
	public static int vratPocetVyskytov(String text, String substr) {
		String str = text;
		String findStr = substr;
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
	
	
	
	/**
	 * Odstrihne okraje z vety po znacky.
	 * 
	 * @param veta
	 * 		Oznackovana veta
	 * @return
	 * 		Skratena veta
	 */
	public static String skratPoZnacky(String veta, Set<String> znacky) {
		// Najde najlavejsi koniec
		int vlavo = veta.length() + 1; // maximalna dlzka
		for(String znacka : znacky) {
			//znacka = "<" + znacka + ">";
			int pozicia = veta.indexOf(znacka);
			if(vlavo > pozicia) vlavo = pozicia; // hladame najlavejsiu poziciu 
		}		
		if(vlavo >= 0) veta = veta.substring(vlavo); // odstrihne
				
		// Najde najpravejsi koniec
		int vpravo = 0;
		int dlzkaZnacky = 0;
		for(String znacka : znacky) {
			//znacka = "</" + znacka + ">";			
			
			int pozicia = veta.lastIndexOf(znacka);
			if(vpravo < pozicia) {
				vpravo = pozicia; // hladame najpravejsiu poziciu
				dlzkaZnacky = znacka.length();
			}
			
		}		
		if(vpravo >= 0) veta = veta.substring(0, vpravo + dlzkaZnacky); // odstrihne
				
		return veta;		
	}
	
	/**
	 * Ak je znaciek viac, vrati vsetky obsahy
	 * @param veta
	 * @param zaciatocnaZnacka
	 * @param koncovaZnacka
	 * @return
	 * 		
	 */
	public static Set<String> vratObsahZnaciek(String veta, String zaciatocnaZnacka, String koncovaZnacka) {
		Set<String> obsah = new HashSet<>();
		
		// Rozdeli vetu na slova (znacka nesmie mat v sebe biele znaky)
		veta = veta.replace(zaciatocnaZnacka, " " + zaciatocnaZnacka + " ");
		veta = veta.replace(koncovaZnacka, " " + koncovaZnacka + " ");
		
		String[] vetaArr = veta.split(" ");
		boolean citame = false;
		boolean start = false; // kontrola aby sme do obsahu nezapisali aj text znacky
		String text = "";
		for(String slovo : vetaArr) {
			slovo = slovo.trim();
						
			if(slovo.equals(zaciatocnaZnacka)) {
				citame = true;
				start = true;
			}
			
			if(slovo.equals(koncovaZnacka)) {
				citame = false;
				// Zapiseme co sme medzitym ziskali
				text = text.trim();
				obsah.add(text);
				text = "";
			}
			
			if(citame == true && start == false) {
				text = text + " " + slovo;
			}			
			
			start = false;
		}
				
		return obsah;
	}	
}
