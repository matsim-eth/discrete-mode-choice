package ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation;

import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilityCandidate;

public interface TripCandidate extends UtilityCandidate {
	String getMode();
}
