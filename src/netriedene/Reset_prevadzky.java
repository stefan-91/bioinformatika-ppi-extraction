package netriedene;

import databazaSQLite.Databaza;
import uprava_suborov.Uprava_suborov;

public class Reset_prevadzky {

	// Vymaze vsetky udaje aby sa mohol proces rpevadzky spustit nanovo
	// Inak sa vsetko iba pridava
	public void vymazPrevadzku() {
		// Vymaze 3 adresare
		Uprava_suborov.vymazObsahPriecinka(MyData.stiahnuteClanky);
		Uprava_suborov.vymazObsahPriecinka(MyData.predspracovaneClanky);
		Uprava_suborov.vymazObsahPriecinka(MyData.anotovaneClanky);
		Uprava_suborov.vymazObsahPriecinka(MyData.ineFormatyClankov);
		
		// Vymaze data v prevadzkovej databaze
		Databaza db = new Databaza();
		db.nastavCestuDB(MyData.cestaDatabazaPrevadzka);
		db.aktualizujDB("DELETE FROM Dopyty;");
		db.aktualizujDB("DELETE FROM Originalne_texty_URL;");	
		db.aktualizujDB("DELETE FROM Stiahnute_clanky;");		
		
		System.out.println("Vymazalo vsetky vysledky z procesu \"prevadzka\".");
	}
	
}
