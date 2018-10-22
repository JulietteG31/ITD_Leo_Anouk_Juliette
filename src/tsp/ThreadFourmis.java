package tsp;

public class ThreadFourmis extends Thread {

	private Colonie colonie;
	private int departFourmis;
	private int nbFourmis;
	
	ThreadFourmis(Colonie colonie, int departFourmis) {
		this.colonie = colonie;
		this.departFourmis = departFourmis;
		this.nbFourmis = this.colonie.fourmis.size();
	}
	
    public void run(){
    	for(int j = departFourmis; j < (this.nbFourmis/4+departFourmis); j++) {
			if(this.colonie.doitOnArreterLAlgorithme())
				break;
			
			try {
				this.colonie.fourmis.get(j).parcourir();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
  }