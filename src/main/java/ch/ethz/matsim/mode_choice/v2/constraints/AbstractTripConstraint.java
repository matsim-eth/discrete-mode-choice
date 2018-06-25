package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public abstract class AbstractTripConstraint implements TripConstraint {
	public boolean validateBeforeEstimation(ModeChoiceTrip trip, String mode, List<String> previousModes) {
		return true;
	}

	public boolean validateAfterEstimation(ModeChoiceTrip trip, TripCandidate candidate,
			List<TripCandidate> previousCandidates) {
		return true;
	}
}
