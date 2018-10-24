package tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Colonie {
	/* 
	 * Liste des phéromones sur chaque arête, par exemple il y a x quantité de phéromones
	 * sur l'arête entre la ville 1 et 2, soit x = pheromones[1][2]. C'est une matrice
	 * triangulaire car pheromones[1][2] = pheromones[2][1] 
	 */
	
	@Override
	public String toString() {
		return "Colonie [pheromones=" + Arrays.toString(pheromones) + ", fourmis=" + fourmis + ", meilleurChemin=" + meilleurChemin + ", meilleureDistance=" + meilleureDistance + "]";
	}

	public int[][] pheromones;
	public ArrayList<Fourmi> fourmis;
	private ArrayList<Integer> meilleurChemin;
	private int meilleureDistance = 0;
	
	private long timeLimit; 
	private Instance instance;
	private Solution solution;
	private long startTime;
	
	public int nbFourmis = 0;
	public int nbFourmisPassees = 0;
	
	private int[] moyenneDistancesInstance;
	
	/**
	 * Créer une colonie de nbFourmis Fourmi Fourmis et créer la matrice triangulaire supérieure des phéromones 
	 * @param nbFourmis
	 * @param instance
	 * @throws Exception 
	 */
	public Colonie(int nbFourmis, Instance instance, Solution solution, long timeLimit) throws Exception {
		this.instance = instance;
		this.solution = solution;
		this.timeLimit = timeLimit;
		this.nbFourmis = nbFourmis;

		int nbVilles = instance.getNbCities();
		
		// Initialisation des fourmis
		Fourmi fourmi;
		this.fourmis = new ArrayList<Fourmi>();
		
		for(int i = 0; i < nbFourmis; i++) {
			fourmi = new Fourmi(this,1);
			this.fourmis.add(fourmi);
		} 
		
		this.pheromones = new int[instance.getNbCities()][];
		for(int i = 0; i < instance.getNbCities(); i++) {
			this.pheromones[i] = new int[i+1];
			// Initialisation des phéromones
			for(int j = 0; j < i+1; j++) {
				this.pheromones[i][j] = 1;
			}
		}
		
		// Initialisation distances moyennes
		moyenneDistancesInstance = new int[2];
		int randomIndex;
		for(int i = 0; i < Math.ceil((double) 0.05*nbVilles); i++) {
			randomIndex = (int) Math.random()*(nbVilles-2);
			moyenneDistancesInstance[0] += this.instance.getDistances(randomIndex, randomIndex+1);
			moyenneDistancesInstance[1]++;
		}
		moyenneDistancesInstance[0] /= moyenneDistancesInstance[1];
		
		System.err.println("Moyenne distances = "+moyenneDistancesInstance[0]);
		
		this.commencer();
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	public Solution getSolution() {
		return this.solution;
	}
	public int getDistanceMoyenne() {
		return this.moyenneDistancesInstance[0];
	}
	public int[][] getPheromones() {
		return this.pheromones;
	}
	public int getPheromones(int i, int j) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");

		return (i>j) ? this.getPheromones()[i][j] : this.getPheromones()[j][i];
	}
	public void setPheromones(int i, int j, int value) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");
		
		if(i>j)
			this.pheromones[i][j] = value;
		else
			this.pheromones[j][i] = value;
	}
	
	/**
	 * Incrémenter les phéromones du chemin entre la ville i et la ville j d'une valeur inc
	 * @param i
	 * @param j
	 * @param inc
	 * @throws Exception
	 */
	public void incPheromones(int i, int j, int inc) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");
		
		this.setPheromones(i, j, this.getPheromones(i, j) + inc); 
	}
	
	public void setMeilleurChemin(ArrayList<Integer> chemin, int distance) throws Exception {
		if(chemin.get(0) != 0 || chemin.get(chemin.size()-1) != 0)
			throw new Exception("Le format du chemin n'est pas bon");
		
		if(distance < this.meilleureDistance || this.meilleureDistance == 0) {
			this.meilleurChemin = chemin;
			this.meilleureDistance = distance;
			
			for(int i = 0; i < chemin.size(); i++) {
				this.solution.setCityPosition(chemin.get(i), i);
			}
		}
	}
	
	public void commencer() throws Exception {
		this.startTime = System.currentTimeMillis();
		
		ThreadFourmis thread;
		
		int depart = 0;
			
		for(int i = 1; i <= 4; i++) {
			thread = new ThreadFourmis(this, depart);
			thread.start();
			
			depart += nbFourmis/4;
		}
		
		while(!this.doitOnArreterLAlgorithme());
		
		Fourmi fourmiSolution = new Fourmi(this, 2);
		fourmiSolution.parcourir();
		System.err.println("Nombre de fourmis passées = "+this.nbFourmisPassees+"/"+this.nbFourmis);
		System.err.println("Durée totale programme (ms) = "+this.getDureeMs());
		System.err.println("Solution faisable = " + this.getSolution().isFeasible());
	}
	
	public float getDureeMs() {
		return (System.currentTimeMillis() - this.startTime);
	}
	public long getDuree() {
		return (System.currentTimeMillis() - this.startTime)/1000;
	}
	public boolean doitOnArreterLAlgorithme() {
		return this.nbFourmis == this.nbFourmisPassees || this.getDuree() > this.timeLimit-1;
	}
	
}
