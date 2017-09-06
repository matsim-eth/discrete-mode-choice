package ch.ethz.matsim.mode_choice.mnl;

public class BasicModeChoiceParameters {
	final private double betaDistance;
	final private double betaTravelTime;
	final private double constant;
	
	final private boolean isChainBased;
	
	public BasicModeChoiceParameters(double constant, double betaDistance, double betaTravelTime, boolean isChainBased) {
		this.betaDistance = betaDistance;
		this.betaTravelTime = betaTravelTime;
		this.constant = constant;
		this.isChainBased = isChainBased;
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
	
	public boolean isChainBased() {
		return isChainBased;
	}
}
