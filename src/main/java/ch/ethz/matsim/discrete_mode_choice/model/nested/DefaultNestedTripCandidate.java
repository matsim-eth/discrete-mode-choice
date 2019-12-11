package ch.ethz.matsim.discrete_mode_choice.model.nested;

import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultTripCandidate;

public class DefaultNestedTripCandidate extends DefaultTripCandidate implements NestedUtilityCandidate {
	private final Nest nest;

	public DefaultNestedTripCandidate(double utility, String mode, Nest nest) {
		super(utility, mode);
		this.nest = nest;
	}

	@Override
	public Nest getNest() {
		return nest;
	}
}
