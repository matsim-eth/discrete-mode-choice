package ch.ethz.matsim.mode_choice.scoring.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.DefaultTripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.NetworkPathPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.scoring.ScoringModes;

public class CarChoiceAlternative implements ModeChoiceAlternative {
	final private NetworkPathPredictor predictor;
	final private PredictionCache cache;
	final private ScoringModes scoring;

	public CarChoiceAlternative(ScoringModes scoring, NetworkPathPredictor predictor, PredictionCache cache) {
		this.predictor = predictor;
		this.cache = cache;
		this.scoring = scoring;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		DefaultTripPrediction prediction = (DefaultTripPrediction) cache.get(trip);

		if (prediction == null) {
			prediction = (DefaultTripPrediction) predictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		return scoring.scoreCarTrip(prediction.getPredictedTravelDistance(), prediction.getPredictedTravelTime(),
				trip.getPerson());
	}

	@Override
	public boolean isChainMode() {
		return true;
	}
}
