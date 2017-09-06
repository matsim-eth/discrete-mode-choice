package ch.ethz.matsim.mode_choice.mnl.prediction;

import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class CrowflyDistancePredictor implements DistancePredictor {
	@Override
	public double predictDistance(ModeChoiceTrip trip) {
		return CoordUtils.calcEuclideanDistance(trip.getOriginLink().getCoord(), trip.getDestinationLink().getCoord());
	}
}
