package ch.ethz.matsim.mode_choice.framework.tour_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public interface TourEstimator {
	TourCandidate estimateTour(List<String> mode, List<ModeChoiceTrip> trips, List<TourCandidate> preceedingTours);
}
