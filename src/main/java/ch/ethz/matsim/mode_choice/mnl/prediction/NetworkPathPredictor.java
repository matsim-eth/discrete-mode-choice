package ch.ethz.matsim.mode_choice.mnl.prediction;

import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class NetworkPathPredictor implements TripPredictor {
	final private LeastCostPathCalculator router;

	public NetworkPathPredictor(LeastCostPathCalculator router) {
		this.router = router;
	}

	@Override
	public TripPrediction predictTrip(ModeChoiceTrip trip) {
		Path path = router.calcLeastCostPath(trip.getOriginLink().getToNode(), trip.getDestinationLink().getFromNode(),
				trip.getDepartureTime(), trip.getPerson(), null);

		if (path == null) {
			throw new IllegalStateException();
		}

		double travelDistance = path.links.stream().mapToDouble(l -> l.getLength()).sum();
		return new DefaultTripPrediction(path.travelTime, travelDistance);
	}
}
