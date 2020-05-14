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
	
	
	public boolean calculate() throws IOException {
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


		if((donnees_lignes_pays[0] != null) || (donnees_lignes_pays[1] != null) || (donnees_lignes_pays[2] != null)) {
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
			
			ligne_actuelle_pays[index_pays_traite] = ligne_actuelle_pays[index_pays_traite] + 1;
			
			int id_personne = Integer.parseInt(donnees_lignes_pays[index_pays_traite][0]);
			
			int id_personne_contaminatrice = -1;
			try {
				id_personne_contaminatrice = Integer.parseInt(donnees_lignes_pays[index_pays_traite][2].trim());
			} catch (NumberFormatException e) {
				// On ne fait rien car au cas où la valeur vaut -1.
				//e.printStackTrace();
			}
			
			if(id_personne_contaminatrice != -1) {
				// TODO : Optimisation possible Richard, en profiter pour actualiser les scores ici... Et reprendre plus tard à partir de l'indice de la chaine auquel on s'est arrêté.
				Iterator<Chaine> iterateur = chaines_.iterator();
				while(iterateur.hasNext()) {
					Chaine chaine_i = iterateur.next();
					if(chaine_i.presenceIdPersonneContaminatrice(id_personne_contaminatrice)) {
						chaine_i.ajouterPersonne(new int[] {id_personne, temps_});
						break;
					}
				}
				// L'origine de cette contamination est une chaine de score 0 déjà supprimée, on créé une nouvelle chaine du coup.
				chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps_}));
				
			} else {
				chaines_.add(new Chaine(index_pays_traite, new int[] {id_personne, temps_}));
			}

			ArrayList<Chaine> classement = new ArrayList<Chaine>(Arrays.asList(new Chaine[] {new Chaine(), new Chaine(), new Chaine()}));
			Iterator<Chaine> iterateur_maj_scores = chaines_.iterator();
			while(iterateur_maj_scores.hasNext()) {

				Chaine chaine_i = iterateur_maj_scores.next();
				if(!chaine_i.actualiserScore(temps_)) {
					iterateur_maj_scores.remove();

				} else {
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
							break; // Pour quitter la boucle
						} else {
							if(i == 0) {
								classement.set(2, classement.get(1)); 
								classement.set(1, classement.get(0)); 
								classement.set(0, chaine_i);
							}
						}
					}
				}
			}
			
			iteration_++;
			//afficherClassement(classement);
			ecrireClassement(classement);
			return true;
		} else {
			return false;
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
