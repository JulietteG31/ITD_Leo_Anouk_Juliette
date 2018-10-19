package tsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fourmi {
	
	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int villeActuelle;
	private int etat; // 0:au départ 1:aller 2:retour
	private int distance; // somme de toutes les distances parcourues par la fourmi
	
	private Colonie colonie;
	
	
	public Fourmi(ArrayList<Integer> villesVisitees, ArrayList<Integer> villesRestantes, int villeActuelle, int etat,
			int distance, Colonie colonie) {
		this.villesVisitees = villesVisitees;
		this.villesRestantes = villesRestantes;
		this.villeActuelle = villeActuelle;
		this.etat = etat; // 0:Au depart 1:aller 2:retour
		this.distance = distance; // somme des distances parcourus par la fourmi
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
	

	/*
	 * Retourne un Hashmap avec en clé le numéro de la ville et en valeur la probabilité
	 */
	public HashMap<Integer,Double> probabilitesVillesPossibles() throws Exception {
		double alpha = 1.0;
		double beta = 1.0;

		ArrayList<Integer> prochainesVillesPossibles = this.prochainesVillesPossibles();
		
		HashMap<Integer,Double> probabilites = new HashMap();
		double probabilite;		 
		
		/*
		 * On calcule pour chaque ville la probabilité de la choisir comme prochaine
		 * destination
		 */
		double sommeProbabilites = 0.0;
		for(int ville : prochainesVillesPossibles) {
			probabilite = Math.pow(1/this.colonie.getInstance().getDistances(this.villeActuelle, ville), alpha)*Math.pow(this.colonie.getPheromones(this.villeActuelle, ville), beta);
			sommeProbabilites += probabilite;
			probabilites.put(ville, probabilite);
		}
		/* 
		 * On ajuste les probabilités pour que la somme de toutes vaille 1
		 */
		for(int ville : prochainesVillesPossibles) {
			probabilites.put(ville, probabilites.get(ville)/sommeProbabilites);
		}
		
		return probabilites;
	}

	
	public int NextStep() throws Exception {
		
		// boucle qui retourne somme (autre)
		//ponderation de chacune des prochainesVillesPossibles probabilite=(phero^alpha*(1/d)^Beta)/ somme
		int i=0;
		int villeSuivante=0;
		HashMap<Integer,Double> proba = this.probabilitesVillesPossibles(); 
		ArrayList<Integer> prochainesVillesPossibles = prochainesVillesPossibles();
		while (i==0) {
			if(Math.random()<= proba.get(prochainesVillesPossibles.get(i))) {
				villeSuivante=prochainesVillesPossibles.get(i);
				i+=1;
			}
			else {
				prochainesVillesPossibles.remove(0);
				for (int villesuiv: prochainesVillesPossibles) {
					proba.put(villesuiv, proba.get(villesuiv)/(1.0-proba.get(prochainesVillesPossibles.get(i))));
				}
			}
		}
			return villeSuivante ;
<<<<<<< HEAD
		}

=======
	}
	
	public void deposerPheromones() throws Exception {
		/*
		 * Il faut prendre les arcs de la ville n à n+1
		 * puis incrémenter l'arc en question sur les phéromones
		 */
		int villeA;
		int villeB;
		int n = this.villesVisitees.size();
		
		if(n >= 2) {
			for(int i = 0; i < n-1; i++) {
				villeA = this.villesVisitees.get(i);
				villeB = this.villesVisitees.get(i+1);
				// Il faut toujours que villeA < villeB
				villeA = (villeA < villeB) ? villeA : villeB;
				
				this.colonie.incPheromones(villeA, villeB, 1);
			}
		}
	}
>>>>>>> branch 'master' of https://github.com/JulietteG31/ITD_Leo_Anouk_Juliette.git
} 
