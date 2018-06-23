package ch.ethz.matsim.mode_choice.v2.framework.tour_based;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public class ActivityTourFinder implements TourFinder {
	final private String activityType;

	public ActivityTourFinder(String activityType) {
		this.activityType = activityType;
	}

	@Override
	public List<List<ModeChoiceTrip>> findTours(List<ModeChoiceTrip> trips) {
		List<List<ModeChoiceTrip>> tours = new LinkedList<>();
		List<ModeChoiceTrip> currentTour = new LinkedList<>();

		for (ModeChoiceTrip trip : trips) {
			currentTour.add(trip);

			if (trip.getTripInformation().getDestinationActivity().getType().equals(activityType)) {
				tours.add(currentTour);
				currentTour = new LinkedList<>();
			}
		}

		return tours;
	}
}
