package ch.ethz.matsim.mode_choice.prediction;

import org.matsim.core.router.TripStructureUtils.Trip;
import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;

public class TeleportationPredictor {
	final private double crowflyDistanceFactor;
	final private double speed;

	public TeleportationPredictor(double crowflyDistanceFactor, double speed) {
		this.crowflyDistanceFactor = crowflyDistanceFactor;
		this.speed = speed;
	}

	public TeleportationPrediction predict(ModeChoiceTrip trip) {
		Trip tripInformation = trip.getTripInformation();

		double distance = CoordUtils.calcEuclideanDistance(tripInformation.getOriginActivity().getCoord(),
				tripInformation.getDestinationActivity().getCoord());
		distance *= crowflyDistanceFactor;

		double travelTime = distance / speed;

		return new TeleportationPrediction(travelTime, distance);
	}
}
