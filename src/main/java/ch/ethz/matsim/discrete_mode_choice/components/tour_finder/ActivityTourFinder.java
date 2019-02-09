package ch.ethz.matsim.discrete_mode_choice.components.tour_finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

/**
 * This TourFinder creates tours between activities of a certain type. For
 * instance, all trips between two home activities are considered as one trip.
 * 
 * @author sebhoerl
 */
public class ActivityTourFinder implements TourFinder {
	private final String activityType;

	/**
	 * Defines which activity type is used to establish tours.
	 */
	public ActivityTourFinder(String activityType) {
		this.activityType = activityType;
	}

	@Override
	public List<List<DiscreteModeChoiceTrip>> findTours(List<DiscreteModeChoiceTrip> trips) {
		Set<Integer> relevantActivityIndices = new HashSet<>();

		for (int index = 0; index < trips.size(); index++) {
			DiscreteModeChoiceTrip trip = trips.get(index);

			if (trip.getOriginActivity().getType().equals(activityType)) {
				relevantActivityIndices.add(index);
			}

			if (trip.getDestinationActivity().getType().equals(activityType)) {
				relevantActivityIndices.add(index + 1);
			}
		}

		List<Integer> orderedActivityIndices = new ArrayList<>(relevantActivityIndices);
		Collections.sort(orderedActivityIndices);

		List<List<DiscreteModeChoiceTrip>> tours = new LinkedList<>();

		int currentActivityIndex = 0;

		while (orderedActivityIndices.size() > 0) {
			int nextActivityIndex = orderedActivityIndices.remove(0);

			tours.add(trips.subList(currentActivityIndex, nextActivityIndex));
			currentActivityIndex = nextActivityIndex;
		}

		if (currentActivityIndex != trips.size()) {
			tours.add(trips.subList(currentActivityIndex, trips.size()));
		}

		tours = tours.stream().filter(t -> t.size() > 0).collect(Collectors.toList());

		return tours;
	}
}
