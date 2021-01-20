package databazaSQLite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Databaza {
	//Navod na pripojenie k sql lite: 
	//http://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/
	
    private Connection connection;
    String cesta = "../korpus/KorpusPPI_dalsie"; //Adresarova cesta k databazovemu suboru
    
    public Databaza() {
        connection = this.vratSpojenie();
    }

    public void nastavCestuDB(String cesta) {
    	this.cesta = cesta;
    	connection = this.vratSpojenie();
    }
    
    public void ukonciSpojenie() {    	
        try {
        	connection.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }	
    }      
    
	public String pripravNaVlozenie(String riadok) {
        riadok = riadok.replaceAll("„", " ");
        riadok = riadok.replaceAll("“", " ");
        riadok = riadok.replaceAll("\"", " "); //uvodzovky
        riadok = riadok.replaceAll("\\\\", " ");
        riadok = riadok.replaceAll("'", "\\\\'"); 
        riadok = riadok.trim();
        return riadok;
	}      
    
public void vymazStlpec(String tabulka, String stlpec) {
	String[][] stlpceArr = vratPole("PRAGMA table_info(" + tabulka + ");");
	
	this.aktualizujDB("ALTER TABLE " + tabulka + " RENAME TO " + tabulka + "_old;");
	
	String stlpce = "";
	for(int i=0; i<stlpceArr.length; i++) {
		if(stlpec.equals(stlpceArr[i][1])) continue;
		stlpce = stlpce + "," + stlpceArr[i][1];
	}
	stlpce = stlpce.substring(1);
	
	this.aktualizujDB("CREATE TABLE " + tabulka + " AS SELECT " + stlpce + " FROM " + tabulka + "_old;");		
	this.aktualizujDB("DROP TABLE " + tabulka + "_old;");		
	
}    
    
//U - dokoncit    
public void exportujTabulku() {

}    

    public String aktualizujDB(String mySQL) {
        String stav = "";
        
		try {
			Statement stmt = connection.createStatement();

			//String sql = "INSERT INTO course " + "VALUES (course_code, course_desc, course_chair)";
			stav = stav + stmt.executeUpdate(mySQL);
			
			//stmt.executeQuery(mySQL);
						
			stmt.close();						
		  
		} catch(Exception e) {
			System.err.println("Chybny prikaz: " + mySQL);
			//System.err.println(e.getMessage());
			e.printStackTrace();
            return e.getMessage();
		}
        
        //System.out.println(stav);
        return stav; //ak je v poriadku
	}
	
	public Connection vratSpojenie() {
		try {
			
		String url = "jdbc:sqlite:" + cesta + ""; 
		Connection connection = DriverManager.getConnection(url);
		//System.out.println("Connection to SQLite has been established.");
		
		return connection;
		} catch(Exception e) {e.printStackTrace();}
		
		return null;
	}
	
    private ArrayList<String> nacitajData() {
        ArrayList<String> data = new ArrayList<String>();
        String line;

        try {

            BufferedReader bufferreader = new BufferedReader(new FileReader("Data.txt"));
            line = bufferreader.readLine();
            while (line != null) {
                line = line.trim();
                if(line.length() > 0) data.add(line);
                line = bufferreader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return data;
    }
    
    //Ked nevrati nic, tak vystupom je pole nulovej dlzky
	public String[][] vratPole(String mySQL) {
		List<String[]> table;
		String[][] matrix;
        
		try {
			Statement stmt = connection.createStatement();		
			ResultSet result = stmt.executeQuery(mySQL);
						
			int nCol = result.getMetaData().getColumnCount();
			table = new LinkedList<>();
			
			while( result.next()) {
				 String[] row = new String[nCol];
				 for( int iCol = 1; iCol <= nCol; iCol++ ){
							Object obj = result.getObject( iCol );
							row[iCol-1] = (obj == null) ?null:obj.toString();
				 }
				 table.add( row );
			}
					  
			stmt.close();									
		  
		} catch(Exception e) {
			//System.err.println(e.getMessage());
			//System.err.println("funkcia VratPole vratila NULL");
            matrix = new String[1][2];
            matrix[0][0] = null;
            matrix[0][1] = e.getMessage();
			return matrix;
		}		
	
		//Konverzia na 2D pole
		matrix =new String[table.size()][];
		matrix=table.toArray(matrix);
						
		return matrix;
	}    
    
	//========== Uprava systemu tabuliek ============
	
	//Odstrani stlpce z tabulky
	//SQLITE musi vediet ktore stlpce ostavaju
	public void odstranStlpce(String[] ostavajuce, String tabulka) {
		this.aktualizujDB("ALTER TABLE " + tabulka + " RENAME TO " + tabulka + "_old;");
		
		String stlpce = "";
		for(int i=0; i<ostavajuce.length; i++) {
			stlpce = stlpce + "," + ostavajuce[i];
		}
		stlpce = stlpce.substring(1);
		
		this.aktualizujDB("CREATE TABLE " + tabulka + " AS SELECT " + stlpce + " FROM " + tabulka + "_old;");		
		this.aktualizujDB("DROP TABLE " + tabulka + "_old;");						
	}
	
	//Prida ID lubovolnej tabulke
	public void pridajID(String tabulka, String kluc, String idNazov) {		
		this.aktualizujDB("ALTER TABLE " + tabulka + " ADD COLUMN " + idNazov + " INTEGER;");
		
		//Nacita kluce		
		String[][] kluceArr = this.vratPole("SELECT " + kluc + " FROM " + tabulka + ";");
		int pocitadlo = 0;
		
		String sql = "";					
		String podmienka = "";
		String podmienkaKoniec = "";
		
		for(String[] klucTab : kluceArr) {
			podmienka = podmienka + " WHEN " + kluc + " = '" + klucTab[0] + "' THEN " + pocitadlo + " ";			
			podmienkaKoniec = podmienkaKoniec + ",'" + klucTab[0] + "'";
			pocitadlo++;
			
			if(pocitadlo % 1000 == 0 && pocitadlo > 0) {
				podmienkaKoniec = podmienkaKoniec.substring(1);							
				sql = "UPDATE " + tabulka + " SET " + idNazov + " = CASE " + podmienka + " END WHERE " + kluc + " IN (" + podmienkaKoniec + ")";
				this.aktualizujDB(sql);
				//System.out.println(sql);
				System.out.println("Pridava ID do " + tabulka + ": " + pocitadlo);
				sql = "";
				podmienkaKoniec = "";
				podmienka = "";
			}			
		}
		podmienkaKoniec = podmienkaKoniec.substring(1);	
		sql = "UPDATE " + tabulka + " SET " + idNazov + " = CASE " + podmienka + " END WHERE " + kluc + " IN (" + podmienkaKoniec + ")";
		this.aktualizujDB(sql); //este posledne zapise
		
	}		
	
    /*
  //U - dokoncit    
  public void importujSubor(String subor) {


      String aSQLScriptFilePath = subor;

      try {
          // Initialize object for ScripRunner
          ScriptRunner sr = new ScriptRunner(connection, false, false);

          // Give the input file to Reader
          Reader reader = new BufferedReader(
                             new FileReader(aSQLScriptFilePath));

          // Exctute script
          sr.runScript(reader);

      } catch (Exception e) {
          System.err.println("Failed to Execute" + aSQLScriptFilePath
                  + " The error is " + e.getMessage());
      }
  }

      */    
    
}