package hladanie_interakcii.GRGT;

public class Hrana {
	public Uzol prvy = null;
	public Uzol druhy = null;
	
	public String gramatickyVztah = null;
	public Integer dlzkaHrany = null;	
		
	@Override
	public String toString() {
		return prvy.toString() + " ---[" + gramatickyVztah + "]---> " + druhy.toString();
	}	
	
}
