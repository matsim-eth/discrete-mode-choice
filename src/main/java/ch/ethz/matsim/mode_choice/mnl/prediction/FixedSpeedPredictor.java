package ch.ethz.matsim.mode_choice.mnl.prediction;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class FixedSpeedPredictor implements TripPredictor {
	final private double speed;
	final private DistancePredictor distancePredictor;
	
	public FixedSpeedPredictor(double speed, DistancePredictor distancePredictor) {
		this.speed = speed;
		this.distancePredictor = distancePredictor;
	}
	
	@Override
	public TripPrediction predictTrip(ModeChoiceTrip trip) {
		double travelDistance = distancePredictor.predictDistance(trip);
		double travelTime = travelDistance / speed;
		
		return new DefaultTripPrediction(travelTime, travelDistance);
	}
}
