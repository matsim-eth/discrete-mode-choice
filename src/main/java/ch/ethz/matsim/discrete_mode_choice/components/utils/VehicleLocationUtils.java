package ch.ethz.matsim.discrete_mode_choice.components.utils;

import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.TripStructureUtils.Trip;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class VehicleLocationUtils {
	static public Id<Link> getOriginLinkId(Trip trip) {
		return trip.getOriginActivity().getLinkId();
	}

	static public Id<Link> getDestinationLinkId(Trip trip) {
		return trip.getDestinationActivity().getLinkId();
	}

	static public Id<Link> getHomeLinkId(List<DiscreteModeChoiceTrip> trips) {
		Id<Link> homeLinkId = null;

		for (DiscreteModeChoiceTrip trip : trips) {
			if (trip.getOriginActivity().getType().contains("home")) {
				homeLinkId = trip.getOriginActivity().getLinkId();
				break;
			}

			if (trip.getDestinationActivity().getType().contains("home")) {
				homeLinkId = trip.getDestinationActivity().getLinkId();
				break;
			}
		}

		return homeLinkId;
	}
}
