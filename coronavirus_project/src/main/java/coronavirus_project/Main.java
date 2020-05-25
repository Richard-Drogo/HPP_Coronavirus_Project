package coronavirus_project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

	// Début : Configuration
	private final static boolean ECRITURE_FICHIER = false;
	private final static boolean CONTAMINATION_INTER_PAYS = false;
	private final static boolean MULTITHREADING = false;
	private final static String CHEMIN_FICHIER_AVANCEMENT = "C:\\Users\\richa\\Documents\\Temporaire\\FISE2\\OPTION1\\HPP\\Projet_Coronavirus\\Avancement.txt"; // NE PAS OUBLIER DE DOUBLER LES SLASHS SUR WINDOWS
	private final static String FRANCE = ClassLoader.getSystemClassLoader().getResource("1000000/France.csv").getPath();
	private final static String ITALY = ClassLoader.getSystemClassLoader().getResource("1000000/Italy.csv").getPath();
	private final static String SPAIN = ClassLoader.getSystemClassLoader().getResource("1000000/Spain.csv").getPath();
	// Fin : Configuration
	
	public static void main(String[] args) {
		CoronavirusTopChainCalculator ctcc;
		Scanner scanner;

		if (!ECRITURE_FICHIER) {
			try {
				ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE, ITALY, SPAIN}, CONTAMINATION_INTER_PAYS, MULTITHREADING);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
				return;
			}
		} else {
			try {
				ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE, ITALY, SPAIN}, CHEMIN_FICHIER_AVANCEMENT, CONTAMINATION_INTER_PAYS, MULTITHREADING);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
				return;
			}
		}
		
		// Si nous sommes ici cela veut dire que la configuration est OK !
		System.out.println("Saisissez un entier pour lancer le programme : ");
		scanner = new Scanner(System.in);
		scanner.nextInt();
		scanner.close();
		
		if(MULTITHREADING) {
			ctcc.demarrerLesThreads();
		}
		
		boolean donnees_en_attente = true;
		if(!ECRITURE_FICHIER) {
			while(donnees_en_attente) {
				donnees_en_attente = ctcc.calculate();
			}
		} else {
			while(donnees_en_attente) {
				try {
					donnees_en_attente = ctcc.calculateFichier();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
					return;
				}
			}
		}

		System.out.println("Le programme s'est terminé avec succès : " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()));
		System.exit(0);
		return;
    }
	
}
