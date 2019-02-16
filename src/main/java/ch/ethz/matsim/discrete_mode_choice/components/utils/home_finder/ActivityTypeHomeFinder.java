package ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder;

import java.util.List;

import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class ActivityTypeHomeFinder implements HomeFinder {
	private final String activityType;

	public ActivityTypeHomeFinder(String activityType) {
		this.activityType = activityType;
	}

	@Override
	public Id<? extends BasicLocation> getHomeLocationId(List<DiscreteModeChoiceTrip> trips) {
		for (DiscreteModeChoiceTrip trip : trips) {
			if (trip.getOriginActivity().getType().equals(activityType)) {
				return trip.getOriginActivity().getLinkId();
			}

			if (trip.getDestinationActivity().getType().equals(activityType)) {
				return trip.getDestinationActivity().getLinkId();
			}
		}

		return null;
	}
}
