package ch.ethz.matsim.mode_choice.framework.trip_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public interface TripEstimator {
	public TripCandidate estimateTrip(String mode, ModeChoiceTrip trip, List<TripCandidate> preceedingTrips);
}
