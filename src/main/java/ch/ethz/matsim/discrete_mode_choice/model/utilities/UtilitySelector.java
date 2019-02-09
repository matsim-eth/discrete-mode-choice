package ch.ethz.matsim.discrete_mode_choice.model.utilities;

import java.util.Optional;
import java.util.Random;

/**
 * A UtilitySelector collects a set of candidates for a specific choice (using
 * addCanddiate) and finally selects one of them according to a predefined
 * process.
 * 
 * @author sebhoerl
 */
public interface UtilitySelector<T extends UtilityCandidate> {
	/**
	 * Add another candidate to the choice set
	 */
	void addCandidate(T candidate);

	/**
	 * Select one candidate from the choice set (or indicate that none can be
	 * chosen).
	 */
	Optional<T> select(Random random);
}
