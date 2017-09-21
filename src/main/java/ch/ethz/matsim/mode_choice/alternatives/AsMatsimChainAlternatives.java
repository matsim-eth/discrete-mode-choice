package ch.ethz.matsim.mode_choice.alternatives;

import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;

public class AsMatsimChainAlternatives implements ChainAlternatives {
	
	private Map<Id<Person>, List<List<String>>> alternatives;
	
	public AsMatsimChainAlternatives(Map<Id<Person>, List<List<String>>> alternatives) {
		
		this.alternatives = alternatives;
	}

	@Override
	public List<List<String>> getTripChainAlternatives(Plan plan, List<String> chainModes, List<String> nonChainModes) {		
		
		return this.alternatives.get(plan.getPerson().getId());
	}

}
