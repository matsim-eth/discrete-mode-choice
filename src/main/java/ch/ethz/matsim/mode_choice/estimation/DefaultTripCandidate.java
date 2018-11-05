package ch.ethz.matsim.mode_choice.estimation;

import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public class DefaultTripCandidate implements TripCandidate {
	final private double utility;
	final private String mode;
	private boolean isFallback;

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

	@Override
	public boolean isFallback() {
		return isFallback;
	}

	@Override
	public void setFallback(boolean isFallback) {
		this.isFallback = isFallback;
	}
}
