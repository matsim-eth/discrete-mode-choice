package ch.ethz.matsim.discrete_mode_choice.components.utils;

import org.matsim.pt.routes.ExperimentalTransitRoute;

/**
 * @author sebhoerl
 */
public interface PTWaitingTimeEstimator {
	double estimateWaitingTime(double agentDepartureTime, ExperimentalTransitRoute route);
}
