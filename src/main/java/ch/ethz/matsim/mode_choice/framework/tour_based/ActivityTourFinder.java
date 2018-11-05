package ch.ethz.matsim.mode_choice.framework.tour_based;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public class ActivityTourFinder implements TourFinder {
	final private String activityType;

	public ActivityTourFinder(String activityType) {
		this.activityType = activityType;
	}

	@Override
	public List<List<ModeChoiceTrip>> findTours(List<ModeChoiceTrip> trips) {
		Set<Integer> relevantActivityIndices = new HashSet<>();

		for (int index = 0; index < trips.size(); index++) {
			ModeChoiceTrip trip = trips.get(index);

			if (trip.getTripInformation().getOriginActivity().getType().equals(activityType)) {
				relevantActivityIndices.add(index);
			}

			if (trip.getTripInformation().getDestinationActivity().getType().equals(activityType)) {
				relevantActivityIndices.add(index + 1);
			}
		}

		List<Integer> orderedActivityIndices = new ArrayList<>(relevantActivityIndices);
		Collections.sort(orderedActivityIndices);

		List<List<ModeChoiceTrip>> tours = new LinkedList<>();

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
