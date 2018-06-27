package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;

public abstract class AbstractPlanConstraint implements PlanConstraint {
	@Override
	public boolean validateBeforeEstimation(List<String> modes) {
		return true;
	}

	@Override
	public boolean validateAfterEstimation(PlanCandidate candidates) {
		return true;
	}
}
