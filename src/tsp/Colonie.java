package tsp;

import java.util.ArrayList;
import java.util.HashMap;

public class Colonie {
	/* 
	 * Liste des phéromones sur chaque arête, par exemple il y a x quantité de phéromones
	 * sur l'arête entre la ville 1 et 2, soit x = pheromones[1][2]. C'est une matrice
	 * triangulaire car pheromones[1][2] = pheromones[2][1] 
	 */
	
	public int[][] pheromones;
	private ArrayList<Fourmi> fourmis;
	private Instance instance;
	private ArrayList<Integer> meilleurChemin;
	private int meilleureDistance = 0;
	
	/**
	 * Créer une colonie de nbFourmis Fourmi Fourmis et créer la matrice triangulaire supérieure des phéromones 
	 * @param nbFourmis
	 * @param instance
	 */
	public Colonie(int nbFourmis, Instance instance) {
		this.instance = instance;
		 
		Fourmi fourmi;
		int nbFourmisMeilleurChemin = (int) (Math.ceil(0.01*((double) nbFourmis)));
		System.out.println(nbFourmisMeilleurChemin);
		for(int i = 0; i < nbFourmis+nbFourmisMeilleurChemin; i++) {
			fourmi = new Fourmi(this, (i < nbFourmis) ? 1 : 2);
			this.fourmis.add(fourmi);
		} 
		
		for(int i = 0; i < instance.getNbCities(); i++) {
			this.pheromones[i] = new int[instance.getNbCities()-i];
			// Initialisation des phéromones
			for(int j = i; j < instance.getNbCities(); j++) {
				this.pheromones[i][j] = 0;
			}
		}
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	public int[][] getPheromones() {
		return this.pheromones;
	}
	public int getPheromones(int i, int j) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");
		
		return (i<j) ? this.getPheromones()[i][j] : this.getPheromones()[j][i];
	}
	public void setPheromones(int i, int j, int value) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");
		
		if(i<j)
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
		
		if(distance < this.meilleureDistance) {
			this.meilleurChemin = chemin;
			this.meilleureDistance = distance;
		}
	}
	
}
