package ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilityCandidate;

public interface PlanCandidate extends UtilityCandidate {
	List<TripCandidate> getTripCandidates();
}
