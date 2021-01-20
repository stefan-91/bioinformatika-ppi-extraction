package ziskavanie_nazvov_proteinov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uprava_suborov.Uprava_suborov;

public class Parsovanie_csv {

	// Nacita do mnoziny jeden zvoleny stlpec 
	// Nazov musi byt minimalne 3 znaky dlhy
	public Set<String> vyberStlpec(String cesta, String oddelovac, int poradie) {
		Set<String> nazvy = new HashSet<>();
		
		File file = new File(cesta);
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    int pocitadlo = 0;
		    while ((line = br.readLine()) != null) {
		    	if(pocitadlo == 0) { // Preskakuje nazvy stlpcov
		    		pocitadlo++;
		    		continue;
		    	} 
		    	
		       //System.out.println(line);
		       String[] polozky = line.split(oddelovac);
		       //System.out.println(Arrays.toString(polozky));
		       if(polozky.length >= 1 && polozky[0].length() > 2) {
		    	   
		    	   nazvy.add(polozky[0]);
		       }
		       
		       // Iba pri protein atlas (synonyma)
		       if(polozky.length >= 2) {
		    	   polozky[1] = polozky[1].replace('"',' ');
		    	   String[] synonyma = polozky[1].split(",");
		    	   for(String syn : synonyma) {
		    		   syn = syn.trim();
		    		   if(syn.length() > 2) {				    	   
		    			   nazvy.add(syn);
		    		   }		    		   
		    	   }		    	   
		       }	
		       
		       // Info vypis
		       
		       if(pocitadlo % 100 == 0) {
		    	   System.out.println(pocitadlo + ", pocet nazvov: " + nazvy.size());
		       }
		       
		       pocitadlo++;		       
		    }
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
						
		return nazvy;
	}	
	
	// Vsetky udaje zo subory pred zapisom vymaze
	public void zapisStlpec(Collection<String> data, String cesta) {
		Uprava_suborov.vytvorSubor(cesta);
		
		for(String slovo : data) {
			Uprava_suborov.pridajNaKoniecDoSuboru(cesta, slovo);
		}
	}
	
	
}




