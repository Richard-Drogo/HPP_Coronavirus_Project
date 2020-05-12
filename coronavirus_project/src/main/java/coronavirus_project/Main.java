package coronavirus_project;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Point of the program...");
		final String FRANCE = "France.csv";
		final String ITALY = "Italy.csv";
		final String SPAIN = "Spain.csv";
		
		//CSVReader csvreader = new CSVReader(","); // Pour prendre toutes les informations 
		CSVReader csvreader = new CSVReader(",",new int[] {1,5,6}); // Colonnes 1, 5 et 6.
		
		String[] dataline = csvreader.getDataLine(SPAIN,0);
		
		for(int i = 0; i < dataline.length; i ++) {
			System.out.println(dataline[i]);
		}
		
		
    }

}
