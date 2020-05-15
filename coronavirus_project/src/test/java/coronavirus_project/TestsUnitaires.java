package coronavirus_project;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class TestsUnitaires {

	private final static String FRANCE_20 = ClassLoader.getSystemClassLoader().getResource("20/France.csv").getPath();
	private final static String ITALY_20 = ClassLoader.getSystemClassLoader().getResource("20/Italy.csv").getPath();
	private final static String SPAIN_20 = ClassLoader.getSystemClassLoader().getResource("20/Spain.csv").getPath();
	
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
		
		try {
			CoronavirusTopChainCalculator ctcc = new CoronavirusTopChainCalculator(new String[] {FRANCE_20, ITALY_20, SPAIN_20});
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
