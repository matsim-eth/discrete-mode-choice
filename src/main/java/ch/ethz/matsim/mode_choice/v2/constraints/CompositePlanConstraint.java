package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;

public class CompositePlanConstraint implements PlanConstraint {
	final private List<PlanConstraint> constraints;

	public CompositePlanConstraint(List<PlanConstraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> modes) {
		for (PlanConstraint constraint : constraints) {
			if (!constraint.validateBeforeEstimation(modes)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(PlanCandidate candidates) {
		for (PlanConstraint constraint : constraints) {
			if (!constraint.validateAfterEstimation(candidates)) {
				return false;
			}
		}

		return true;
	}
}
