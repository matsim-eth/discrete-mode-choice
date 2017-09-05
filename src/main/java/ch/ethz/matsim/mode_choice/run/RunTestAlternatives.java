package ch.ethz.matsim.mode_choice.run;

import java.util.Arrays;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.Counter;

import ch.ethz.matsim.mode_choice.alternatives.ChainAlternatives;
import ch.ethz.matsim.mode_choice.alternatives.TripChainAlternatives;

public class RunTestAlternatives {
	static public void main(String[] args) {
		Config config = ConfigUtils.loadConfig("/home/sebastian/modechoice/zurich/config_001.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		
		List<String> chainModes = Arrays.asList("car", "bike");
		List<String> nonChainModes = Arrays.asList("pt", "walk");
		
		ChainAlternatives chainAlternatives = new TripChainAlternatives();
		
		Counter counter = new Counter("", "");
		
		for (Person person : scenario.getPopulation().getPersons().values()) {
			for (List<String> chain : chainAlternatives.getTripChainAlternatives(person.getSelectedPlan(), chainModes, nonChainModes)) {
				
			}
			
			counter.incCounter();
		}
		
		System.out.println("FINISH");
	}
}
