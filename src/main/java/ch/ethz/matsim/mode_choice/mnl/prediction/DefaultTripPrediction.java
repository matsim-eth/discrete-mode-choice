package ch.ethz.matsim.mode_choice.mnl.prediction;

public class DefaultTripPrediction implements TripPrediction {
	final private double travelTime;
	final private double travelDistance;
	
	public DefaultTripPrediction(double travelTime, double travelDistance) {
		this.travelDistance = travelDistance;
		this.travelTime = travelTime;
	}
	
	@Override
	public double getPredictedTravelTime() {
		return travelTime;
	}
	@Override
	public double getPredictedTravelDistance() {
		return travelDistance;
	}
}
