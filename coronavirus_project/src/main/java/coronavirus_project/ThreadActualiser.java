package coronavirus_project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadActualiser extends Thread {

	private boolean contamination_inter_pays_ = false;
	private LinkedList<Chaine> chaines_ = new LinkedList<Chaine>();
	private ArrayList<Chaine> classement_;
	private ThreadCSVReader thread_lecture_reference_;
	private boolean donnees_en_attente_ = true;
	private int iteration_ = 0;
	// Début : Données récupérées par le Thread d'affichage(2)
	private LinkedList<String> sorties_ = new LinkedList<String>();
	// Fin : Données récupérées par le Thread d'affichage (2)
	
	
	public ThreadActualiser(boolean contamination_inter_pays, ThreadCSVReader thread_lecture_) {
		contamination_inter_pays_ = contamination_inter_pays;
		thread_lecture_reference_ = thread_lecture_;
	}
	
	
    @Override
    public void run() {
    	while(donnees_en_attente_) {
	    	int[] donnees_lecture = thread_lecture_reference_.getDonnees();
	    	// [0] : index_pays_traite_ sinon -1
	    	// [1] : temps_ sinon -1
	    	// [2] : id_personne_[0] sinon -1
	    	// [3] : id_personne_contaminatrice[0] sinon -1
	        if (donnees_lecture[1] != -1) { // On s'assure juste que le temps renvoyé est != -1
	    		
	        		classement_ = new ArrayList<Chaine>(Arrays.asList(new Chaine[] {new Chaine(), new Chaine(), new Chaine()}));
	        		
	        		Iterator<Chaine> iterateur_chaines_ = actionNouveauCas(donnees_lecture[0], donnees_lecture[1], donnees_lecture[2], donnees_lecture[3]);
	
	        		if(iterateur_chaines_ != null) {
	        			actualiserScoresChainesRestantes(donnees_lecture[1], iterateur_chaines_);
	        		}
	        		iteration_++;

					synchronized (this) {
		        		if(classement_.get(1).getScore() == 0) {
		        			sorties_.add(iteration_ + ": " + classement_.get(0).afficher());
		        		} else {
		        			if(classement_.get(2).getScore() == 0) {
		        				sorties_.add(iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher());
		        			} else {
		        				sorties_.add(iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher() + " " + classement_.get(2).afficher());
		        			}
		        		}
		        		//System.out.println("SORTIE : " + sorties_.get(sorties_.size() - 1));
		        		notify();
					}
			} else {
				donnees_en_attente_ = false;
			}
    	}
    }

    
    
	
	private Iterator<Chaine> actionNouveauCas(int index_pays_traite, int temps, int id_personne, int id_personne_contaminatrice) {
		if(id_personne_contaminatrice != -1) {
			Iterator<Chaine> iterateur_chaines = chaines_.iterator();
			
			while(iterateur_chaines.hasNext()) {
				Chaine chaine_i = iterateur_chaines.next();
				chaine_i.actualiserPersonnesContaminees(temps);
				
				if(!contamination_inter_pays_) {
				// Si on ne suppose plus que le virus s'arrête aux frontières des pays, on enlève la première condition.
					if(chaine_i.getIndexPays() == index_pays_traite && chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
						chaine_i.ajouterPersonne(new int[] {id_personne, temps});
						if(!chaine_i.actualiserScore(temps)) {
							iterateur_chaines.remove();
						} else {
							changerClassement(chaine_i, classement_);
						}
						return iterateur_chaines;
					}
				} else {
					if(chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
						chaine_i.ajouterPersonne(new int[] {id_personne, temps});
						if(!chaine_i.actualiserScore(temps)) {
							iterateur_chaines.remove();
						} else {
							changerClassement(chaine_i, classement_);
						}
						return iterateur_chaines;
					}
				}
				
				if(!chaine_i.actualiserScore(temps)) {
					iterateur_chaines.remove();
				} else {
					changerClassement(chaine_i, classement_);
				}
			}
			// L'origine de cette contamination est une chaine de score 0 déjà supprimée, on créé une nouvelle chaine du coup.
			chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps}));
			changerClassement(chaines_.get(chaines_.size() - 1), classement_);
			return null;
		} else {
			chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps}));
			return chaines_.iterator();
		}
	}
	
	
	private void changerClassement(Chaine chaine_i, ArrayList<Chaine> classement) {
		int score = chaine_i.getScore();
		for(int i = 2; i >= 0; i--) {
			if(classement.get(i).getScore() >= score) {
				switch(i) {
				// Pour le case 2 on quitte directement. 
				case 1:{
					classement.set(2, chaine_i); 
				}break;
				
				case 0:{
					classement.set(2, classement.get(1)); 
					classement.set(1, chaine_i); 
				}break;
				}
				break; // ICI ON QUITTE LA BOUCLE : LE CLASSMENT RESTE INCHANGE car i == 2
			} else {
				if(i == 0) {
					classement.set(2, classement.get(1)); 
					classement.set(1, classement.get(0)); 
					classement.set(0, chaine_i);
				}
			}
		}
	}



	private void actualiserScoresChainesRestantes(int temps, Iterator<Chaine> iterateur_chaines){

		while(iterateur_chaines.hasNext()) {

			Chaine chaine_i = iterateur_chaines.next();
			if(!chaine_i.actualiserScore(temps)) {
				iterateur_chaines.remove();

			} else {
				changerClassement(chaine_i, classement_);
			}
		}
	}
	
	
	public synchronized String getSortie() {
		while(sorties_.size() == 0) {
			if(donnees_en_attente_) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return "";
			}
		}
		
		synchronized(sorties_) {
			return sorties_.remove();
		}
	}
    
}
