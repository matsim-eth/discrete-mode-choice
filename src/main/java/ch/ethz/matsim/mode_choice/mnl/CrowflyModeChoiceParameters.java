package ch.ethz.matsim.mode_choice.mnl;

public class CrowflyModeChoiceParameters {
	final private double betaDistance;
	final private double betaTravelTime;
	final private double constant;
	
	final private double speed;
	
	public CrowflyModeChoiceParameters(double speed, double constant, double betaDistance, double betaTravelTime) {
		this.betaDistance = betaDistance;
		this.betaTravelTime = betaTravelTime;
		this.constant = constant;
		this.speed = speed;
	}

	public double getBetaDistance() {
		return betaDistance;
	}

	public double getBetaTravelTime() {
		return betaTravelTime;
	}

	public double getConstant() {
		return constant;
	}

	public double getSpeed() {
		return speed;
	}
}
