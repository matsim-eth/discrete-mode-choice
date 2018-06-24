package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class CompositeTripConstraint implements TripConstraint {
	final private List<TripConstraint> constraints;

	public CompositeTripConstraint(List<TripConstraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public boolean validateBeforeEstimation(String mode) {
		for (TripConstraint constraint : constraints) {
			if (!constraint.validateBeforeEstimation(mode)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(TripCandidate candidate) {
		for (TripConstraint constraint : constraints) {
			if (!constraint.validateAfterEstimation(candidate)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void acceptCandidate(TripCandidate candidate) {
		for (TripConstraint constraint : constraints) {
			constraint.acceptCandidate(candidate);
		}
	}
}
