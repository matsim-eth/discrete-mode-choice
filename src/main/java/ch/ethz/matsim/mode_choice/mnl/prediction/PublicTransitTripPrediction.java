package ch.ethz.matsim.mode_choice.mnl.prediction;

public class PublicTransitTripPrediction extends DefaultTripPrediction {
	final private int numberOfLineSwitches;
	final private boolean isOnlyTransitWalk;
	
	final private double transferTime;
	final private double transferDistance;
	
	final private double waitingTime;
	
	public PublicTransitTripPrediction(double travelTime, double travelDistance, double transferTime, double transferDistance, double waitingTime, int numberOfLinSwitches, boolean isOnlyTransitWalk) {
		super(travelTime, travelDistance);
		
		this.numberOfLineSwitches = numberOfLinSwitches;
		this.isOnlyTransitWalk = isOnlyTransitWalk;
		this.transferTime = transferTime;
		this.transferDistance = transferDistance;
		this.waitingTime = waitingTime;
	}

	public int getNumberOfLineSwitches() {
		return numberOfLineSwitches;
	}
	
	public boolean isOnlyTransitWalk() {
		return isOnlyTransitWalk;
	}

	public double getTransferTime() {
		return transferTime;
	}

	public double getTransferDistance() {
		return transferDistance;
	}

	public double getWaitingTime() {
		return waitingTime;
	}
}
