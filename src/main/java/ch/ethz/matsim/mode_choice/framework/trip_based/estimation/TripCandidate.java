package ch.ethz.matsim.mode_choice.framework.trip_based.estimation;

import ch.ethz.matsim.mode_choice.framework.utilities.UtilityCandidate;

public interface TripCandidate extends UtilityCandidate {
	String getMode();
}
