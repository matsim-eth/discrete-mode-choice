package ch.ethz.matsim.discrete_mode_choice.components.tour_finder;

import java.util.List;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

/**
 * This interface segments a chain of trips into tours.
 * 
 * @sebhoerl
 */
public interface TourFinder {
	List<List<DiscreteModeChoiceTrip>> findTours(List<DiscreteModeChoiceTrip> trips);
}
