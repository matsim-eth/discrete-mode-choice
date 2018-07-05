package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public interface ModeChoiceResult {
	List<TripCandidate> getTripCandidates();
}
