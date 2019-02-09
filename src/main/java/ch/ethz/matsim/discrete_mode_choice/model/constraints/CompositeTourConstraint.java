package ch.ethz.matsim.discrete_mode_choice.model.constraints;

import java.util.List;

import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraint;

/**
 * A TourConstraint that makes it easy to combine different constraints.
 * 
 * Validation happens as a AND operation, i.e. a candidate is only considered
 * feasible if all child constraints find it feasible.
 * 
 * @author sebhoerl
 */
public class CompositeTourConstraint implements TourConstraint {
	final private List<TourConstraint> constraints;

	CompositeTourConstraint(List<TourConstraint> constraints) {
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
