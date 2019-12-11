package ch.ethz.matsim.discrete_mode_choice.model.nested;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilityCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelector;

public class NestedLogitSelector<T extends UtilityCandidate> implements UtilitySelector<T> {
	private final List<T> candidates = new LinkedList<>();
	private final List<NestedUtilityCandidate> nestedCandidates = new LinkedList<>();

	private final NestCalculator calculator;
	private final Nest rootNest;

	public NestedLogitSelector(Nest rootNest) {
		calculator = new NestCalculator(rootNest, nests);
	}

	@Override
	public void addCandidate(T candidate) {
		candidates.add(candidate);
	}

	@Override
	public Optional<T> select(Random random) {
		// I) If not candidates are available, give back nothing
		if (candidates.size() == 0) {
			return Optional.empty();
		}

		// II) Filter candidates that have a very low utility
		List<T> filteredCandidates = candidates;

		if (considerMinimumUtility) {
			filteredCandidates = candidates.stream() //
					.filter(c -> c.getUtility() > minimumUtility) //
					.collect(Collectors.toList());

			if (filteredCandidates.size() == 0) {
				logger.warn(String.format(
						"Encountered choice where all utilities were smaller than %f (minimum configured utility)",
						minimumUtility));
				return Optional.empty();
			}
		}

		// III) Build a nest calculator
		List<NestedUtilityCandidate> nestedCandidates = new LinkedList<>();

		for (T candidate : filteredCandidates) {
			if (!(candidate instanceof NestedUtilityCandidate)) {
				nestedCandidates.add((NestedUtilityCandidate) candidate);
				calculator.addCandidate((NestedUtilityCandidate) candidate);
			} else {
				WrappingNestedUtilityCandidate nestedCandidate = new WrappingNestedUtilityCandidate(candidate,
						rootNest);
				nestedCandidates.add(nestedCandidate);
				calculator.addCandidate(nestedCandidate);
			}
		}

		// IV) Create a probability distribution over candidates
		List<Double> density = new ArrayList<>(filteredCandidates.size());

		for (NestedUtilityCandidate candidate : nestedCandidates) {
			double probability = calculator.calculateProbability(candidate);
			density.add(probability);
		}

		// IV) Build a cumulative density of the distribution
		List<Double> cumulativeDensity = new ArrayList<>(density.size());
		double totalDensity = 0.0;

		for (int i = 0; i < density.size(); i++) {
			totalDensity += density.get(i);
			cumulativeDensity.add(totalDensity);
		}

		// V) Perform a selection using the CDF
		double pointer = random.nextDouble() * totalDensity;

		int selection = (int) cumulativeDensity.stream().filter(f -> f < pointer).count();
		return Optional.of(filteredCandidates.get(selection));
	}
}
