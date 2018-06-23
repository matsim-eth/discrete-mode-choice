package ch.ethz.matsim.mode_choice.v2.mode_availability;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public class DefaultModeAvailability implements ModeAvailability {
	final private Collection<String> modes;

	public DefaultModeAvailability(Collection<String> modes) {
		this.modes = modes;
	}

	@Override
	public Collection<String> getAvailableModes(List<ModeChoiceTrip> trips) {
		return modes;
	}
}
