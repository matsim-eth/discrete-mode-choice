package ch.ethz.matsim.mode_choice.mnl;

public class BasicPublicTransitModeChoiceParameters {
	final private double betaTravelTime;
	final private double betaDistance;

	final private double betaTransferTravelTime;
	final private double betaTransferDistance;

	final private double betaWaitingTime;
	final private double betaNumberOfLineSwitches;

	final private double constant;

	public BasicPublicTransitModeChoiceParameters(double betaTravelTime, double betaDistance,
			double betaTransferTravelTime, double betaTransferDistance, double betaWaitingTime,
			double betaNumberOfLineSwitches, double constant) {
		this.betaTravelTime = betaTravelTime;
		this.betaDistance = betaDistance;
		this.betaTransferTravelTime = betaTransferTravelTime;
		this.betaTransferDistance = betaTransferDistance;
		this.betaWaitingTime = betaWaitingTime;
		this.betaNumberOfLineSwitches = betaNumberOfLineSwitches;
		this.constant = constant;
	}

	public double getBetaTravelTime() {
		return betaTravelTime;
	}

	public double getBetaDistance() {
		return betaDistance;
	}

	public double getBetaTransferTravelTime() {
		return betaTransferTravelTime;
	}

	public double getBetaTransferDistance() {
		return betaTransferDistance;
	}

	public double getBetaWaitingTime() {
		return betaWaitingTime;
	}

	public double getBetaNumberOfLineSwitches() {
		return betaNumberOfLineSwitches;
	}

	public double getConstant() {
		return constant;
	}
}
