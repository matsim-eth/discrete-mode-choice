package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;

import org.matsim.api.core.v01.population.PlanElement;

import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultRoutedTripCandidate;

/**
 * Special trip candidate for the MATSimTripEstimator. If the MATSimDayEstimator
 * is used, this class here moves information on the total trip travel times to
 * the daily estimation.
 * 
 * @author sebhoerl
 */
public class MATSimTripCandidate extends DefaultRoutedTripCandidate {
	private final double travelTime;

	public MATSimTripCandidate(double utility, String mode, List<? extends PlanElement> routedPlanElements,
			double travelTime) {
		super(utility, mode, routedPlanElements);
		this.travelTime = travelTime;
	}

	public double getTravelTime() {
		return travelTime;
	}
}
