package ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface TourEstimator {
	TourCandidate estimateTour(List<String> mode, List<ModeChoiceTrip> trips, List<TourCandidate> preceedingTours);
}
