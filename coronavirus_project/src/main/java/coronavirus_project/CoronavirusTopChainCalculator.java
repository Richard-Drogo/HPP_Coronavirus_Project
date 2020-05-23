package coronavirus_project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class CoronavirusTopChainCalculator {
	
	// ATTRIBUTS ---------------------------------------------------------------------------
	private String chemin_fichier_avancement_ = null;
	private int[] ligne_actuelle_pays = new int[] {0, 0, 0}; // France, Italy, Spain
	private int temps_ = 0; // Temps écoulé en seconde depuis le 1er Janvier 1970
	private int iteration_ = 0;
	private LinkedList<Chaine> chaines_ = null;
	private String[][] donnees_lignes_pays_ = new String[3][];
	private int index_pays_traite_ = -1;
	private CSVReader[] csvreader_;
	private ArrayList<Chaine> classement_;
	private String sortie_ = "";
	boolean contamination_inter_pays_ = false;
	
	// MÉTHODES PUBLIQUES -----------------------------------------------------------------------------
	// Début : Constructeurs
	public CoronavirusTopChainCalculator(String[] chemins_fichiers_csv_pays, boolean contamination_inter_pays) throws FileNotFoundException {
		contamination_inter_pays_ = contamination_inter_pays;
		chaines_ = new LinkedList<Chaine>();
		csvreader_ = new CSVReader[] {new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[0]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[1]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[2])};
		for (int i = 0; i < donnees_lignes_pays_.length; i++) {
			donnees_lignes_pays_[i] = csvreader_[i].getNextLine(); 
		}
	}
	
	public CoronavirusTopChainCalculator(String[] chemins_fichiers_csv_pays, String chemin_fichier_avancement, boolean contamination_inter_pays) throws FileNotFoundException {
		contamination_inter_pays_ = contamination_inter_pays;
		chaines_ = new LinkedList<Chaine>();
		csvreader_ = new CSVReader[] {new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[0]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[1]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[2])};
		chemin_fichier_avancement_ = chemin_fichier_avancement;
		for (int i = 0; i < donnees_lignes_pays_.length; i++) {
			donnees_lignes_pays_[i] = csvreader_[i].getNextLine(); 
		}
	}
	// Fin : Constructeurs
	
	public boolean calculate() {
		boolean encore = chaineDeTraitement();
		if(encore) {
			afficherClassement();
		}
		return encore;
	}
	
	public boolean calculateFichier() throws IOException {
		boolean encore = chaineDeTraitement();
		if(encore) {
			ecrireClassement();
		}
		return encore;
	}

	
	public String getSortie() {return sortie_;};
	
	// MÉTHODES PRIVÉES -----------------------------------------------------------------------------------
	private boolean chaineDeTraitement(){
		if(temps_ != 0) {
			donnees_lignes_pays_[index_pays_traite_] = csvreader_[index_pays_traite_].getNextLine();
		}


		if((donnees_lignes_pays_[0] != null) || (donnees_lignes_pays_[1] != null) || (donnees_lignes_pays_[2] != null)) {
			modifierIndexPaysTraite();

			ligne_actuelle_pays[index_pays_traite_] = ligne_actuelle_pays[index_pays_traite_] + 1;
			
			int id_personne = Integer.parseInt(donnees_lignes_pays_[index_pays_traite_][0]);
			
			int id_personne_contaminatrice = -1;
			try {
				id_personne_contaminatrice = Integer.parseInt(donnees_lignes_pays_[index_pays_traite_][2].trim());
			} catch (NumberFormatException e) {
				// On ne fait rien car au cas où la valeur vaut -1.
				//e.printStackTrace();
			}
			
			classement_ = new ArrayList<Chaine>(Arrays.asList(new Chaine[] {new Chaine(), new Chaine(), new Chaine()}));
			
			Iterator<Chaine> iterateur_chaines_ = actionNouveauCas(id_personne, id_personne_contaminatrice);

			if(iterateur_chaines_ != null) {
				actualiserScoresChainesRestantes(iterateur_chaines_);
			}
			
			iteration_++;
			return true;
		} else {
			return false;
		}
	}

	private void modifierIndexPaysTraite() {
		boolean first = true;
		for(int i = 0; i < donnees_lignes_pays_.length; i++) {
			
			if(donnees_lignes_pays_[i] != null) {
				int temps_pays_i = (int)Float.parseFloat(donnees_lignes_pays_[i][1]);
				if(first) {
					temps_ = temps_pays_i;
					index_pays_traite_ = i;
					first = false;
				}
				else if(temps_pays_i < temps_) {
					temps_ = temps_pays_i;
					index_pays_traite_ = i;
				}
			}
		}
	}
	
	private Iterator<Chaine> actionNouveauCas(int id_personne, int id_personne_contaminatrice) {
		if(id_personne_contaminatrice != -1) {
			Iterator<Chaine> iterateur_chaines = chaines_.iterator();
			
			while(iterateur_chaines.hasNext()) {
				Chaine chaine_i = iterateur_chaines.next();
				chaine_i.actualiserPersonnesContaminees(temps_);
				
				if(!contamination_inter_pays_) {
				// Si on ne suppose plus que le virus s'arrête aux frontières des pays, on enlève la première condition.
					if(chaine_i.getIndexPays() == index_pays_traite_ && chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
						chaine_i.ajouterPersonne(new int[] {id_personne, temps_});
						if(!chaine_i.actualiserScore(temps_)) {
							iterateur_chaines.remove();
						} else {
							changerClassement(chaine_i, classement_);
						}
						return iterateur_chaines;
					}
				} else {
					if(chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
						chaine_i.ajouterPersonne(new int[] {id_personne, temps_});
						if(!chaine_i.actualiserScore(temps_)) {
							iterateur_chaines.remove();
						} else {
							changerClassement(chaine_i, classement_);
						}
						return iterateur_chaines;
					}
				}
				
				if(!chaine_i.actualiserScore(temps_)) {
					iterateur_chaines.remove();
				} else {
					changerClassement(chaine_i, classement_);
				}
			}
			// L'origine de cette contamination est une chaine de score 0 déjà supprimée, on créé une nouvelle chaine du coup.
			chaines_.add(new Chaine(index_pays_traite_, new int[] {id_personne, temps_}));
			changerClassement(chaines_.get(chaines_.size() - 1), classement_);
			return null;
		} else {
			chaines_.add(new Chaine(index_pays_traite_, new int[] {id_personne, temps_}));
			return chaines_.iterator();
		}
	}
	
	private void actualiserScoresChainesRestantes(Iterator<Chaine> iterateur_chaines){

		while(iterateur_chaines.hasNext()) {

			Chaine chaine_i = iterateur_chaines.next();
			if(!chaine_i.actualiserScore(temps_)) {
				iterateur_chaines.remove();

			} else {
				changerClassement(chaine_i, classement_);
			}
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

	
	// Début : Méthodes d'affichage
	private void afficherClassement() {
		if(classement_.get(1).getScore() == 0) {
			sortie_ = iteration_ + ": " + classement_.get(0).afficher();
		} else {
			if(classement_.get(2).getScore() == 0) {
				sortie_ = iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher();
			} else {
				sortie_ = iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher() + " " + classement_.get(2).afficher();
			}
		}
		System.out.println(sortie_);
	}
	
	private void ecrireClassement() throws IOException {
		if(chemin_fichier_avancement_ != null) {
			String contenu = "";
			if(classement_.get(1).getScore() == 0) {
				contenu = iteration_ + ": " + classement_.get(0).afficher() + "\n";
			} else {
				if(classement_.get(2).getScore() == 0) {
					contenu = iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher() + "\n";
				} else {
					contenu = iteration_ + ": " + classement_.get(0).afficher() + " " + classement_.get(1).afficher() + " " + classement_.get(2).afficher() + "\n";
				}
			}
		
	    	FileWriter fw = new FileWriter(new File(chemin_fichier_avancement_),true);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	bw.write(contenu);
	    	bw.flush();
	    	bw.close();
		}
	}
	// Fin : Méthodes d'affichage
}
