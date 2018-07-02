package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;

public abstract class AbstractTourConstraint implements TourConstraint {
	@Override
	public boolean validateBeforeEstimation(List<String> modes, List<List<String>> previousModes) {
		return true;
	}

	@Override
	public boolean validateAfterEstimation(TourCandidate candidate, List<TourCandidate> previousCandidates) {
		return true;
	}
}
