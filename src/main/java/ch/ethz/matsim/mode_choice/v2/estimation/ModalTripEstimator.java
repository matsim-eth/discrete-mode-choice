package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public interface ModalTripEstimator {
	TripCandidate estimateTrip(ModeChoiceTrip trip, List<TripCandidate> preceedingTrips);
}
