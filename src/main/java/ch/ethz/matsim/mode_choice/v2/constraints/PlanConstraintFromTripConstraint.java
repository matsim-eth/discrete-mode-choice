package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class PlanConstraintFromTripConstraint implements PlanConstraint {
	final private TripConstraintFactory factory;
	final private List<ModeChoiceTrip> trips;
	final private Collection<String> availableModes;

	public PlanConstraintFromTripConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes,
			TripConstraintFactory factory) {
		this.factory = factory;
		this.trips = trips;
		this.availableModes = availableModes;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> modes) {
		TripConstraint constraint = factory.createConstraint(trips, availableModes);

		for (int i = 0; i < modes.size(); i++) {
			if (!constraint.validateBeforeEstimation(modes.get(i), modes.subList(0, i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(PlanCandidate planCandidate) {
		TripConstraint constraint = factory.createConstraint(trips, availableModes);
		List<TripCandidate> tripCandidates = planCandidate.getTripCandidates();

		for (int i = 0; i < tripCandidates.size(); i++) {
			if (!constraint.validateAfterEstimation(tripCandidates.get(i), tripCandidates.subList(0, i))) {
				return false;
			}
		}

		return true;
	}

	static public class Factory implements PlanConstraintFactory {
		final private TripConstraintFactory factory;

		public Factory(TripConstraintFactory factory) {
			this.factory = factory;
		}

		@Override
		public PlanConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
			return new PlanConstraintFromTripConstraint(trips, availableModes, factory);
		}
	}
}
