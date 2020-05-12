package coronavirus_project;

import java.util.Scanner;
import java.lang.NumberFormatException;

public class Main {

	public static void main(String[] args) {
		System.out.println("Saisissez un entier pour lancer le programme : ");
		Scanner scanner = new Scanner(System.in);
		scanner.nextInt();
		
		
		CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator();		
		boolean donnees_en_attente = true;
		while(donnees_en_attente) {
			donnees_en_attente = ctcc.calculate();
			// D�commenter les deux lignes suivantes pour faire pas � pas.
			//System.out.println("Saisissez un entier pour lancer le tour suivant : ");
			//scanner.nextInt();
		}
    }

}
