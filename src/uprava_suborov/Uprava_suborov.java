package uprava_suborov;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class Uprava_suborov {

public static boolean existujeSubor(String cesta) {
	File f = new File(cesta);
	if(f.exists()) { 
	    return true;
	} else {
		return false;
	}		
}	
	
	// NEvracia nazvy priecinkov
public static Set<String> vratVsetkyNazvySuborov(String cesta) {
	Set<String> nazvy = new HashSet<>();
	
	File folder = new File(cesta);
	File[] listOfFiles = folder.listFiles();

	for (int i = 0; i < listOfFiles.length; i++) {
	  if (listOfFiles[i].isFile()) {
	    //System.out.println("File " + listOfFiles[i].getName());
		  nazvy.add(listOfFiles[i].getName());
	  } else if (listOfFiles[i].isDirectory()) {
	    //System.out.println("Directory " + listOfFiles[i].getName());
	  }
	}	
	
	return nazvy;
}	

public static String vratObsahSuboru(String cesta) {
	String obsah = null;
		try {
			obsah = new String(Files.readAllBytes(Paths.get(cesta)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
	}		
	return obsah;
}

public static long vratVelkostSuboru(String cesta) {
	java.io.File file = new java.io.File(cesta);
	return file.length();	
}	
	
public static void vymazSubor(String cesta) {	
	try{		
		File file = new File(cesta);
    	
		if(file.delete()){
			//System.out.println(file.getName() + " is deleted!");
		}else{
			System.err.println("Subor sa nepodarilo vymazat: " + cesta);
		}	   
	}catch(Exception e){		
		e.printStackTrace();		
	}	
}	
	
public static String upravStringDoSQL(String riadok) {
	if(riadok == null) riadok = "";
	if(riadok.equals("NULL")) riadok = "";

    riadok = riadok.replace("\\", "\\\\"); //Spatne zatvorky //MUSI byt prve aby nepokazilo nasledovne upravy
    riadok = riadok.replace("'", "\\'"); //apostrofy
    riadok = riadok.replace("\"", "\\\""); //uvodzovky

	return riadok;
}	
	
public static void vymazObsahPriecinka(String cesta) {
	File directory = new File(cesta);
	File[] files = directory.listFiles();
	if(files != null) { 
		for (File file : files) {
			if(!file.delete()) {
				System.out.println("Failed to delete " + file);
			}
		}
	}
}	
	
public static void pridajNaKoniecDoSuboru(String cesta, String riadok) {
    riadok = riadok + "\n";
    String nazovSuboru = cesta;    
    try {
        Files.write(Paths.get(nazovSuboru), riadok.getBytes(), StandardOpenOption.APPEND);
    }catch (IOException e) {
        //exception handling left as an exercise for the reader
    	e.printStackTrace();
    }       
    //System.out.println("Zapisalo do suboru");
}

public static void vytvorSubor(String cesta) {
    //Zmaze stary subor ak existuje
    File file = new File(cesta);
    try {        
        Files.deleteIfExists(file.toPath());
    } catch(Exception e) {e.printStackTrace();}
    
    //Vytvori novy subor
    try { 
        file = new File(cesta);
        file.createNewFile();
    } catch(Exception e) {e.printStackTrace();}      
    
}
	
}
