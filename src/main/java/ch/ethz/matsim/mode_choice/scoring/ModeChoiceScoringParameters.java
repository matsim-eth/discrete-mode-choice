package ch.ethz.matsim.mode_choice.scoring;

public class ModeChoiceScoringParameters {
	
	public final double betatraveltime;
	public final double betadistance;
	public final double constant;
	public final double betacarAvail;
	public final double betaincome;
	public final double betaage;
	public final double betagender;
	public final double betatransfers;
	
	
	public ModeChoiceScoringParameters(
			double betatraveltime, double betadistance,
			double constant, double carAvail, double betaincome,
			double betaage, double betagender, double betatransfers) {
		this.betatraveltime = betatraveltime;
		this.betadistance = betadistance;
		this.constant = constant;
		this.betacarAvail = carAvail;
		this.betaincome = betaincome;
		this.betaage = betaage;
		this.betagender = betagender;
		this.betatransfers = betatransfers;
	}

}
