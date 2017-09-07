package ch.ethz.matsim.mode_choice.scoring.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.DefaultTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;
import ch.ethz.matsim.mode_choice.scoring.ScoringModes;

public class WalkChoiceAlternative implements ModeChoiceAlternative {
	final private FixedSpeedPredictor predictor;
	final private ScoringModes scoring;

	public WalkChoiceAlternative(ScoringModes scoring, FixedSpeedPredictor predictor) {
		this.predictor = predictor;
		this.scoring = scoring;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		DefaultTripPrediction prediction = (DefaultTripPrediction) predictor.predictTrip(trip);
		return scoring.scoreBikeTrip(prediction.getPredictedTravelDistance(), prediction.getPredictedTravelTime(),
				trip.getPerson());
	}

	@Override
	public boolean isChainMode() {
		return false;
	}
}
