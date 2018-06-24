package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class DefaultPlanCandidate implements PlanCandidate {
	final private double utility;
	final private List<TripCandidate> tripCandidates;

	public DefaultPlanCandidate(double utility, List<TripCandidate> tripCandidates) {
		this.utility = utility;
		this.tripCandidates = tripCandidates;
	}

	@Override
	public double getUtility() {
		return utility;
	}
	
	@Override
	public List<TripCandidate> getTripCandidates() {
		return tripCandidates;
	}
}
