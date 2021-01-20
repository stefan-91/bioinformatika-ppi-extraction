package hladanie_interakcii.GRGT;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

public class Uzol {
	public Integer id = null; // ID

	// Hrany napojene na tento uzol
	public List<Hrana> vychadzajuce = new LinkedList<>();
	public List<Hrana> vstupujuce = new LinkedList<>();
	
	// Obsah uzla
	public String slovo = null;
	public String druhSlova = null;
	public Boolean jeProtein = null;
	public Boolean jeInterakcia = null;
	
	// Na grafove prehladavanie
	public Set<Uzol> predchodcovia = new HashSet<>();	
	
	@Override
	public boolean equals(Object o) { 
		// instanceof Check and actual value check
		if (o instanceof Uzol) {
			if(((Uzol) o).id == id) {
				return true;
			} else {
				return false;
			}					
		} else {
			return false;
		}
	}
 
	@Override
	public int hashCode() {				
		return id;
	}
	
	@Override
	public String toString() {
		return slovo + "(" + druhSlova + ")";
	}
}


































































