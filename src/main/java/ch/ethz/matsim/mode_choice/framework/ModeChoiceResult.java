package ch.ethz.matsim.mode_choice.framework;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public interface ModeChoiceResult {
	List<TripCandidate> getTripCandidates();
}
