package ch.ethz.matsim.mode_choice.framework.utilities;

import java.util.Optional;
import java.util.Random;

public class MaximumUtilitySelector<T extends UtilityCandidate> implements UtilitySelector<T> {
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
	public int getNumberOfCandidates() {
		return bestCandidate == null ? 0 : 1;
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
			return new MaximumUtilitySelector<>();
		}
	}
}
