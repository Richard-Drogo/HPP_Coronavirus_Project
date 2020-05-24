package coronavirus_project;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCSVReader extends Thread{
	private CSVReader[] csvreader_;
	private int[] ligne_actuelle_pays = new int[] {0, 0, 0}; // France, Italy, Spain
	private String[][] donnees_lignes_pays_ = new String[3][];
	private boolean donnees_en_attente_ = true;
	private int temps_ = 0;
	private int index_pays_traite_ = -1;
	
	// Début : Données récupérées par le Thread de mise-à-jour des chaines (2)
	private LinkedList<Integer> indices_pays_traites_ = new LinkedList<Integer>();
	private LinkedList<Integer> temps_partages_ = new LinkedList<Integer>();
	private LinkedList<Integer> id_personne_ = new LinkedList<Integer>();
	private LinkedList<Integer> id_personne_contaminatrice_ = new LinkedList<Integer>();
	// Fin : Données récupérées par le Thread de mise-à-jour des chaines (2)
	
	
	
	public ThreadCSVReader(String[] chemins_fichiers_csv_pays) throws FileNotFoundException {
		csvreader_ = new CSVReader[] {new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[0]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[1]), new CSVReader(",",new int[] {1,5,6}, chemins_fichiers_csv_pays[2])};
		for (int i = 0; i < donnees_lignes_pays_.length; i++) {
			donnees_lignes_pays_[i] = csvreader_[i].getNextLine(); 
		}
	}
	
	
    @Override
    public void run() {
        while (donnees_en_attente_) {
			if(temps_ != 0) {
				donnees_lignes_pays_[index_pays_traite_] = csvreader_[index_pays_traite_].getNextLine();
			}
			
			if((donnees_lignes_pays_[0] != null) || (donnees_lignes_pays_[1] != null) || (donnees_lignes_pays_[2] != null)) {
				modifierIndexPaysTraite();
				synchronized(this) {
					indices_pays_traites_.add(index_pays_traite_);
					temps_partages_.add(temps_);
					
					ligne_actuelle_pays[index_pays_traite_] = ligne_actuelle_pays[index_pays_traite_] + 1;
					
						id_personne_.add(Integer.parseInt(donnees_lignes_pays_[index_pays_traite_][0]));
					try {
						id_personne_contaminatrice_.add(Integer.parseInt(donnees_lignes_pays_[index_pays_traite_][2].trim()));
					} catch (NumberFormatException e) {
						id_personne_contaminatrice_.add(-1);
						//e.printStackTrace();
					}
					//System.out.println("PUSH : " + indices_pays_traites_.get(indices_pays_traites_.size() - 1) + " | " + temps_partages_.get(temps_partages_.size() - 1) + " | " + id_personne_.get(id_personne_.size() - 1) + " | " + id_personne_contaminatrice_.get(id_personne_contaminatrice_.size() - 1));

					this.notify();
				}
			} else {
				donnees_en_attente_ = false;
			}
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

	
	// [0] : index_pays_traite_ sinon -1
	// [1] : temps_ sinon -1
	// [2] : id_personne_[0] sinon -1
	// [3] : id_personne_contaminatrice[0] sinon -1
	public synchronized int[] getDonnees() {
		while((id_personne_.size() == 0) && (id_personne_contaminatrice_.size() == 0) ) {
			if(donnees_en_attente_) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return new int[] {-1, -1, -1, -1};
			}
		}

		//System.out.println("GET : " + indices_pays_traites_.get(0) + " | " + temps_partages_.get(0) + " | " + id_personne_.get(0) + " | " + id_personne_contaminatrice_.get(0));
		return new int[] {indices_pays_traites_.remove(), temps_partages_.remove(), id_personne_.remove(), id_personne_contaminatrice_.remove()};

	}
}
