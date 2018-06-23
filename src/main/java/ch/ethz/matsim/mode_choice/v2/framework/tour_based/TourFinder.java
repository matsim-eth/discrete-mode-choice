package ch.ethz.matsim.mode_choice.v2.framework.tour_based;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface TourFinder {
	List<List<ModeChoiceTrip>> findTours(List<ModeChoiceTrip> trips);
}
