package ch.ethz.matsim.mode_choice.estimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripEstimator;

public class ModeAwareTripEstimator implements TripEstimator {
	final private Map<String, ModalTripEstimator> modalTripEstimators = new HashMap<>();

	public void addEstimator(String mode, ModalTripEstimator estimator) {
		modalTripEstimators.put(mode, estimator);
	}

	@Override
	public TripCandidate estimateTrip(String mode, ModeChoiceTrip trip, List<TripCandidate> preceedingTrips) {
		ModalTripEstimator delegate = modalTripEstimators.get(mode);

		if (delegate != null) {
			TripCandidate candidate = delegate.estimateTrip(trip, preceedingTrips);

			if (!candidate.getMode().equals(mode)) {
				throw new IllegalArgumentException(
						String.format("Expected mode '%s' instead of '%s' to be returned by %s", mode,
								candidate.getMode(), delegate.getClass().toString()));
			}

			return candidate;
		}

		throw new IllegalArgumentException(String.format("No estimator found for mode '%s'", mode));
	}
}
