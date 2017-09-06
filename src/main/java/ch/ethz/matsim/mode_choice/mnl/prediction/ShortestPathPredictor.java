package ch.ethz.matsim.mode_choice.mnl.prediction;

import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class ShortestPathPredictor implements DistancePredictor {
	final private LeastCostPathCalculator router;
	
	public ShortestPathPredictor(LeastCostPathCalculator router) {
		this.router = router;
	}
	
	@Override
	public double predictDistance(ModeChoiceTrip trip) {
		Path path = router.calcLeastCostPath(trip.getOriginLink().getToNode(), trip.getDestinationLink().getFromNode(), trip.getDepartureTime(), trip.getPerson(), null);
		return path.links.stream().mapToDouble(l -> l.getLength()).sum();
	}
}
