package tsp;

import java.util.ArrayList;
import java.util.List;

public class Fourmi {
	
	private ArrayList<Integer> noeudsVisites;
	private ArrayList<Integer> noeudsRestants;
	private int noeudActuel;
	private int etat;
	private int distance;
	
	
	public Fourmi(ArrayList<Integer> noeudsVisites, ArrayList<Integer> noeudsRestants, int noeudActuel, int etat,
			int distance) {
		this.noeudsVisites = noeudsVisites;
		this.noeudsRestants = noeudsRestants;
		this.noeudActuel = noeudActuel;
		this.etat = etat;
		this.distance = distance;
	}
	
	public Fourmi() {
		this(new ArrayList<Integer>(),new ArrayList<Integer>(),0,0,0 );
		for(int i=0;i<442;i++) {
			this.noeudsRestants.add(i);
		}
	}
	
	

}
