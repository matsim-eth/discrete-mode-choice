package ch.ethz.matsim.mode_choice.framework.tour_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.framework.utilities.UtilityCandidate;

public interface TourCandidate extends UtilityCandidate {
	List<TripCandidate> getTripCandidates();
}
