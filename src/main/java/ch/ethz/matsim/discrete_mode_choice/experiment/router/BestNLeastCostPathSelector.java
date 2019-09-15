package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import java.util.Collection;

import org.matsim.core.router.util.LeastCostPathCalculator.Path;

public interface BestNLeastCostPathSelector {
	Path selectPath(Collection<Path> alternatives);
}
