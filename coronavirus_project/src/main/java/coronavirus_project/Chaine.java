package coronavirus_project;

import java.util.ArrayList;
import java.util.Iterator;

public class Chaine {
	private int score_ = 0;
	private ArrayList<int []> personnes_;
	private int index_pays_;
	private int id_personne_originelle_ = -1;
	
	public Chaine() {};
	public Chaine(int index_pays, int[] personne) {
		index_pays_ = index_pays;
		score_ = 10;
		personnes_ = new ArrayList<int[]>();
		personnes_.add(personne);
		id_personne_originelle_ = personne[0];
	}
	
	public void ajouterPersonne(int[] personne) {
		personnes_.add(personne);
	}
	
	// Si == false, indique que la chaine doit être supprimée.
	// TODO : OPTIMISATION LILIAN A FAIRE, indiquer dans un champs de la classe l'indice à partir duquel > 14 jours pour éviter le traitement suplémentaire.
	public boolean actualiserScore(int temps_actuel) {
		score_ = 0;
		
		Iterator<int []> iterateur = personnes_.iterator();
		while(iterateur.hasNext()) {
			int[] personne = iterateur.next();
			
			int difference_de_jour = (temps_actuel - personne[1]) / 86400;

			if(difference_de_jour < 14) {
				if(difference_de_jour < 7) {
					score_ += 10;
				} else {
					score_ += 4;
				}
			}
		}
		
		if(score_ == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean presenceIdPersonneContaminatrice(int id_personne_contaminatrice) {
		Iterator<int []> iterateur = personnes_.iterator();
		while(iterateur.hasNext()) {
			int[] personne = iterateur.next();
			if(personne[0] == id_personne_contaminatrice) {
				return true;
			}
		}
		return false;
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
