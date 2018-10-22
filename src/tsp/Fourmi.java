package tsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Juliette, Léo, Anouk
 *
 */
public class Fourmi {
	
	@Override
	public String toString() {
		return "Fourmi [villesVisitees=" + villesVisitees + ", villesRestantes=" + villesRestantes + ", typeFourmi="
				+ typeFourmi + ", villeActuelle=" + villeActuelle + ", etat=" + etat + ", distance=" + distance
				+ ", colonie=" + colonie + "]";
	}

	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int typeFourmi = 1; // 1: fourmi exploratrice 2: fourmi meilleur chemin
	private int villeActuelle;
	private int etat; // 0:au départ 1:aller 2:retour
	private int distance; // somme de toutes les distances parcourues par la fourmi
	
	private int nbTours;
	
	private Colonie colonie;
	
	
	public Fourmi(ArrayList<Integer> villesVisitees, ArrayList<Integer> villesRestantes, int typeFourmi, int villeActuelle, int etat,
			int distance, Colonie colonie) throws Exception {
		this.villesVisitees = villesVisitees;
		this.villesRestantes = villesRestantes;
		this.typeFourmi = typeFourmi;
		this.villeActuelle = villeActuelle;
		this.etat = etat; 
		this.distance = distance; 
		this.colonie = colonie;

		this.nbTours = 0;
		
		this.initialiser();
	}
	
	/**
	 * Description :  Constructeur par chaînage. Créer une fourmi au point de départ
	 * @param colonie
	 * @throws Exception 
	 */
	public Fourmi(Colonie colonie, int typeFourmi) throws Exception {
		this(new ArrayList<Integer>(),new ArrayList<Integer>(),typeFourmi,0,0,0, colonie);
	}
	
	/**
	 * Description :  Constructeur par chaînage. Créer une fourmi au point de départ
	 * @param colonie
	 * @throws Exception 
	 */
	public Fourmi(Colonie colonie) throws Exception {
		this(new ArrayList<Integer>(),new ArrayList<Integer>(),1,0,0,0, colonie);
	}
	
	/**
	 * Description : villes atteignables depuis la position actuelle de la fourmi. Critère arbitraire
	 * @param rayonRecherche (-1 pour tout accepter)
	 * @return liste des numéros des villes atteignables
	 */
	public ArrayList<Integer> prochainesVillesPossibles(int rayonRecherche) {
		int nbVillesMinimum = 5;
		
		long[][] distances = this.colonie.getInstance().getDistances();
		ArrayList<Integer> prochainesVillesPossibles = new ArrayList<Integer>();
		for(int ville : this.villesRestantes) {
			if(rayonRecherche == -1 || distances[villeActuelle][ville]<=rayonRecherche) {
				prochainesVillesPossibles.add(ville);
			}
		}
		return prochainesVillesPossibles;
	}
	public ArrayList<Integer> prochainesVillesPossibles() {
		return prochainesVillesPossibles(-1);
	}
	

/**
 * Description : On associe chaque ville à sa probabilité d'être choisi à la prochaine étape
 * @return Hashmap avec en clé le numéro de la ville et en valeur la probabilité. 
 * @throws Exception
 */
	public HashMap<Integer,Double> probabilitesVillesPossibles(ArrayList<Integer> prochainesVillesPossibles) throws Exception {
		double alpha = 1.0;
		double beta = 3.0;
		
		HashMap<Integer,Double> probabilites = new HashMap();
		double probabilite;		 
		
		/*
		 * On calcule pour chaque ville la probabilité de la choisir comme prochaine
		 * destination
		 */
		double sommeProbabilites = 0.0;
		for(int ville : prochainesVillesPossibles) {
			probabilite = Math.pow(1.0/((double) this.colonie.getInstance().getDistances(this.villeActuelle, ville)), alpha)*Math.pow((double)this.colonie.getPheromones(this.villeActuelle, ville), beta);
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
	
/**
 * Description : Utilisation des probabilités calculées pour choisir la prochaine destination
 * @return le numéro de la prochaine ville à laquelle la fourmi va se rendre.
 * @throws Exception
 */
	
	// Non optimal et bug pour ajuster
	public int NextStep() throws Exception {
		int i=0;
		int villeSuivante=0;
		ArrayList<Integer> prochainesVillesPossibles = prochainesVillesPossibles();
		
		if(prochainesVillesPossibles.size() >= 1) {
			if(this.typeFourmi == 2) {
				int maxPheromone = 0;
				int pheromone;
				for(int ville : prochainesVillesPossibles) {
					pheromone = this.colonie.getPheromones(villeActuelle, ville);
					if(pheromone >= maxPheromone) {
						maxPheromone = pheromone;
						villeSuivante = ville;
					}
				}
			}
			else if(this.typeFourmi == 1) {
				HashMap<Integer,Double> proba = this.probabilitesVillesPossibles(prochainesVillesPossibles); 
				
				while (i==0) {
					//System.err.println(prochainesVillesPossibles);
					//System.err.println(prochainesVillesPossibles.size());
					//System.err.println(proba);
					if(prochainesVillesPossibles.size() == 1 || Math.random()<= proba.get(prochainesVillesPossibles.get(i))) {
						villeSuivante=prochainesVillesPossibles.get(i);
						i=1;
						// i=1 donc on arrête la boucle
					}
					else {
						prochainesVillesPossibles.remove(0);
						for (int villesuiv: prochainesVillesPossibles) {
							proba.put(villesuiv, proba.get(villesuiv)/(1.0-proba.get(prochainesVillesPossibles.get(i))));
						}
					}
				}
			}
		}
		else {
			// On peut être dans un cul de sac à cause du critère de distance de prochainesVillesPossibles()
			villeSuivante = -1;
		}
		
		//System.err.println();
		return villeSuivante;
	}
	
	/**
	 * Description : dépose des phéromones sur le chemin retour.
	 * @throws Exception
	 */
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
				this.colonie.incPheromones(villeA, villeB, 1);
			}
		}
	}

	/**
	 * @return true si la fourmi est arrivée à destination, false sinon
	 * @throws Exception
	 */
	public boolean arriveeADestination() throws Exception {
		if(this.villesRestantes.size()==0) {
			// On ferme la boucle du trajet
			this.avancer(0);
			
			this.etat=2;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Initialise une fourmi (ou la réinitialise)
	 * @throws Exception 
	 */
	public void initialiser() throws Exception {		
		this.villeActuelle = 0;
		this.etat = 0;
		this.distance = 0;
		this.villesVisitees = new ArrayList<Integer>();
		this.villesRestantes = new ArrayList<Integer>();
		
		this.villesVisitees.add(0);
		for(int i=1;i<this.colonie.getInstance().getNbCities();i++) {
			this.villesRestantes.add(i);
		}
	
		this.nbTours++;
	} 
	
	public void mettreAJourMeilleurChemin() throws Exception {
		if(this.villesRestantes.size()==0)
			this.colonie.setMeilleurChemin(this.villesVisitees, this.distance);
	}
	
	/**
	 * Description : défini comment la fourmi doit se déplacer dans le graphe.
	 * @throws Exception
	 */
	public void parcourir() throws Exception {
		if(this.typeFourmi == 2 || (this.typeFourmi == 1 && !this.colonie.doitOnArreterLAlgorithme())) {
			if(this.arriveeADestination()) {			
				if(this.typeFourmi == 1)
					this.deposerPheromones();
				if(this.typeFourmi == 2) 
					this.mettreAJourMeilleurChemin();
			}
			else { 
				int villeSuivante=NextStep();
				// Si la fourmi est bloquée dans un cul de sac
				if(villeSuivante == -1)
					this.initialiser();
				else
					this.avancer(villeSuivante);
				this.parcourir();
			} 
		}
	}

	/**
	 * Description : avancer de la ville actuelle à la ville suivante
	 * @param villeSuivante
	 * @throws Exception
	 */
	public void avancer(int villeSuivante) throws Exception {		
		distance+=this.colonie.getInstance().getDistances(villeActuelle, villeSuivante);
		villesVisitees.add(villeSuivante);
		villeActuelle=villeSuivante;
		if(villesRestantes.size() > 0)
			villesRestantes.remove(villesRestantes.indexOf(villeActuelle));
		
	}
} 

