package hladanie_interakcii.GRGT;

import java.util.HashSet;
import java.util.Set;

public class Vzor {
	public String vzor = null;
	public String veta = "";
	public Set<String> proteiny = new HashSet<>(); // Ocakavaju sa dva proteiny
	public String interakcia = null;
	public double parsovacieSkore = 0;
	
	@Override
	public String toString() {
		return vzor;
	}
	
	@Override
	public boolean equals(Object o) { 
		// instanceof Check and actual value check
		if (o instanceof Uzol) {
			if(((Vzor) o).veta.equals(veta)) {
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
		return veta.hashCode();
	}	
}
