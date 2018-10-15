package tsp;

import java.util.ArrayList;
import java.util.List;

public class Fourmi {
	
	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int villeActuelle;
	private int etat;
	private int distance;
	
	
	public Fourmi(ArrayList<Integer> villesVisitees, ArrayList<Integer> villesRestantes, int villeActuelle, int etat,
			int distance) {
		this.villesVisitees = villesVisitees;
		this.villesRestantes = villesRestantes;
		this.villeActuelle = villeActuelle;
		this.etat = etat;
		this.distance = distance;
	}
	
	public Fourmi() {
		this(new ArrayList<Integer>(),new ArrayList<Integer>(),0,0,0 );
		for(int i=0;i<442;i++) {
			this.villesRestantes.add(i);
		}
	}
	
	public ArrayList<Integer> prochainesVillesPossibles() {
		int critere;
		//long[][] distances = Colonie.getInstance().getDistance();
		ArrayList<Integer> prochainesVillesPossibles = new ArrayList<Integer>();
		for(int ville : this.villesRestantes) {
			/*if(distances[villeActuelle][ville]<=critere) {
				prochainesVillesPossibles.add(ville);
			}*/
		}
		return prochainesVillesPossibles;
	}
	
	

}