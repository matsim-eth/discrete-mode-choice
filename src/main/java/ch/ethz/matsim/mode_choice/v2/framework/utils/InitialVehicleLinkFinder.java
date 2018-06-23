package ch.ethz.matsim.mode_choice.v2.framework.utils;

import java.util.List;
import java.util.Optional;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface InitialVehicleLinkFinder {
	Optional<Id<Link>> findInitialLinkId(String mode, List<ModeChoiceTrip> trips);
}
