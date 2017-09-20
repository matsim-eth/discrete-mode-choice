package ch.ethz.matsim.mode_choice.alternatives;

import java.util.List;

import org.matsim.api.core.v01.population.Plan;

public interface ChainAlternatives {

	List<List<String>> getTripChainAlternatives(Plan plan, List<String> chainModes, List<String> nonChainModes, boolean matsimStyle);

}