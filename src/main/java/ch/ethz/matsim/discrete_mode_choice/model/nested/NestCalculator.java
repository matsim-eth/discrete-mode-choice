package ch.ethz.matsim.discrete_mode_choice.model.nested;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilityCandidate;

public class NestCalculator {
	private final Map<Nest, Collection<UtilityCandidate>> candidates = new HashMap<>();
	private final Map<Nest, Nest> parents = new HashMap<>();

	private final Map<Nest, Double> expectedUtilityCache = new HashMap<>();
	private final Nest rootNest;

	public NestCalculator(Nest rootNest, Collection<Nest> nests) {
		this.rootNest = rootNest;

		for (Nest nest : nests) {
			for (Nest child : nest.getChildren()) {
				parents.put(child, nest);
			}

			candidates.put(nest, new LinkedList<>());
		}
	}

	public void addCandidate(NestedUtilityCandidate candidate) {
		candidates.get(candidate.getNest()).add(candidate);
	}

	public double calculateProbability(NestedUtilityCandidate candidate) {
		double nominator = Math.exp(candidate.getUtility() / candidate.getNest().getScaleParameter());
		return calculateProbability(nominator, candidate.getNest());
	}

	public double calculateProbability(double nominator, Nest nest) {
		double denominator = 0.0;

		for (UtilityCandidate alternative : candidates.get(nest)) {
			denominator += Math.exp(alternative.getUtility() / nest.getScaleParameter());
		}

		for (Nest child : nest.getChildren()) {
			denominator += Math.exp(calculateExpectedUtility(child) / nest.getScaleParameter());
		}

		double probability = nominator / denominator;

		if (nest == rootNest) {
			return probability;
		} else {
			double nestNominator = Math.exp(nest.getScaleParameter() * calculateExpectedUtility(nest));
			return probability * calculateProbability(nestNominator, parents.get(nest));
		}
	}

	public double calculateExpectedUtility(Nest nest) {
		if (expectedUtilityCache.containsKey(nest)) {
			return expectedUtilityCache.get(nest);
		}

		Collection<UtilityCandidate> nestCandidates = candidates.getOrDefault(nest, Collections.emptySet());

		double expectedUtility = 0.0;

		for (UtilityCandidate candidate : nestCandidates) {
			expectedUtility += Math.exp(candidate.getUtility() / nest.getScaleParameter());
		}

		for (Nest child : nest.getChildren()) {
			expectedUtility += Math.exp(child.getScaleParameter() * calculateExpectedUtility(child));
		}

		expectedUtility = Math.log(expectedUtility);
		expectedUtilityCache.put(nest, expectedUtility);

		return expectedUtility;
	}
}
