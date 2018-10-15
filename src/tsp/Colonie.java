package tsp;

import java.util.ArrayList;
import java.util.HashMap;

public class Colonie {
	/* 
	 * Liste des phéromones sur chaque arête, par exemple il y a x quantité de phéromones
	 * sur l'arête entre la ville 1 et 2, soit x = pheromones[1][2]. C'est une matrice
	 * triangulaire car pheromones[1][2] = pheromones[2][1] 
	 */
	
	Integer[][] pheromones;
	ArrayList<Fourmi> fourmis;
	Instance instance;
	
	public Colonie(int nbFourmis, Instance instance) {
		this.instance = instance;
		
		Fourmi fourmi;
		for(int i = 0; i < nbFourmis; i++) {
			fourmi = new Fourmi(this);
			this.fourmis.add(fourmi);
		}
	}
}
