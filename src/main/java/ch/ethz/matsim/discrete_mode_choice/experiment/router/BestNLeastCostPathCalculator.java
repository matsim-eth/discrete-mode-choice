package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.vehicles.Vehicle;

public class BestNLeastCostPathCalculator implements LeastCostPathCalculator {
	private final LeastCostPathCalculator delegate;
	private final PenalizedTravelDisutility disutility;
	private final BestNLeastCostPathSelector selector;
	private final double maximumDelay;
	private final int maximumNumberOfAlternatives;

	public BestNLeastCostPathCalculator(LeastCostPathCalculator delegate, PenalizedTravelDisutility disutility,
			BestNLeastCostPathSelector selector, double maximumDelay, int maximumNumberOfAlternatives) {
		this.delegate = delegate;
		this.disutility = disutility;
		this.maximumDelay = maximumDelay;
		this.maximumNumberOfAlternatives = maximumNumberOfAlternatives;
		this.selector = selector;
	}

	@Override
	public Path calcLeastCostPath(Node fromNode, Node toNode, double starttime, Person person, Vehicle vehicle) {
		// Store all path alternatives
		Collection<Path> alternatives = new LinkedList<>();

		// Store links that should be penalized as they have been visited
		Set<Id<Link>> penalizedLinkIds = new HashSet<>();
		disutility.setPenalizedLinkIds(penalizedLinkIds);

		// First, route the best path
		Path bestPath = delegate.calcLeastCostPath(fromNode, toNode, starttime, person, vehicle);
		alternatives.add(bestPath);

		double bestTravelTime = bestPath.travelTime;

		// Make sure links from the first path are penalized in any subsequent routing
		bestPath.links.forEach(l -> penalizedLinkIds.add(l.getId()));

		while (alternatives.size() < maximumNumberOfAlternatives) {
			// Calculate alternative path
			Path alternativePath = delegate.calcLeastCostPath(fromNode, toNode, starttime, person, vehicle);
			double alternativeTravelTime = alternativePath.travelTime;

			// Check whether we're still within the delay limit
			if (alternativeTravelTime - bestTravelTime < maximumDelay) {
				// Calculate back the correct travel disutility
				double unpenalizedDisutility = disutility.unpenalizeDisutility(alternativePath);

				// Update the path object with unpenalized utility
				alternativePath = new Path(alternativePath.nodes, alternativePath.links, alternativePath.travelTime,
						unpenalizedDisutility);

				// Make sure any new links are penalized in the next round
				alternativePath.links.forEach(l -> penalizedLinkIds.add(l.getId()));

				// Add as alternative and then continue with next round
				alternatives.add(alternativePath);
			} else {
				// We're not, so exit the loop!
				break;
			}
		}

		return selector.selectPath(alternatives);
	}
}
