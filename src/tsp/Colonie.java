package tsp;

import java.util.ArrayList;
import java.util.HashMap;

public class Colonie {
	/* 
	 * Liste des phéromones sur chaque arête, par exemple il y a x quantité de phéromones
	 * sur l'arête entre la ville 1 et 2, soit x = pheromones[1][2]. C'est une matrice
	 * triangulaire car pheromones[1][2] = pheromones[2][1] 
	 */
	
	public Integer[][] pheromones;
	private ArrayList<Fourmi> fourmis;
	private Instance instance;
	
	public Colonie(int nbFourmis, Instance instance) {
		this.instance = instance;
		
		Fourmi fourmi;
		for(int i = 0; i < nbFourmis; i++) {
			fourmi = new Fourmi(this);
			this.fourmis.add(fourmi);
			
			// Initialisation des phéromones
			for(int j = 0; j < nbFourmis; j++) {
				this.pheromones[i][j] = 0;
			}
		}
	}
	
	public Instance getInstance() {
		return this.instance;
	}
	public Integer[][] getPheromones() {
		return this.pheromones;
	}
	public Integer getPheromones(int i, int j) throws Exception {
		if(i < 0 || j < 0 || i >= this.getInstance().getNbCities() || j >= this.getInstance().getNbCities())
			throw new Exception("Vous demandez pheromones[i][j] avec des index qui dépassent les limites");
		
		return (i<j) ? this.getPheromones()[i][j] : this.getPheromones()[j][i];
	}
}
