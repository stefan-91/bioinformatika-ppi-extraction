package stahovanie_clankov;

import java.util.Arrays;
import java.util.List;

import netriedene.Reset_prevadzky;

public class Hlavna_trieda {

	public static void main(String[] args) {
		/*
		String text = "ppi";;
		String[] dopyty = text.split("\\|");
		System.out.println("Rozdelene dopyty: " + Arrays.toString(dopyty));
		*/
		
		
		
		Reset_prevadzky rp = new Reset_prevadzky();
		rp.vymazPrevadzku();
		
		Vyhladanie_id_clankov vic = new Vyhladanie_id_clankov();		
		//String kategoria = "ppi";
		String kategoria = "Cardiovascular diseases ppi";
		
		// Stahovanie z databazy PMC		
		List<String> idClankov = vic.najdiIdClankov("PMC", kategoria, 100000); // Pozn. naostro "PMC", "ppi", "100000"
		StahovaniePMC spmc = new StahovaniePMC();
		spmc.stiahniClanky(idClankov, kategoria);
		//spmc.stianiPDF("");

	}

}
