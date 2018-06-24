package ch.ethz.matsim.mode_choice.v2.estimation;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class DefaultTripCandidate implements TripCandidate {
	final private double utility;
	final private String mode;

	public DefaultTripCandidate(double utility, String mode) {
		this.utility = utility;
		this.mode = mode;
	}

	@Override
	public double getUtility() {
		return utility;
	}

	@Override
	public String getMode() {
		return mode;
	}
}
