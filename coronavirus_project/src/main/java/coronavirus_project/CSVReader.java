package coronavirus_project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class CSVReader {
	private String separateur_;
	private int colonnes_a_extraire_[]; // La première colonne est la colonne 1.
	private boolean extraire_toutes_les_informations_ = true;
	
	
	public CSVReader(String separateur, int colonnes_a_extraire[]) {
		separateur_ = separateur;
		colonnes_a_extraire_ = colonnes_a_extraire;
		extraire_toutes_les_informations_ = false;
	}
	
	public CSVReader(String separateur) {
		separateur_ = separateur;
	}
	
	
	public String[] getDataLine(Path chemin, int ligne) throws NoSuchElementException {		
		
		try (Stream<String> lines = Files.lines(chemin)) {
		    String line = lines.skip(ligne).findFirst().get();
		    String[] all_data = line.split(separateur_);
		    
		    if(!extraire_toutes_les_informations_) {
			    String[] data = new String[colonnes_a_extraire_.length];
			    for(int i = 0; i < colonnes_a_extraire_.length; i++) {
			    	data[i] = all_data[colonnes_a_extraire_[i]-1]; // - 1 car on passe de la colonne désirée à l'index d'affectation.
			    }
			    return data;
		    } else {
		    	return all_data;
		    }
		    
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}

