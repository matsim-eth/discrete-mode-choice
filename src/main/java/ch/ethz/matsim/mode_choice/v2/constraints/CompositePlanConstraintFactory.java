package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraintFactory;

public class CompositePlanConstraintFactory implements PlanConstraintFactory {
	final private List<PlanConstraintFactory> factories;

	public CompositePlanConstraintFactory(List<PlanConstraintFactory> factories) {
		this.factories = factories;
	}

	@Override
	public PlanConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
		return new CompositePlanConstraint(
				factories.stream().map(f -> f.createConstraint(trips, availableModes)).collect(Collectors.toList()));
	}
}
