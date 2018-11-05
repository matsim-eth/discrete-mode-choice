package ch.ethz.matsim.mode_choice.framework.tour_based;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public interface TourFinder {
	List<List<ModeChoiceTrip>> findTours(List<ModeChoiceTrip> trips);
}
