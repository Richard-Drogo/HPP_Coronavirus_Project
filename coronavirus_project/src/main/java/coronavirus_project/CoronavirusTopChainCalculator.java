package coronavirus_project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class CoronavirusTopChainCalculator {
	
	private final Path FRANCE = new File(getClass().getClassLoader().getResource("France.csv").getFile()).toPath();
	private final Path ITALY = new File(getClass().getClassLoader().getResource("Italy.csv").getFile()).toPath();
	private final Path SPAIN = new File(getClass().getClassLoader().getResource("Spain.csv").getFile()).toPath();
	
	private String chemin_fichier_avancement_ = null;
	private int[] ligne_actuelle_pays = new int[] {0, 0, 0}; // France, Italy, Spain
	private int temps_ = 0; // Temps écoulé en seconde depuis le 1er Janvier 1970
	private int iteration_ = 0;
	private LinkedList<Chaine> chaines_ = null;
	
	private final CSVReader csvreader_ = new CSVReader(",",new int[] {1,5,6});
	
	
	
	public CoronavirusTopChainCalculator() {
		chaines_ = new LinkedList<Chaine>();
	}
	
	public CoronavirusTopChainCalculator(String chemin_fichier_avancement) {
		chaines_ = new LinkedList<Chaine>();
		chemin_fichier_avancement_ = chemin_fichier_avancement;
	}
	
	
	public boolean calculate() /*throws IOException*/ {
		String[][] donnees_lignes_pays = getDataLigne();


		if((donnees_lignes_pays[0] != null) || (donnees_lignes_pays[1] != null) || (donnees_lignes_pays[2] != null)) {
			int index_pays_traite = retournerIndexPaysTraite(donnees_lignes_pays);

			
			ligne_actuelle_pays[index_pays_traite] = ligne_actuelle_pays[index_pays_traite] + 1;
			
			int id_personne = Integer.parseInt(donnees_lignes_pays[index_pays_traite][0]);
			
			int id_personne_contaminatrice = -1;
			try {
				id_personne_contaminatrice = Integer.parseInt(donnees_lignes_pays[index_pays_traite][2].trim());
			} catch (NumberFormatException e) {
				// On ne fait rien car au cas où la valeur vaut -1.
				//e.printStackTrace();
			}
			
			ArrayList<Chaine> classement = new ArrayList<Chaine>(Arrays.asList(new Chaine[] {new Chaine(), new Chaine(), new Chaine()}));
			
			Iterator<Chaine> iterateur_chaines_ = actionNouveauCas(index_pays_traite, id_personne, id_personne_contaminatrice, classement);

			if(iterateur_chaines_ != null) {
				actualiserScoresChainesRestantes(iterateur_chaines_, classement);
			}
			
			iteration_++;
			afficherClassement(classement);
			//ecrireClassement(classement);
			return true;
		} else {
			return false;
		}
	}
	

	
	private String[][] getDataLigne() {
		String[][] donnees_lignes_pays = new String[3][];
		try {
			donnees_lignes_pays[0] = csvreader_.getDataLine(FRANCE, ligne_actuelle_pays[0]);
		} catch (NoSuchElementException e) {
			donnees_lignes_pays[0] = null;
		}
		try {
			donnees_lignes_pays[1] = csvreader_.getDataLine(ITALY, ligne_actuelle_pays[1]);
		} catch (NoSuchElementException e) {
			donnees_lignes_pays[1] = null;
		}
		try {
			donnees_lignes_pays[2] = csvreader_.getDataLine(SPAIN, ligne_actuelle_pays[2]);
		} catch (NoSuchElementException e) {
			donnees_lignes_pays[2] = null;
		}
		return donnees_lignes_pays;
	}
	
	private int retournerIndexPaysTraite(String[][] donnees_lignes_pays) {
		int index_pays_traite = 0;
		boolean first = true;
		for(int i = 0; i < donnees_lignes_pays.length; i++) {
			
			if(donnees_lignes_pays[i] != null) {
				int temps_pays_i = (int)Float.parseFloat(donnees_lignes_pays[i][1]);
				if(first) {
					temps_ = temps_pays_i;
					index_pays_traite = i;
					first = false;
				}
				else if(temps_pays_i < temps_) {
					temps_ = temps_pays_i;
					index_pays_traite = i;
				}
			}
		}
		return index_pays_traite;
	}
	
	private Iterator<Chaine> actionNouveauCas(int index_pays_traite, int id_personne, int id_personne_contaminatrice, ArrayList<Chaine> classement) {
		if(id_personne_contaminatrice != -1) {
			Iterator<Chaine> iterateur_chaines = chaines_.iterator();
			
			while(iterateur_chaines.hasNext()) {
				Chaine chaine_i = iterateur_chaines.next();
				
				// Si on ne suppose plus que le virus s'arrête aux frontières des pays, on enlève la première condition.
				if(chaine_i.getIndexPays() == index_pays_traite && chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
					chaine_i.ajouterPersonne(new int[] {id_personne, temps_});
					changerClassement(chaine_i, classement);
					return iterateur_chaines;
				}
				
				if(!chaine_i.actualiserScore(temps_)) {
					iterateur_chaines.remove();
				} else {
					changerClassement(chaine_i, classement);
				}
			}
			// L'origine de cette contamination est une chaine de score 0 déjà supprimée, on créé une nouvelle chaine du coup.
			chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps_}));
			changerClassement(chaines_.get(chaines_.size() - 1), classement);
			return null;
		} else {
			chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps_}));
			return chaines_.iterator();
		}
	}
	
	// Retourne le classement.
	private void actualiserScoresChainesRestantes(Iterator<Chaine> iterateur_chaines, ArrayList<Chaine> classement){

		while(iterateur_chaines.hasNext()) {

			Chaine chaine_i = iterateur_chaines.next();
			if(!chaine_i.actualiserScore(temps_)) {
				iterateur_chaines.remove();

			} else {
				changerClassement(chaine_i, classement);
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
	
	
	private void afficherClassement(ArrayList<Chaine> classement) {
		if(classement.get(1).getScore() == 0) {
			System.out.println(iteration_ + ": " + classement.get(0).afficher());
		} else {
			if(classement.get(2).getScore() == 0) {
				System.out.println(iteration_ + ": " + classement.get(0).afficher() + " " + classement.get(1).afficher());
			} else {
				System.out.println(iteration_ + ": " + classement.get(0).afficher() + " " + classement.get(1).afficher() + " " + classement.get(2).afficher());
			}
		}
	}
	
	private void ecrireClassement(ArrayList<Chaine> classement) throws IOException {
		if(chemin_fichier_avancement_ != null) {
			String contenu = "";
			if(classement.get(1).getScore() == 0) {
				contenu = iteration_ + ": " + classement.get(0).afficher() + "\n";
			} else {
				if(classement.get(2).getScore() == 0) {
					contenu = iteration_ + ": " + classement.get(0).afficher() + " " + classement.get(1).afficher() + "\n";
				} else {
					contenu = iteration_ + ": " + classement.get(0).afficher() + " " + classement.get(1).afficher() + " " + classement.get(2).afficher() + "\n";
				}
			}
		
	    	FileWriter fw = new FileWriter(new File(chemin_fichier_avancement_),true);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	bw.write(contenu);
	    	bw.flush();
	    	bw.close();
		}
	}
}
