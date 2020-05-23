package coronavirus_project;

import java.util.ArrayList;

public class Chaine {
	private int score_ = 0;
	private ArrayList<int []> personnes_;
	private int index_pays_;
	private int id_personne_originelle_ = -1;
	private int id_min = 0;
	
	public Chaine() {};
	public Chaine(int index_pays, int[] personne) { 
		index_pays_ = index_pays; // Permet de chercher dans les liste du bon pays.
		score_ = 10; // Dans une nouvelle chaine, on est � temps minimal, il lui faut un score de 0
		personnes_ = new ArrayList<int[]>();
		personnes_.add(personne); // on ajoute la personne qui initie la chaine
		id_personne_originelle_ = personne[0];
	}
	
	public void ajouterPersonne(int[] personne) { // ajout d'une personne dans la chaine
		personnes_.add(personne); 
	}
	
	// Si == false, indique que la chaine doit �tre supprim�e.
	public boolean actualiserScore(int temps_actuel) {
		score_ = 0; // recalcul de la chaine, initialisation

        for( int i = id_min; i < personnes_.size(); i++) { // on boucle sur les elements ayant un score sup�rieur � 0 � l'it�ration pr�cedente
            int difference_de_jour = (temps_actuel - personnes_.get(i)[1]) / 86400;
            // calcul de la diff�rence de temps
            if(difference_de_jour < 14) {
                if(difference_de_jour < 7) {
                    score_ += 10;
                } else {
                    score_ += 4;
                }
            }
            else { // on d�place l'id minimal 
                id_min++;
            }
        }
		
		if(score_ == 0) { // apr�s le calcul, si le score est 0, on renvoie false
			return false;
		} else {
			return true;
		}
	}

	public boolean presenceIdPersonneContaminatrice(int id_personne_contaminatrice) {	
		
		// recherche de la personne dans la chaine
		// si la personne recherch�e est contamin�e depuis plus de 14 jours, on renvoie false ( pas lue )
		
		for (int i = id_min; i < personnes_.size(); i++) {
			if(personnes_.get(i)[0] == id_personne_contaminatrice) {
				return true;
			}
		}

		return false;
	}
	
	// Permet avant d'affecter une nouvelle personne � une chaine d'actualiser l'indice minimum si avec le nouveau temps son score passe � 0.
	// N�cessaire pour passer le DATASET_PROFESSEUR.
	public void actualiserPersonnesContaminees(int temps_actuel) {
        for( int i = id_min; i < personnes_.size(); i++) {
            int difference_de_jour = (temps_actuel - personnes_.get(i)[1]) / 86400;

            if(difference_de_jour < 14) {
        		return;
            }
            else {
                id_min++;
            }
        }
        return;
	}
	
	
	public String afficher() {
		String pays = "";
		switch(index_pays_) {
		case 0: pays = "France"; break;
		case 1: pays = "Italy"; break;
		case 2: pays = "Spain"; break;
		}
		
		return pays + ", " + String.valueOf(id_personne_originelle_) + ", " + String.valueOf(score_) + ";";
	}
	
	public int getScore() {return score_;};
	public int getIndexPays() {return index_pays_;};
}
