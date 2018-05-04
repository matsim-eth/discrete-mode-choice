package ch.ethz.matsim.mode_choice.mnl;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public interface ModeChoiceAlternative {
	double estimateUtility(ModeChoiceTrip trip);
	boolean isFeasible(ModeChoiceTrip trip);
	boolean isChainMode();
}
