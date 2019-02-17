package ch.ethz.matsim.discrete_mode_choice.components.utils;

import org.matsim.pt.routes.ExperimentalTransitRoute;

/**
 * Waiting time estimator which is used for the MATSim utility function
 * estimator if no TransitSchedule is available.
 * 
 * @author sebhoerl
 */
public class NullWaitingTimeEstimator implements PTWaitingTimeEstimator {
	@Override
	public double estimateWaitingTime(double agentDepartureTime, ExperimentalTransitRoute route) {
		return 0.0;
	}
}
