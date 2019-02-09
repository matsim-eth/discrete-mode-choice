package ch.ethz.matsim.discrete_mode_choice.model.utilities;

import java.util.Optional;
import java.util.Random;

/**
 * The maximum utility selector collects a set of candidates with a given
 * utility value and then selects the one with the highest utility. Internally,
 * always only the best candidate is held.
 * 
 * @author sebhoerl
 */
public class MaximumSelector<T extends UtilityCandidate> implements UtilitySelector<T> {
	private T bestCandidate = null;

	@Override
	public void addCandidate(T candidate) {
		if (bestCandidate == null) {
			bestCandidate = candidate;
		} else if (candidate.getUtility() > bestCandidate.getUtility()) {
			bestCandidate = candidate;
		}
	}

	@Override
	public Optional<T> select(Random random) {
		if (bestCandidate == null) {
			return Optional.empty();
		}

		return Optional.of(bestCandidate);
	}

	public static class Factory<TF extends UtilityCandidate> implements UtilitySelectorFactory<TF> {
		@Override
		public UtilitySelector<TF> createUtilitySelector() {
			return new MaximumSelector<>();
		}
	}
}
