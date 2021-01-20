package stahovanie_clankov;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import databazaSQLite.Databaza;

public class Sprava_metadat {
	private Databaza db = new Databaza();;

	// Kvoli spojeniu s databazou
	public void myDestructor() {
		db.ukonciSpojenie();
	}
	
	/**
	 * 
	 * @param id
	 * 		Napriklad kanonicka URL pre clanok
	 *
	 */
	public void pridajDopyt(String id, String dopyt) {
		String sql = "SELECT ID, dopyty FROM Dopyty WHERE ID = '" + id + "'";
		String[][] pole = db.vratPole(sql);
		
		if(pole.length == 0) { // vlozi uplne novy zaznam
			sql = "INSERT INTO Dopyty(ID, dopyty) VALUES ('"+id+"','"+dopyt+"')";
			db.aktualizujDB(sql);
		} else { // aktualizuje novy zaznam						
			// Nacita stare dopyty do mnoziny
			String[] dopyty = pole[0][1].split("\\|");
			Set<String> dopytySet = new HashSet<>();
			for(String dopytMinuly : dopyty) {
				dopytMinuly = dopytMinuly.trim();
				dopytySet.add(dopytMinuly);
			}
			
			// Prida novy
			dopytySet.add(dopyt); // ak existuje, tak je v mnozine ulozeny vzdy iba raz
			String data = "";
			for(String dop : dopytySet) {
				data = data + "|" + dop;
			}
			if(data.length() > 0) data = data.substring(1); // da prec prvy oddelovac "|"
				
			sql = "UPDATE Dopyty SET dopyty = '"+data+"' WHERE id = '"+id+"'";
			db.aktualizujDB(sql);
		}						
	}
}


























































