package ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates;

/**
 * Default implementation for a TripCandidate.
 * 
 * @author sebhoerl
 */
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
