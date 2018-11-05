package tsp;

import java.util.ArrayList;
import java.util.List;

import tsp.neighborhood.ANeighborhood;

public class PlusProchesVoisins {
	
	// -----------------------------
	// ----- ATTRIBUTS -------------
	// -----------------------------

	ArrayList<Integer> villesRestantes;
	ArrayList<Integer> villesVisitees;
	int villeDepart;
	int villeActuelle;
	int distance = 0;
	Instance m_instance;
	Solution m_solution;
	
	// -----------------------------
	// ----- CONSTRUCTOR -----------
	// -----------------------------
	
	/**
	 * Constructor
	 * @param instance l'instance du probleme
	 * @param solution
	 * @throws Exception
	 */
	public PlusProchesVoisins(Instance instance, Solution solution, int villeDepart) throws Exception {
		m_instance = instance;
		m_solution = solution;		
		
		villeDepart = villeDepart;
		villeActuelle = villeDepart;
		villesRestantes = new ArrayList<Integer>();
		villesVisitees = new ArrayList<Integer>();
		
		for(int i = 1; i < this.m_instance.getNbCities(); i++) {
			villesRestantes.add(i);
		}
		villesVisitees.add(villeDepart);
		
		this.avancer();
	}
	
	// -----------------------------
	// ----- METHODS ---------------
	// -----------------------------

	/**
	 * Donne la prochaine ville sur laquelle va se rendre le voyageur
	 * @return villeChoisie, le numero de la prochaine ville
	 * @throws Exception
	 */
	public int prochaineVille() throws Exception {
		int villeChoisie = -1;
		double distanceMin = -1;
		double distance;
		
		for(int ville : this.villesRestantes) {
			distance = this.m_instance.getDistances(ville, this.villeActuelle);
			if(distanceMin == -1 || distance < distanceMin) {
				villeChoisie = ville;
				distanceMin = distance;
			}
		}
		
		return villeChoisie;
	}
	
	/**
	 * Lancement de l'algorithme du plus proche voisin
	 * @throws Exception
	 */
	public void avancer() throws Exception {
		if(villesRestantes.size() > 0) {
			int prochaineVille = this.prochaineVille();
			this.villesRestantes.remove(this.villesRestantes.indexOf(prochaineVille));
			this.villesVisitees.add(prochaineVille);
			this.distance += this.m_instance.getDistances(villeActuelle, prochaineVille);
			this.villeActuelle = prochaineVille;
			this.avancer();
		}
		else {
			this.villesVisitees.add(villeDepart);
			
			/*
			for(int i = 0; i < this.villesVisitees.size(); i++) {
				this.m_solution.setCityPosition(this.villesVisitees.get(i), i);
			}*/
		}
	} 

}
