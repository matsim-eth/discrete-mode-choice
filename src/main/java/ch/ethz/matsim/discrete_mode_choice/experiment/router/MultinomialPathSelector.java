package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.matsim.core.router.util.LeastCostPathCalculator.Path;

public class MultinomialPathSelector implements BestNLeastCostPathSelector {
	private final Random random = new Random(0);
	private final double scalingFactor;

	public MultinomialPathSelector(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	@Override
	public Path selectPath(Collection<Path> alternatives) {
		List<Path> alternativesList = new ArrayList<>(alternatives);
		List<Double> exponentials = new ArrayList<>(alternatives.size());

		for (Path path : alternativesList) {
			exponentials.add(Math.exp(-path.travelCost * scalingFactor));
		}

		List<Double> cdf = new ArrayList<>(alternatives.size());
		cdf.add(exponentials.get(0));

		for (int i = 1; i < alternatives.size(); i++) {
			cdf.add(cdf.get(i - 1) + exponentials.get(1));
		}

		double r = cdf.get(cdf.size() - 1) * random.nextDouble();

		int index = 0;
		while (r > cdf.get(index)) {
			index++;
		}

		return alternativesList.get(index);
	}

}
