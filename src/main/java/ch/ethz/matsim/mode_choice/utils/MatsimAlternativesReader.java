package ch.ethz.matsim.mode_choice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;

public class MatsimAlternativesReader {	
	
	public Map<Id<Person>, List<List<String>>> read(String filePath) throws IOException {
		
		Map<Id<Person>, List<List<String>>> alternatives = new HashMap<>();
		
		final BufferedReader readLink = IOUtils.getBufferedReader(filePath);
		
		String s = readLink.readLine();
		
		while (s != null) {
			
			String[] arr = s.split(";");
			
			List<List<String>> allChains = new LinkedList<>();
			
			if (arr.length > 1) {
				
				for (int i = 1; i < arr.length; i++) {
					
					String[] modes = arr[i].split(",");
					List<String> chain = new LinkedList<>();
					
					for (String mode : modes)
						chain.add(mode);
					
					allChains.add(chain);
				}
			}
			
			alternatives.put(Id.createPersonId(arr[0]), allChains);
			
			s = readLink.readLine();
		}
		
		return alternatives;
	}

}
