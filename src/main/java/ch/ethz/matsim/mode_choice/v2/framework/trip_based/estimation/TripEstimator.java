package ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface TripEstimator {
	public TripCandidate estimateTrip(String mode, ModeChoiceTrip trip, List<TripCandidate> preceedingTrips);
}
