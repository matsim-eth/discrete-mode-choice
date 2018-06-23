package ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public interface PlanEstimator {
	public PlanCandidate estimatePlan(List<String> mode, List<ModeChoiceTrip> trips);
}
