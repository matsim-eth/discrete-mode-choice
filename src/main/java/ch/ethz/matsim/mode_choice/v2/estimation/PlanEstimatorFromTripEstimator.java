package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripEstimator;

public class PlanEstimatorFromTripEstimator implements PlanEstimator {
	final private TripEstimator delegate;

	public PlanEstimatorFromTripEstimator(TripEstimator delegate) {
		this.delegate = delegate;
	}

	@Override
	public PlanCandidate estimatePlan(List<String> modes, List<ModeChoiceTrip> trips) {
		List<TripCandidate> tripCandidates = new LinkedList<>();
		double utility = 0.0;

		for (int i = 0; i < modes.size(); i++) {
			String mode = modes.get(i);
			ModeChoiceTrip trip = trips.get(i);

			TripCandidate tripCandidate = delegate.estimateTrip(mode, trip, tripCandidates);
			utility += tripCandidate.getUtility();

			tripCandidates.add(tripCandidate);
		}

		return new DefaultPlanCandidate(utility, tripCandidates);
	}
}
