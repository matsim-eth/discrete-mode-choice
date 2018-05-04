package ch.ethz.matsim.mode_choice.scoring.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.DefaultTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.scoring.ScoringModes;

public class BikeChoiceAlternative implements ModeChoiceAlternative {
	final private FixedSpeedPredictor predictor;
	final private ScoringModes scoring;

	public BikeChoiceAlternative(ScoringModes scoring, FixedSpeedPredictor predictor) {
		this.predictor = predictor;
		this.scoring = scoring;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		DefaultTripPrediction prediction = (DefaultTripPrediction) predictor.predictTrip(trip);
		return scoring.scoreWalkTrip(prediction.getPredictedTravelTime(), trip.getPerson());
	}

	@Override
	public boolean isChainMode() {
		return true;
	}
	
	@Override
	public boolean isFeasible(ModeChoiceTrip trip) {
		return true;
	}
}
