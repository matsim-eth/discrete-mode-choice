package ch.ethz.matsim.mode_choice.framework.tour_based.constraints;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public interface TourConstraintFactory {
	TourConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes);
}
