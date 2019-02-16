package ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder;

import java.util.List;

import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public interface HomeFinder {
	Id<? extends BasicLocation> getHomeLocationId(List<DiscreteModeChoiceTrip> trips);
}
