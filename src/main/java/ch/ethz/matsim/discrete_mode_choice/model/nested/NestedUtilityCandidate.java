package ch.ethz.matsim.discrete_mode_choice.model.nested;

import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilityCandidate;

public interface NestedUtilityCandidate extends UtilityCandidate {
	Nest getNest();
}
