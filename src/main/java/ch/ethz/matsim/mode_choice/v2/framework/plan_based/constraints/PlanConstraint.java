package ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;

public interface PlanConstraint {
	boolean validateBeforeEstimation(List<String> modes);

	boolean validateAfterEstimation(PlanCandidate candidates);
}
