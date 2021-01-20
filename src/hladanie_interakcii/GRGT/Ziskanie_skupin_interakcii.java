package hladanie_interakcii.GRGT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uprava_suborov.Uprava_suborov;

public class Ziskanie_skupin_interakcii {
	private Set<String> vzory = new HashSet<>();
	private Set<String> interakcie = new HashSet<>();
	
	public Ziskanie_skupin_interakcii() {
		// Nacita vzory interakcii				
		String subVzory = "../materialy/vzory_interakcii/hash/vzory.txt";
		String obsahVzory = Uprava_suborov.vratObsahSuboru(subVzory);
		String[] slova = obsahVzory.split("\n");	
		
		for(String slovo : slova) {
			slovo = slovo.trim();
			if(slovo.length() == 0) continue;
			vzory.add(slovo);
		}		
		System.out.println("Nacitalo " + vzory.size() + " vzorov.");
		
		// Nacita interakcie
		String interakcieStr = Uprava_suborov.vratObsahSuboru("../materialy/nazvy_interakcii/dohromady.txt");
		String[] polozkyInter = interakcieStr.split("\n");
		for(String s : polozkyInter) {
			s = s.trim();
			s = s.toLowerCase();
			if(s.length() == 0) continue;
			interakcie.add(s);
		}	
	}
	
	public void ziskajSkupinyInterakcii() {
		// Ziska objekty vzorov
		VelkaMnozina vzoryS = new VelkaMnozina();
		int pocitadlo = 0;
		for(String vzor : vzory) {
			String novy = vzor;			
			for(String interakcia : interakcie) {				
				if(vzor.contains(interakcia)) {
					novy = novy.replace(interakcia, "INTERAKCIA");					
					Vzor v = new Vzor();
					v.vzor = novy;

					// Prida vzor do mnoziny
					if(vzoryS.contains(v)) { // ak uz existuje tak len modifikujeme existujuci objekt
						// Najde ten vzor
						Vzor najdeny = vzoryS.get(v.vzor);
						//System.out.println("Do objektu " + najdeny.interakcie + " pridava dalsiu interakciu: " + interakcia);
						najdeny.interakcie.add(interakcia);
						vzoryS.update(najdeny);
						
						//vzoryS.add(najdeny);
						//System.out.println("Pridava dalsiu interakciu: " + interakcia);
						//najdeny = vzoryS.get(v.vzor);
						//if(najdeny.interakcie.size() > 0) System.out.println(najdeny);
					} else { // Ak neexistuje tak vytvorime novy objekt					
						v.interakcie.add(interakcia);						
						vzoryS.addNew(v);
						//System.out.println("Pridava novy");	
						//System.out.println("Objekt vonku: " + v);					
						//System.out.println("Objekt po pridani interakcie: " + vzoryS.get(v.vzor));
						//System.out.println("Velkost velkej mnoziny: " + vzoryS.size());
					}
					
					break; // pre istotu ak by bolo vo vzore viac inerakcii (nemalo by sa stavat)					
				}	
				//System.out.println("Velkost velkej mnoziny: " + vzoryS.size());
			}
			
			if(pocitadlo % 1000 == 0) {
				System.out.println("Spracovalo " + pocitadlo + "/" + vzory.size() + " vzorov.");				
			}
			pocitadlo++;
			
			//if(true) break; // Pozn. naostro zakomentovat
			if(pocitadlo > 20000) break; // Pozn. Naostro zakomentovat	
		}

		// Vypis
		System.out.println("Pocet mnozin vo velkej mnozine: " + vzoryS.size());
		vzoryS.vypis();
		
		//------------------ Zluci objekty vzorov (Objavi skupiny/kliky slov) -----------------
		Map<String, Klika> priradenie = new HashMap<>();
		// Kazdemu objektu najde kliku
		for(Set<Vzor> m : vzoryS.mnoziny) {
			for(Vzor v : m) {
				
				
			}
		}
		
		// Zluci rovnake kliky
		
		
		// Vypise kliky
		
	}
	
	private class Klika {
		public Set<String> vzory = new HashSet<>();
		public Set<String> interakcie = new HashSet<>();		
		
	}

	
	
	private class Vzor {
		public String vzor = null;
		public Set<String> interakcie = new HashSet<>();
		
		@Override
		public boolean equals(Object o) {
		    return ((Vzor)o).vzor.equals(this.vzor);
		} 
		
		@Override
		public int hashCode() {
		    return vzor.hashCode();
		}	
		
		@Override
		public String toString() {
		    return "Vzor: " + vzor + "\n" + "Interakcie: " + interakcie.toString();
		}
		
	}
	
	// Ked je v mnozine poloziek nad 100 000, tak vkladanie zacne byt extremne pomale
	private class VelkaMnozina {
		private List<Set<Vzor>> mnoziny = new LinkedList<>();
	
		public int size() {
			int sucet = 0;
			for(Set<Vzor> m : mnoziny) {
				sucet  =sucet + m.size();
			}			
			return sucet;
		}
		
		public void vypis() {
			int sucet = 0;			
			for(Set<Vzor> m : mnoziny) {
				//System.out.println(m);
				for(Vzor v : m) {
					if(v.interakcie.size() > 1) {
						//System.out.println(v + "\n");
						System.out.println(v.interakcie.size());	
					}
					//System.out.println(v + "\n");
					sucet++;
				}				
			}
			System.out.println("Sucet vzorov je " + sucet);
		}
		
		/**
		 * 
		 * @param vzorStr
		 * @return
		 * 		Ak nenajde nic
		 */
		public Vzor get(String vzorStr) {
			Vzor pom = new Vzor();
			pom.vzor = vzorStr;
			for(Set<Vzor> m : mnoziny) {
				if(m.contains(pom)) {
					for(Vzor v : m) {
						if(v.equals(pom)) {
							return v; // pom nevraciame!
						}
					}					
				}
			}			
			return null;	
		}		
		
		public boolean contains(Vzor vzor) {
			for(Set<Vzor> m : mnoziny) {
				if(m.contains(vzor)) {
					return true;
				}
			}			
			return false;
		}
		
		/**
		 * 
		 * @param vzor
		 * 		Vzor uz muzi byt aktualizovany, tu sa len nanovi vlozi do velkej mnoziny
		 */
		public void update(Vzor vzor) {
			//System.out.println("Pridava " + vzor.interakcie);
			//System.out.println("------ 1. ------");
			//vypis();		
			remove(vzor);
			//System.out.println("Vzor uz neexistuje " + !contains(vzor));
			//System.out.println("------ 2. -------");
			//vypis();			
			addNew(vzor);
			//System.out.println("Vzor " + vzor.interakcie + " opat existuje " + contains(vzor));
			//System.out.println("jedna sa o " + get(vzor.vzor));
			//System.out.println("------ 3. ------");
			//vypis();			
		}
		
		public void remove(Vzor vzor) {
			for(Set<Vzor> m : mnoziny) {
				m.remove(vzor);
			}			
		}
		
		// Pozn. Nepouziva sa
		public void add(Vzor vzor) {
			if(mnoziny.size() == 0) { // velka mnozina je uplne prazdna
				//System.out.println("Pridava vzor ako prvy prvok " + vzor.vzor);
				addNew(vzor);
			} else { // vo velkej mnozine uz nieco existuje
				//System.out.println("Pridava vzor ako dalsi " + vzor.vzor);
				boolean obsahuje = false;
				Set<Vzor> naslo = null;
				Vzor najdeny = null;
				for(Set<Vzor> m : mnoziny) {
					if(m.contains(vzor)) {
						najdeny = get(vzor.vzor);
						naslo = m;
						obsahuje = true;
					}									
				}
				
				if(obsahuje == true) {
					//System.out.println("Aktualizuje existujucu interakciu: " + vzor.vzor);
					update(vzor);					
					//System.out.println("Stare: " + najdeny.interakcie + ", nove: " + vzor.interakcie);					
					//naslo.remove(najdeny);
					//naslo.add(najdeny);					
					//System.out.println("Edituje obsah objektu: " + najdeny.interakcie);										
				} else {
					//System.out.println("Pridava uplne novu interakciu: " + vzor.vzor);
					addNew(vzor);								
				}
				
			}				
		}
		
		public void addNew(Vzor vzor) {
			boolean obsadilo = false;
			Set<Vzor> pridava = null;
			
			for(Set<Vzor> m : mnoziny) {
				if(m.size() <= 50000) {
					// Musime vymazat lebo by sa inak nanovo nepridal (s aktualizovanymi atributami)
					// Vid. https://stackoverflow.com/questions/12940663/does-adding-a-duplicate-value-to-a-hashset-hashmap-replace-the-previous-value					
					pridava = m;
					obsadilo = true;
					break;
				}
			}
						
			if(obsadilo == false) { // vytvarame uplne novu mnozinu
				pridava = new HashSet<>();
				pridava.add(vzor);
				mnoziny.add(pridava);
				//System.out.println("Vytvara novy mnozinu " + m);
			} else {						
				//System.out.println("Vklada do existujucej mnoziny " + m);
				//System.out.println("Pridava " + pridava);
				mnoziny.remove(pridava);
				pridava.add(vzor);				
				mnoziny.add(pridava);			
			}
			
			//System.out.println("Do tejto mnoziny pridalo: " + pridava);
			
		}
		
	}	
	
	
	// Kvazi unit test
	public void otestujVelkuMnozinu() {
		VelkaMnozina vm = new VelkaMnozina();
		Vzor v1 = new Vzor(); v1.vzor = "text1";
		Vzor v2 = new Vzor(); v2.vzor = "text2";
		Vzor v3 = new Vzor(); v3.vzor = "text3";
		Vzor v4 = new Vzor(); v4.vzor = "text4";
		Vzor v5 = new Vzor(); v5.vzor = "text5";
		
		// test
		vm.add(v1);
		System.out.println("----------------- \npridava jeden prvok:");
		vm.vypis();
		System.out.println("-------------------");
		
		// test
		System.out.println("Obsahuje prvy prvok: " + vm.contains(v1));
		
		// test
		vm.add(v2);
		System.out.println("----------------- \npridava druhy prvok:");		
		vm.vypis();
		System.out.println("-------------------");
		
		// test
		System.out.println("Obsahuje druhy prvok: " + vm.contains(v2));
		
		// test
		vm.add(v3);
		vm.add(v4);
		vm.add(v5);
		
		// test
		Vzor v6 = new Vzor(); v6.vzor = "text2";
		System.out.println("Obsahuje (fyzicky iny objekt): " + vm.contains(v6));
		
		// test
		Vzor vx = new Vzor();
		vx.vzor = "text1";
		vx.interakcie.add("nieco1");
		vx.interakcie.add("nieco2");
		System.out.println("------------------");
		System.out.println("Ide aktualizovat: " + vx);
		vm.update(vx);
		
		/*
		// test na aktualizaciu
		v1.interakcie.add("nieco1");
		v1.interakcie.add("nieco2");
		System.out.println("Ide aktualizovat: " + v1);
		vm.add(v1);
		System.out.println("Aktualizovalo teno prvok, ktory je uz vo vel. mnozine: " + vm.get(v1.vzor));
		*/
	}	
}




























































