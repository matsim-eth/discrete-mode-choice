package ch.ethz.matsim.mode_choice.framework.trip_based.constraints;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public interface TripConstraintFactory {
	TripConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes);
}
