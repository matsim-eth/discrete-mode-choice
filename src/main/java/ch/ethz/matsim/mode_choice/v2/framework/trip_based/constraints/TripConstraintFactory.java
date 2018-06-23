package ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface TripConstraintFactory {
	TripConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes);
}
