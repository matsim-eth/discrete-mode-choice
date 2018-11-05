package ch.ethz.matsim.mode_choice.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public class CompositeTripConstraint implements TripConstraint {
	final private List<TripConstraint> constraints;

	public CompositeTripConstraint(List<TripConstraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public boolean validateBeforeEstimation(ModeChoiceTrip trip, String mode, List<String> previousModes) {
		for (TripConstraint constraint : constraints) {
			if (!constraint.validateBeforeEstimation(trip, mode, previousModes)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(ModeChoiceTrip trip, TripCandidate candidate,
			List<TripCandidate> previousCandidates) {
		for (TripConstraint constraint : constraints) {
			if (!constraint.validateAfterEstimation(trip, candidate, previousCandidates)) {
				return false;
			}
		}

		return true;
	}
}
