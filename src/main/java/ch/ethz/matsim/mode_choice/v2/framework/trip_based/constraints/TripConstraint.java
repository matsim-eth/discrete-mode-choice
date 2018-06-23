package ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public interface TripConstraint {
	boolean validateBeforeEstimation(String mode);

	boolean validateAfterEstimation(TripCandidate candidate);

	void acceptCandidate(TripCandidate candidate);
}
