package tsp;
import java.util.ArrayList;

public class PlusProcheVoisin {

	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int villeActuelle;
	private int distance;
	private Instance instance;
	public Solution solution;
	
	
	
	public PlusProcheVoisin(ArrayList<Integer> villesVisitees, ArrayList<Integer> villesRestantes, int villeActuelle, int distance, Instance instance, Solution solution) {
		this.villesVisitees = villesVisitees;
		this.villesRestantes = villesRestantes;
		this.villeActuelle = villeActuelle;
		this.distance = distance;
		this.instance = instance;
		this.solution = solution;
	}
	
	/**
	 * Description: on initialise avec seule la premiere ville (villeActuelle) dans la liste des villes visitees villesVisitees, toutes les autre villes dans villesRestantes et la villeActuelle a la ville 0
	 * On lance avancer pour enclancher le deplacement de ville en ville
	 * @param instance
	 * @param solution
	 * @throws Exception
	 */
	public PlusProcheVoisin(Instance instance, Solution solution) throws Exception{
		this(new ArrayList<Integer>(0),new ArrayList<Integer>(),0,0,instance,solution);
		
		for(int ville = 0; ville < this.instance.getNbCities(); ville++) {
			this.villesRestantes.add(ville);
		}
		
		avancer();
	}
	
	/*
	 * Prend une ville au hasard + lui trouver le plus proche voisin + avancer
	 * Si il n'y a plus de villes non visitees --> retour a la ville 0
	 */
	
	/**
	 * 
	 * @param villeActuelle
	 * @return la ville non visitee la plus proche de notre ville actuelle villeActuelle
	 * @throws Exception
	 */

	public int PlusProche(int villeActuelle) throws Exception {
		int VilleSuiv=0;
		if(villesRestantes.size() > 0) {
			long d=this.instance.getDistances(villeActuelle, villesRestantes.get(0));
			VilleSuiv=villesRestantes.get(0);
			for (int ville : villesRestantes) {
				if (this.instance.getDistances(villeActuelle, ville)<=d){
						d=this.instance.getDistances(villeActuelle, ville);
						VilleSuiv=ville;
				}
			}
		}
		return VilleSuiv;
	}
	
	/*
	 * si il reste des villesRestantes on fait plusproche et on y va, si villesRestantes est vide on retourne a la premiere ville 
	 */
	
	/**
	 * Description: si il reste des villes non visitees dans villesRestantes, on va a la ville la plus proche, si villesRestantes est vide on retourne a la premiere ville (ville 0) 
	 * @throws Exception
	 */
	
	public void avancer() throws Exception {		
		int PlusProche=PlusProche(villeActuelle);
		if (villesRestantes.size()>0) {
			distance+=this.instance.getDistances(villeActuelle, PlusProche);
			villesVisitees.add(PlusProche);
			villeActuelle=PlusProche;
			villesRestantes.remove(villesRestantes.indexOf(PlusProche));
			avancer();
			
		}else {
			distance+=this.instance.getDistances(villeActuelle, 0);
			for(int i=0; i<villesVisitees.size();i++) {
				solution.setCityPosition(villesVisitees.get(i), i);
			}
		}
	}
	
}