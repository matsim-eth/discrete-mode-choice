package ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface PlanConstraintFactory {
	PlanConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes);
}
