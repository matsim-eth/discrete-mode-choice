package ch.ethz.matsim.mode_choice.estimation;

public class TripCandidateWithPrediction extends DefaultTripCandidate {
	final private Object prediction;

	public TripCandidateWithPrediction(double utility, String mode, Object prediction) {
		super(utility, mode);
		this.prediction = prediction;
	}

	public Object getPrediction() {
		return prediction;
	}
}
