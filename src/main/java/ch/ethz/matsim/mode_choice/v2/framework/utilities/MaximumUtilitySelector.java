package ch.ethz.matsim.mode_choice.v2.framework.utilities;

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
	public T select(Random random) {
		if (bestCandidate == null) {
			throw new IllegalStateException("No feasible candidate found for trip");
		}

		return bestCandidate;
	}

	public static class Factory<TF extends UtilityCandidate> implements UtilitySelectorFactory<TF> {
		@Override
		public UtilitySelector<TF> createUtilitySelector() {
			return new MaximumUtilitySelector<>();
		}
	}
}
