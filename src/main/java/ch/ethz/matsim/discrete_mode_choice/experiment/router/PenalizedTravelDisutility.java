package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import java.util.Collection;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.vehicles.Vehicle;

public class PenalizedTravelDisutility implements TravelDisutility {
	private final TravelDisutility delegate;
	private final double penalty;

	private Collection<Id<Link>> penalizedLinkIds;

	public PenalizedTravelDisutility(TravelDisutility delegate, double penalty) {
		this.delegate = delegate;
		this.penalty = penalty;
	}

	public void setPenalizedLinkIds(Collection<Id<Link>> penalizedLinkIds) {
		this.penalizedLinkIds = penalizedLinkIds;
	}

	@Override
	public double getLinkTravelDisutility(Link link, double time, Person person, Vehicle vehicle) {
		double returnValue = delegate.getLinkTravelDisutility(link, time, person, vehicle);

		if (penalizedLinkIds.contains(link.getId())) {
			returnValue += penalty;
		}

		return returnValue;
	}

	/**
	 * With this function we can take a Path object from the router and recover the
	 * "actual" disutility of the path. This means we have to calculate back those
	 * links for which we have artificially added a penalty.
	 */
	public double unpenalizeDisutility(Path path) {
		double unpenalizedDisutility = path.travelCost;

		for (Link link : path.links) {
			if (penalizedLinkIds.contains(link.getId())) {
				unpenalizedDisutility -= penalty;
			}
		}

		return unpenalizedDisutility;
	}

	@Override
	public double getLinkMinimumTravelDisutility(Link link) {
		return delegate.getLinkMinimumTravelDisutility(link);
	}
}
