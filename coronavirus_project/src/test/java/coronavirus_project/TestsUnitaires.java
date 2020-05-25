package coronavirus_project;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class TestsUnitaires {

	private final static boolean MULTITHREADING = true;
	private final static boolean CONTAMINATION_INTER_PAYS = false; // N'est pas utilisé pour le test du professeur.
	
	private final static String FRANCE_20 = ClassLoader.getSystemClassLoader().getResource("20/France.csv").getPath();
	private final static String ITALY_20 = ClassLoader.getSystemClassLoader().getResource("20/Italy.csv").getPath();
	private final static String SPAIN_20 = ClassLoader.getSystemClassLoader().getResource("20/Spain.csv").getPath();
	
	private final static String FRANCE_T1 = ClassLoader.getSystemClassLoader().getResource("test1/France.csv").getPath();
	private final static String ITALY_T1 = ClassLoader.getSystemClassLoader().getResource("test1/Italy.csv").getPath();
	private final static String SPAIN_T1 = ClassLoader.getSystemClassLoader().getResource("test1/Spain.csv").getPath();
	
	private final static String FRANCE_T2 = ClassLoader.getSystemClassLoader().getResource("test2/France.csv").getPath();
	private final static String ITALY_T2 = ClassLoader.getSystemClassLoader().getResource("test2/Italy.csv").getPath();
	private final static String SPAIN_T2 = ClassLoader.getSystemClassLoader().getResource("test2/Spain.csv").getPath();

	private final static String FRANCE_T3 = ClassLoader.getSystemClassLoader().getResource("test3/France.csv").getPath();
	private final static String ITALY_T3 = ClassLoader.getSystemClassLoader().getResource("test3/Italy.csv").getPath();
	private final static String SPAIN_T3 = ClassLoader.getSystemClassLoader().getResource("test3/Spain.csv").getPath();

	private final static String FRANCE_PROFESSEUR = ClassLoader.getSystemClassLoader().getResource("Exemple_Professeur/France.csv").getPath();
	private final static String ITALY_PROFESSEUR = ClassLoader.getSystemClassLoader().getResource("Exemple_Professeur/Italy.csv").getPath();
	private final static String SPAIN_PROFESSEUR = ClassLoader.getSystemClassLoader().getResource("Exemple_Professeur/Spain.csv").getPath();
	
	
	@Test
	public void DataSet20() {
		String[] attendues = new String[20];
		String[] obtenues = new String[20];
		
		attendues[0] = "1: Spain, 0, 10;";
		attendues[1] = "2: Italy, 1, 10;";
		attendues[2] = "3: Spain, 2, 10;";
		attendues[3] = "4: Spain, 3, 10;";
		attendues[4] = "5: France, 4, 10; Spain, 3, 4;";
		attendues[5] = "6: France, 5, 10; France, 4, 4;"; 
		attendues[6] = "7: Italy, 6, 10; France, 5, 4;";
		attendues[7] = "8: Spain, 7, 10; Italy, 6, 4;";
		attendues[8] = "9: Spain, 7, 10; Spain, 8, 10;";
		attendues[9] = "10: Spain, 8, 10; France, 9, 10; Spain, 7, 4;";
		attendues[10] = "11: France, 9, 10; Italy, 10, 10; Spain, 8, 4;";
		attendues[11] = "12: Italy, 10, 10; Spain, 11, 10; France, 9, 4;";
		attendues[12] = "13: Spain, 11, 10; Italy, 12, 10; Italy, 10, 4;";
		attendues[13] = "14: Italy, 12, 10; France, 13, 10; Spain, 11, 4;";
		attendues[14] = "15: France, 13, 10; France, 14, 10; Spain, 11, 4;";
		attendues[15] = "16: France, 14, 10; Italy, 15, 10; Italy, 12, 4;";
		attendues[16] = "17: Italy, 15, 10; Spain, 16, 10; France, 13, 4;";
		attendues[17] = "18: Italy, 15, 10; Spain, 16, 10; Italy, 17, 10;";
		attendues[18] = "19: Spain, 16, 10; Italy, 17, 10; Italy, 18, 10;";
		attendues[19] = "20: Italy, 17, 10; Italy, 18, 10; France, 19, 10;";
		
		System.out.println("Début du test : DATASET_20");
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_20, ITALY_20, SPAIN_20}, CONTAMINATION_INTER_PAYS, MULTITHREADING);
			
			if(MULTITHREADING) {
				ctcc.demarrerLesThreads();
			}
			
			boolean donnees_en_attente = true;
			int i = 0;
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
				if(donnees_en_attente) {
					obtenues[i] = ctcc.getSortie().trim();
					i++;
				}
			}
			assertArrayEquals(attendues, obtenues);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Vérifier le chemin des fichiers csv...");
		}
		
		System.out.println("Fin du test : DATASET_20");
	}
	
	@Test
	public void DataSetBranch() {
		String[] attendues = new String[6];
		String[] obtenues = new String[6];
		
		attendues[0] = "1: Italy, 0, 10;";
		attendues[1] = "2: Italy, 0, 10; Spain, 1, 10;";
		attendues[2] = "3: France, 2, 10;";
		attendues[3] = "4: France, 2, 20;";
		attendues[4] = "5: France, 2, 24;";
		attendues[5] = "6: France, 2, 18;";
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_T1, ITALY_T1, SPAIN_T1}, CONTAMINATION_INTER_PAYS, MULTITHREADING);
			
			if(MULTITHREADING) {
				ctcc.demarrerLesThreads();
			}
			
			boolean donnees_en_attente = true;
			int i = 0;
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
				if(donnees_en_attente) {
					obtenues[i] = ctcc.getSortie().trim();
					i++;
				}
			}
			assertArrayEquals(attendues, obtenues);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Vérifier le chemin des fichiers csv...");
		}
	}
	
	@Test
	public void DataSetBranches() {
		String[] attendues = new String[9];
		String[] obtenues = new String[9];
		
		attendues[0] = "1: Spain, 0, 10;";
		attendues[1] = "2: Italy, 1, 10;";
		attendues[2] = "3: Italy, 1, 10; France, 2, 10;";
		attendues[3] = "4: Italy, 1, 20; France, 2, 10;";
		attendues[4] = "5: France, 2, 20; Italy, 1, 14;";
		attendues[5] = "6: Italy, 1, 24; France, 2, 20;";
		attendues[6] = "7: Italy, 1, 24; France, 2, 24;";
		attendues[7] = "8: Italy, 1, 18; France, 2, 18;";
		attendues[8] = "9: Italy, 1, 18; France, 2, 18;";
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_T2, ITALY_T2, SPAIN_T2}, CONTAMINATION_INTER_PAYS, MULTITHREADING);
			
			if(MULTITHREADING) {
				ctcc.demarrerLesThreads();
			}
			
			boolean donnees_en_attente = true;
			int i = 0;
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
				if(donnees_en_attente) {
					obtenues[i] = ctcc.getSortie().trim();
					i++;
				}
			}
			assertArrayEquals(attendues, obtenues);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Vérifier le chemin des fichiers csv...");
		}
	}
	
	@Test
	public void DataSetProfesseur() {
		String[] attendues = new String[6];
		String[] obtenues = new String[6];
		
		attendues[0] = "1: France, 0, 10;";
		attendues[1] = "2: France, 0, 20;";
		attendues[2] = "3: France, 0, 20; France, 2, 10;";
		attendues[3] = "4: Italy, 3, 10; France, 0, 8; France, 2, 4;";
		attendues[4] = "5: Spain, 4, 10;";
		attendues[5] = "6: Spain, 4, 10; Italy, 5, 10;";

		System.out.println("Début du test : DATASET_PROFESSEUR");
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_PROFESSEUR, ITALY_PROFESSEUR, SPAIN_PROFESSEUR}, true, MULTITHREADING);
			
			if(MULTITHREADING) {
				ctcc.demarrerLesThreads();
			}
			
			boolean donnees_en_attente = true;
			int i = 0;
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
				if(donnees_en_attente) {
					obtenues[i] = ctcc.getSortie().trim();
					i++;
				}
			}
			assertArrayEquals(attendues, obtenues);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Vérifier le chemin des fichiers csv...");
		}
		
		System.out.println("Fin du test : DATASET_PROFESSEUR");
	}
	
	@Test
	public void DataSetOldBranch() {
		String[] attendues = new String[7];
		String[] obtenues = new String[7];
		
		attendues[0] = "1: Italy, 0, 10;";
		attendues[1] = "2: Italy, 0, 10; Spain, 1, 10;";
		attendues[2] = "3: France, 2, 10;";
		attendues[3] = "4: France, 2, 20;";
		attendues[4] = "5: France, 2, 24;";
		attendues[5] = "6: France, 2, 18;";
		attendues[6] = "7: France, 2, 14; France, 6, 10;";
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_T3, ITALY_T3, SPAIN_T3}, false);
			boolean donnees_en_attente = true;
			int i = 0;
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
				if(donnees_en_attente) {
					obtenues[i] = ctcc.getSortie();
					i++;
				}
			}
			assertArrayEquals(attendues, obtenues);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Vérifier le chemin des fichiers csv...");
		}
	}

}
