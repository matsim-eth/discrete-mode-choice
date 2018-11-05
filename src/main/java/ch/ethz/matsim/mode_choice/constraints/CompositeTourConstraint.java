package ch.ethz.matsim.mode_choice.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.tour_based.constraints.TourConstraint;
import ch.ethz.matsim.mode_choice.framework.tour_based.estimation.TourCandidate;

public class CompositeTourConstraint implements TourConstraint {
	final private List<TourConstraint> constraints;

	public CompositeTourConstraint(List<TourConstraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> modes, List<List<String>> previousModes) {
		for (TourConstraint constraint : constraints) {
			if (!constraint.validateBeforeEstimation(modes, previousModes)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(TourCandidate candidate, List<TourCandidate> previousCandidates) {
		for (TourConstraint constraint : constraints) {
			if (!constraint.validateAfterEstimation(candidate, previousCandidates)) {
				return false;
			}
		}

		return true;
	}
}
