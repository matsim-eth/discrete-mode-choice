package ch.ethz.matsim.mode_choice.v2.framework.utilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MultinomialSelector<T extends UtilityCandidate> implements UtilitySelector<T> {
	final private List<T> candidates = new LinkedList<>();

	@Override
	public void addCandidate(T candidate) {
		candidates.add(candidate);
	}

	@Override
	public T select(Random random) {
		if (candidates.size() == 0) {
			throw new IllegalStateException("No feasible candidate found for trip");
		}

		List<Double> density = candidates.stream() //
				.map(UtilityCandidate::getUtility) //
				.map(u -> Math.max(u, -700.0)) //
				.map(u -> Math.min(u, 700.0)) //
				.map(Math::exp) //
				.collect(Collectors.toList());

		double cumulativeDensity = density.stream().reduce(0.0, Double::sum);
		double pointer = random.nextDouble() * cumulativeDensity;

		int selection = (int) density.stream().filter(f -> f < pointer).count();
		return candidates.get(selection);
	}
	
	public static class Factory<TF extends UtilityCandidate> implements UtilitySelectorFactory<TF> {
		@Override
		public UtilitySelector<TF> createUtilitySelector() {
			return new MultinomialSelector<>();
		}
	}
}
