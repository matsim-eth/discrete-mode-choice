package ch.ethz.matsim.mode_choice.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public interface ModalTripEstimator {
	TripCandidate estimateTrip(ModeChoiceTrip trip, List<TripCandidate> preceedingTrips);
}
