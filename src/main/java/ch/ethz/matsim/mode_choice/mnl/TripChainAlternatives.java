package ch.ethz.matsim.mode_choice.mnl;

import java.util.List;

import org.matsim.api.core.v01.population.Plan;

public interface TripChainAlternatives {
	List<List<String>> getTripChainAlternatives(Plan plan, List<String> chainModes, List<String> nonChainModes);
}
