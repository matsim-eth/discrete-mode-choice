package ch.ethz.matsim.mode_choice;

import org.matsim.api.core.v01.network.Link;

public interface ModeChoiceTrip {
	Link getOriginLink();
	Link getDestinationLink();
}
