package coronavirus_project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class CSVReader {
	private String separateur_;
	private int colonnes_a_extraire_[]; // La première colonne est la colonne 1.
	private BufferedReader bf_;
	
	public CSVReader(String separateur, int colonnes_a_extraire[], String chemin) throws FileNotFoundException {
		separateur_ = separateur;
		colonnes_a_extraire_ = colonnes_a_extraire;
		bf_ = new BufferedReader(new FileReader(chemin));
	}
	
	public void liberer() {
		try {
			bf_.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getNextLine() {
	    String line;
		try {
			line = bf_.readLine();
			if(line != null) {
			    String[] all_data = line.split(separateur_);
			    
			    String[] data = new String[colonnes_a_extraire_.length];
			    for(int i = 0; i < colonnes_a_extraire_.length; i++) {
			    	data[i] = all_data[colonnes_a_extraire_[i]-1]; // - 1 car on passe de la colonne désirée à l'index d'affectation.
			    }
			    return data;
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

