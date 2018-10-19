package tsp;

import java.util.ArrayList;
import java.util.List;

public class Fourmi {
	
	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int villeActuelle;
	private int etat;// 0:au depart 1:aller 2:retour
	private int distance; // somme des distances parcourues par la fourmi
	
	private Colonie colonie;
	
	
	public Fourmi(ArrayList<Integer> villesVisitees, ArrayList<Integer> villesRestantes, int villeActuelle, int etat,
			int distance, Colonie colonie) {
		this.villesVisitees = villesVisitees;
		this.villesRestantes = villesRestantes;
		this.villeActuelle = villeActuelle;
		this.etat = etat;
		this.distance = distance;
		this.colonie = colonie;
	}
	
	public Fourmi(Colonie colonie) {
		this(new ArrayList<Integer>(),new ArrayList<Integer>(),0,0,0, colonie);
		for(int i=0;i<442;i++) {
			this.villesRestantes.add(i);
		}
	}
	
	public ArrayList<Integer> prochainesVillesPossibles() {
		int critere=100;
		long[][] distances = this.colonie.getInstance().getDistances();
		ArrayList<Integer> prochainesVillesPossibles = new ArrayList<Integer>();
		for(int ville : this.villesRestantes) {
			if(distances[villeActuelle][ville]<=critere) {
				prochainesVillesPossibles.add(ville);
			}
		}
		return prochainesVillesPossibles;
	}

}
