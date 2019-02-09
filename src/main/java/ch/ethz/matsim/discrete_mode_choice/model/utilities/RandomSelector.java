package ch.ethz.matsim.discrete_mode_choice.model.utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * The RandomSelector does what the name says: It collects a set of candidates
 * and then selects one randomly.
 * 
 * @author sebhoerl
 */
public class RandomSelector<T extends UtilityCandidate> implements UtilitySelector<T> {
	private final List<T> candidates = new LinkedList<>();

	@Override
	public void addCandidate(T candidate) {
		candidates.add(candidate);
	}

	@Override
	public Optional<T> select(Random random) {
		if (candidates.size() == 0) {
			return Optional.empty();
		}

		return Optional.of(candidates.get(random.nextInt(candidates.size())));
	}

	static public class Factory<T extends UtilityCandidate> implements UtilitySelectorFactory<T> {
		@Override
		public UtilitySelector<T> createUtilitySelector() {
			return new RandomSelector<>();
		}
	}
}
