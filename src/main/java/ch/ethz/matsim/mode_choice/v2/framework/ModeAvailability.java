package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.Collection;
import java.util.List;

public interface ModeAvailability {
	Collection<String> getAvailableModes(List<ModeChoiceTrip> trips);
}
