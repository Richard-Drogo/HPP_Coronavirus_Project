package coronavirus_project;

import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Saisissez un entier pour lancer le programme : ");
		Scanner scanner = new Scanner(System.in);
		scanner.nextInt();
		
		// PENSER A DOUBLER LES '\'
		//String chemin_fichier_avancement = "C:\\Users\\richa\\Documents\\Temporaire\\FISE2\\OPTION1\\HPP\\Projet_Coronavirus\\Avancement.txt";
		//CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(chemin_fichier_avancement);
		CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator();
		
		boolean donnees_en_attente = true;
		while(donnees_en_attente) {
			donnees_en_attente = ctcc.calculate();
			/*
			try {
				donnees_en_attente = ctcc.calculate();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("V�rifier le chemin du fichier d'avancement...");
				break;
			}*/
			// D�commenter les deux lignes suivantes pour faire pas � pas.
			//System.out.println("Saisissez un entier pour lancer le tour suivant : ");
			//scanner.nextInt();
		}

    }

}
