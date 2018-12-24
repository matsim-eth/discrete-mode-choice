package ch.ethz.matsim.mode_choice.framework.utilities;

import java.util.Optional;
import java.util.Random;

public interface UtilitySelector<T extends UtilityCandidate> {
	void addCandidate(T candidate);

	Optional<T> select(Random random);
}
