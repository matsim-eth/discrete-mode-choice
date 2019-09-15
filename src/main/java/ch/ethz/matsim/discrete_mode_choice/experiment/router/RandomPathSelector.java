package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.matsim.core.router.util.LeastCostPathCalculator.Path;

public class RandomPathSelector implements BestNLeastCostPathSelector {
	private final Random random = new Random(0);

	@Override
	public Path selectPath(Collection<Path> alternatives) {
		List<Path> alternativeList = new ArrayList<>(alternatives);
		int numberOfAlternatives = alternatives.size();

		return alternativeList.get(random.nextInt(numberOfAlternatives));
	}
}
