package tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Juliette, Léo, Anouk
 *
 */
public class Fourmi {
	
	/**
	 * Methode utilisee pour des tests
	 */
	@Override
	public String toString() {
		return "Fourmi [villesVisitees=" + villesVisitees + ", villesRestantes=" + villesRestantes + ", typeFourmi="
				+ typeFourmi + ", villeActuelle=" + villeActuelle + ", etat=" + etat + ", distance=" + distance
				+ ", colonie=" + colonie + "]";
	}
	
	// -----------------------------
	// ----- ATTRIBUTS -------------
	// -----------------------------

	private ArrayList<Integer> villesVisitees;
	private ArrayList<Integer> villesRestantes;
	private int typeFourmi = 1; // 1: fourmi exploratrice 2: fourmi meilleur chemin
	private int villeActuelle;
	private int etat; // 0:au départ 1:aller 2:retour
	private int distance; // somme de toutes les distances parcourues par la fourmi
	private Colonie colonie;
	private int villeDepart;
	
	private ArrayList<double[]> arcsAjoutes;
	
	// -----------------------------
	// ----- CONSTRUCTOR -----------
	// -----------------------------

	/**
	 * Constructeur
	 * @param colonie
	 * @param typeFourmi
	 * @param villeDepart
	 * @param commencer
	 * @throws Exception
	 */
	
	public Fourmi(Colonie colonie, int typeFourmi, int villeDepart, boolean commencer) throws Exception {
		this.colonie=colonie;
		this.typeFourmi=typeFourmi;
		
		initialiser(villeDepart);
		
		if(commencer && !this.colonie.doitOnArreterLAlgorithme())
			this.parcourir();
	}
	public Fourmi(Colonie colonie, int typeFourmi, int villeDepart) throws Exception {
		this(colonie, typeFourmi, villeDepart, false);
	}
	
	public void initialiser(int villeDepart) throws Exception {
		//this.colonie.nbFourmisPassees++;
		
		this.villeDepart = villeDepart;
		this.etat = 0;
		this.distance = 0;
		this.villesVisitees = new ArrayList<Integer>();
		this.villesRestantes = new ArrayList<Integer>();
		this.villeActuelle = villeDepart;
		this.arcsAjoutes = new ArrayList<double[]>();
		
		this.villesVisitees.add(villeDepart);
		for(int i=1;i<this.colonie.getInstance().getNbCities();i++) {
			this.villesRestantes.add(i);
		}
	}
	
	public void reinitialiser(int villeDepart) throws Exception {
		Fourmi nextStep = new Fourmi(this.colonie, this.typeFourmi, villeDepart, true);
	}
	
	/**
	 * Description :  Constructeur par chaînage. Créer une fourmi au point de départ
	 * @param colonie
	 * @throws Exception 
	 */
	public Fourmi(Colonie colonie) throws Exception {
		this(colonie, 1, 0);
	}
	
	// -----------------------------
	// ----- METHODS ---------------
	// -----------------------------

	
	/**
	 * Description : villes atteignables depuis la position actuelle de la fourmi. 
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
	 * Description : On associe chaque ville à sa probabilité d'être choisie à la prochaine étape
	 * @return Hashmap avec en clé le numéro de la ville et en valeur la probabilité. 
	 * @throws Exception
	 */
	public HashMap<Integer,Double> probabilitesVillesPossibles(ArrayList<Integer> prochainesVillesPossibles) throws Exception {
		double alpha = 1;
		double beta = 5;
		
		HashMap<Integer,Double> probabilites = new HashMap();
		double probabilite;		
		
		HashMap<Integer,ArrayList<Double>> probabilitesAffichage = new HashMap();
		// Pour débuguer
		ArrayList<Double> probabilitesAffichageList;
		
		/*
		 * On calcule pour chaque ville la probabilité qu'elle soit choisie comme prochaine destination
		 */
		double sommeProbabilites = 0.0;
		double distance;
		double pheromone;
		for(int ville : prochainesVillesPossibles) {
			distance = this.colonie.getInstance().getDistances(this.villeActuelle, ville);
			pheromone = this.colonie.getPheromones(this.villeActuelle, ville);
			probabilite = Math.pow(1.0/(distance), alpha)*Math.pow(pheromone, beta);
			sommeProbabilites += probabilite;
			probabilites.put(ville, probabilite);
			
			probabilitesAffichageList = new ArrayList<Double>();
			probabilitesAffichageList.add(((double) this.colonie.getInstance().getDistances(this.villeActuelle, ville)));
			probabilitesAffichageList.add((double)this.colonie.getPheromones(this.villeActuelle, ville));
			probabilitesAffichageList.add(probabilite);
			probabilitesAffichage.put(ville, probabilitesAffichageList);
			
			// on met à jour la moyenne des distances
			this.colonie.addDistanceMoyenne((int) distance);
		}
		/* 
		 * On ajuste les probabilités pour que la somme de toutes vaille 1
		 */
		for(int ville : prochainesVillesPossibles) {
			probabilites.put(ville, probabilites.get(ville)/sommeProbabilites);
			
			probabilitesAffichageList = probabilitesAffichage.get(ville);
			probabilite = probabilitesAffichageList.get(2);
			probabilite = probabilite/sommeProbabilites;
			probabilitesAffichageList.set(2, probabilite);
			probabilitesAffichage.put(ville, probabilitesAffichageList);
		}
		
		//System.err.println(probabilitesAffichage);
		
		return probabilites;
	}
	
	/**
	 * Description : Utilisation des probabilités calculées pour choisir la prochaine destination
	 * @return le numéro de la prochaine ville à laquelle la fourmi va se rendre.
	 * @throws Exception
	 */
	
	public int NextStep() throws Exception {
		int villeSuivante=0;
		int nbProchainesVillesPossibles = 0;
		int critereDistance = this.colonie.getDistanceMoyenne()/4;
		//critereDistance = -1;
		
		int nbCritere = 1;
		ArrayList<Integer> prochainesVillesPossibles;
		do {
			prochainesVillesPossibles = prochainesVillesPossibles(nbCritere*critereDistance);
			nbProchainesVillesPossibles = prochainesVillesPossibles.size();
			nbCritere++;
		} while(nbProchainesVillesPossibles < Math.ceil((double) 0.01*this.colonie.getInstance().getNbCities()) && nbProchainesVillesPossibles < this.villesRestantes.size());
		
		if(nbProchainesVillesPossibles >= 1) {
			if(this.typeFourmi == 2) {
				double maxPheromone = 0;
				double pheromone;
				for(int ville : prochainesVillesPossibles) {
					pheromone = this.colonie.getPheromones(villeActuelle, ville);
					if(pheromone >= maxPheromone) {
						maxPheromone = pheromone;
						villeSuivante = ville;
					}
				}
			}
			else if(this.typeFourmi == 1) {
				int i=0;
				//int villeSupprimee;
				HashMap<Integer,Double> proba = this.probabilitesVillesPossibles(prochainesVillesPossibles); 
				HashMap<Integer,Double> proba2 = proba;
				double random = Math.random();
				double probaTest = 0.0;
				
				while (i==0) {
					//System.err.println(prochainesVillesPossibles);
					//System.err.println(prochainesVillesPossibles.size());
					//System.err.println(proba)
					probaTest += proba.get(prochainesVillesPossibles.get(0));
					if(prochainesVillesPossibles.size() == 1 || random <= probaTest) {
						villeSuivante=prochainesVillesPossibles.get(0);
						/*if(this.colonie.getInstance().getDistances(villeActuelle, villeSuivante) > 3000) {
							System.err.println(this.colonie.getInstance().getDistances(villeActuelle, villeSuivante));
							System.err.println(prochainesVillesPossibles);
							System.err.println(proba2);
						}*/
						i=1;
						// i=1 donc on arrête la boucle
					}
					else {
						//villeSupprimee = prochainesVillesPossibles.get(0);
						prochainesVillesPossibles.remove(0);
						/*for (int villesuiv: prochainesVillesPossibles) {
							proba.put(villesuiv, proba.get(villesuiv)*(1.0-proba.get(villeSupprimee)));
						}*/
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
				//this.colonie.evapPheromones(villeA, villeB, 0.001);
				this.colonie.incPheromones(villeA, villeB, (double)this.colonie.getDistanceMoyenne()/this.distance);
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
			this.avancer(this.villeDepart);
			
			this.etat=2;
			return true;
		}
		else {
			return false;
		}
	}
	
	public void mettreAJourMeilleurChemin() throws Exception {
		if(this.villesRestantes.size()==0) {
			this.colonie.setMeilleurChemin(this.villesVisitees, this.distance);
		}
	}
	
	/**
	 * Description : défini comment la fourmi doit se déplacer dans le graphe.
	 * @throws Exception
	 */
	public void parcourir() throws Exception {
		if(this.typeFourmi == 2 || (this.typeFourmi == 1 && !this.colonie.doitOnArreterLAlgorithme())) {
			if(this.arriveeADestination()) {			
				if(this.typeFourmi == 1) {
					this.deposerPheromones();
					//this.reinitialiser((int)Math.random()*this.colonie.getInstance().getNbCities());
				}
				if(this.typeFourmi == 2) 
					this.mettreAJourMeilleurChemin();
			}
			else { 
				int villeSuivante=NextStep();
				// Si la fourmi est bloquée dans un cul de sac
				if(villeSuivante == -1)
					this.reinitialiser((int)Math.random()*this.colonie.getInstance().getNbCities());
				else {	
					this.avancer(villeSuivante);
					this.parcourir();
				}
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
		if(this.typeFourmi == 1) 
			villesVisitees.add(villeSuivante);
		else
			villesVisitees.add(villeSuivante);
		
		
		/**
		 * Tentative détection des croisements 
		 */
		
		/*if(this.typeFourmi == 2) {
			double[] pointA = {this.colonie.getInstance().getX(villeActuelle), this.colonie.getInstance().getY(villeActuelle),0, villeActuelle};
			double[] pointB = {this.colonie.getInstance().getX(villeSuivante), this.colonie.getInstance().getY(villeSuivante),0, villeSuivante};
			
			boolean pointAAuBonEndroit = false, pointBAuBonEndroit = false;
			int i = 0;
			int indexA = 0, indexB = 0;
			if(pointB[0] <= pointA[0]) {
				double[] point = pointA;
				pointA = pointB;
				pointB = point;
			}
			while(!pointAAuBonEndroit && i < this.arcsAjoutes.size()) {
				if(pointA[0] < this.arcsAjoutes.get(i)[0]) {
					this.arcsAjoutes.add(i, pointA);
					pointAAuBonEndroit = true;
					indexA = i;
				}
				i++;
			}
			if(!pointAAuBonEndroit && i == this.arcsAjoutes.size()) {
				this.arcsAjoutes.add(pointA);
				indexA = i;
			}
			
			while(!pointBAuBonEndroit && i < this.arcsAjoutes.size()) {
				if(pointB[0] < this.arcsAjoutes.get(i)[0]) {
					this.arcsAjoutes.add(i, pointB);
					pointBAuBonEndroit = true;
					indexB = i;
				}
				i++;
			}
			if(!pointBAuBonEndroit && i == this.arcsAjoutes.size()) {
				this.arcsAjoutes.add(pointB);
				indexB = i;
			}
			
			pointA[2] = indexB;
			pointB[2] = indexA;
			this.arcsAjoutes.set(indexA, pointA);
			this.arcsAjoutes.set(indexB, pointB);
			
			int ecartIndexA = 1, ecartIndexB = 1;
			boolean croisementTrouve = false;
			double[] arcAGaucheDeA;
			double[] arcADroiteDeB = null;
			double[] pointArcAGaucheDeA = null;
			double[] pointArcADroiteDeB;
			int villeC = 0, villeD = 0;
			while(!croisementTrouve && (indexA-ecartIndexA >= 0 || indexB+ecartIndexB < this.arcsAjoutes.size())) {
				if(indexA-ecartIndexA >= 0) {
					arcAGaucheDeA = this.arcsAjoutes.get(indexA-ecartIndexA);
					if(arcAGaucheDeA[2] > indexA) {
						pointArcAGaucheDeA = this.arcsAjoutes.get((int)arcAGaucheDeA[2]);
						if( Math.sqrt(Math.pow(pointArcAGaucheDeA[0]-arcAGaucheDeA[0], 2)+Math.pow(pointArcAGaucheDeA[1]-arcAGaucheDeA[1], 2))+Math.sqrt(Math.pow(pointA[0]-pointB[0], 2)+Math.pow(pointA[1]-pointB[1], 2)) == Math.sqrt(Math.pow(pointA[0]-pointArcAGaucheDeA[0], 2)+Math.pow(pointA[1]-pointArcAGaucheDeA[1], 2))+Math.sqrt(Math.pow(pointArcAGaucheDeA[0]-pointB[0], 2)+Math.pow(pointArcAGaucheDeA[1]-pointB[1], 2))+Math.sqrt(Math.pow(arcAGaucheDeA[0]-pointB[0], 2)+Math.pow(arcAGaucheDeA[1]-pointB[1], 2))+Math.sqrt(Math.pow(arcAGaucheDeA[0]-pointA[0], 2)+Math.pow(arcAGaucheDeA[1]-pointA[1], 2)) ) {
							croisementTrouve = true;
							villeC = (int) arcAGaucheDeA[3];
							villeD = (int) pointArcAGaucheDeA[3];
						}
					}
					ecartIndexA++;
				}
				
				if(!croisementTrouve && indexB+ecartIndexB < this.arcsAjoutes.size()) {
					arcADroiteDeB = this.arcsAjoutes.get(indexB+ecartIndexB);
					if(arcADroiteDeB[2] < indexB) {
						pointArcADroiteDeB = this.arcsAjoutes.get((int)arcADroiteDeB[2]);
						if( Math.sqrt(Math.pow(pointArcADroiteDeB[0]-arcADroiteDeB[0], 2)+Math.pow(pointArcADroiteDeB[1]-arcADroiteDeB[1], 2))+Math.sqrt(Math.pow(pointA[0]-pointB[0], 2)+Math.pow(pointA[1]-pointB[1], 2)) == Math.sqrt(Math.pow(pointA[0]-pointArcADroiteDeB[0], 2)+Math.pow(pointA[1]-pointArcADroiteDeB[1], 2))+Math.sqrt(Math.pow(pointArcADroiteDeB[0]-pointB[0], 2)+Math.pow(pointArcADroiteDeB[1]-pointB[1], 2))+Math.sqrt(Math.pow(arcADroiteDeB[0]-pointB[0], 2)+Math.pow(arcADroiteDeB[1]-pointB[1], 2))+Math.sqrt(Math.pow(arcADroiteDeB[0]-pointA[0], 2)+Math.pow(arcADroiteDeB[1]-pointA[1], 2)) ) {
							croisementTrouve = true;
							villeC = (int) arcADroiteDeB[3];
							villeD = (int) pointArcADroiteDeB[3];
						}
					}
					ecartIndexB++;
				}
			}
			
			if(croisementTrouve) {
				int indexCDansCheminActuel = this.villesVisitees.indexOf(villeC);
				int indexDDansCheminActuel = this.villesVisitees.indexOf(villeD);
				int index = 0;
				if(indexDDansCheminActuel > indexCDansCheminActuel) {
					index = indexCDansCheminActuel;
					indexCDansCheminActuel = indexDDansCheminActuel;
					indexDDansCheminActuel = index;
				}
				
				if(indexCDansCheminActuel != -1 && indexDDansCheminActuel != -1 && indexCDansCheminActuel-indexDDansCheminActuel > 1) {
					ArrayList<Integer> cheminAInverserEntreCetD = new ArrayList<Integer>(this.villesVisitees.subList(indexCDansCheminActuel+1, indexDDansCheminActuel));
					Collections.reverse(cheminAInverserEntreCetD);
					System.err.println("inversion");
					this.villesVisitees.add(indexCDansCheminActuel+1, villeActuelle);
					for(i = 0; i < cheminAInverserEntreCetD.size(); i++) {
						this.villesVisitees.set(indexCDansCheminActuel+i+2, cheminAInverserEntreCetD.get(i));
					}
					this.villesVisitees.add(villeSuivante);
				}
			}
			else
				this.villesVisitees.add(villeSuivante);
		}*/
			
		villeActuelle=villeSuivante;
		if(villesRestantes.size() > 0)
			villesRestantes.remove(villesRestantes.indexOf(villeActuelle));
		
	}
} 

