package ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder;

import java.util.List;

import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;

import ch.ethz.matsim.discrete_mode_choice.components.utils.LocationUtils;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class FirstActivityHomeFinder implements HomeFinder {
	@Override
	public Id<? extends BasicLocation> getHomeLocationId(List<DiscreteModeChoiceTrip> trips) {
		if (trips.size() > 0) {
			return LocationUtils.getLocationId(trips.get(0).getOriginActivity());
		} else {
			return null;
		}
	}
}
