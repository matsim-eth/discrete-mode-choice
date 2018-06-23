package ch.ethz.matsim.mode_choice.v2.framework.utilities;

import java.util.Random;

public interface UtilitySelector<T extends UtilityCandidate> {
	void addCandidate(T candidate);

	T select(Random random);
}
