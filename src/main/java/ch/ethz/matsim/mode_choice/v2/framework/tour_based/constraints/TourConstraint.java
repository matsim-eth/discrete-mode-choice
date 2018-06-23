package ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;

public interface TourConstraint {
	boolean validateBeforeEstimation(List<String> modes);

	boolean validateAfterEstimation(TourCandidate candidates);

	void acceptTour(TourCandidate candidate);
}
