package ch.ethz.matsim.mode_choice.v2.prediction;

public class TeleportationPrediction {
	final public double travelTime;
	final public double distance;
	
	public TeleportationPrediction(double travelTime, double distance) {
		this.travelTime = travelTime;
		this.distance = distance;
	}
}
