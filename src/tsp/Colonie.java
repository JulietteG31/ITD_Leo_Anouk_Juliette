package tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	private ArrayList<Fourmi> fourmis;
	private ArrayList<Integer> meilleurChemin;
	private int meilleureDistance = 0;
	
	private long timeLimit; 
	private Instance instance;
	private Solution solution;
	private long startTime;
	
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

		int nbVilles = instance.getNbCities();
		
		Fourmi fourmi;
		int nbFourmisMeilleurChemin = (int) (Math.ceil(0.01*((double) nbFourmis)));
		//nbFourmisMeilleurChemin = 0;
		this.fourmis = new ArrayList<Fourmi>();
		
		for(int i = 0; i < nbFourmis+nbFourmisMeilleurChemin; i++) {
			fourmi = new Fourmi(this, (i < nbFourmis) ? 1 : 2);
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
		
		this.commencer();
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	public Solution getSolution() {
		return this.solution;
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
		
		for(Fourmi fourmi : this.fourmis) {
			fourmi.parcourir();
		}
	}
	
	public float getDureeMs() {
		return (System.currentTimeMillis() - this.startTime);
	}
	public long getDuree() {
		return (System.currentTimeMillis() - this.startTime)/1000;
	}
	public boolean doitOnArreterLAlgorithme() {
		return this.getDuree() > this.timeLimit;
	}
	
}
