package ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates;

import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilityCandidate;

public interface TripCandidate extends UtilityCandidate {
	String getMode();
}
