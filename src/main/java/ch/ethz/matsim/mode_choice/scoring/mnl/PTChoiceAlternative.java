package ch.ethz.matsim.mode_choice.scoring.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.ModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitTripPrediction;
import ch.ethz.matsim.mode_choice.scoring.ScoringModes;

public class PTChoiceAlternative implements ModeChoiceAlternative {
	final private PublicTransitPredictor predictor;
	final private PredictionCache cache;
	final private ScoringModes scoring;

	public PTChoiceAlternative(ScoringModes scoring, PublicTransitPredictor predictor, PredictionCache cache) {
		this.predictor = predictor;
		this.cache = cache;
		this.scoring = scoring;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		PublicTransitTripPrediction prediction = (PublicTransitTripPrediction) cache.get(trip);

		if (prediction == null) {
			prediction = (PublicTransitTripPrediction) predictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		return scoring.scorePtTrip(prediction.getPredictedTravelDistance(), prediction.getPredictedTravelTime(),
				prediction.getNumberOfLineSwitches(), trip.getPerson());
	}

	@Override
	public boolean isChainMode() {
		return false;
	}
	
	@Override
	public boolean isFeasible(ModeChoiceTrip trip) {
		return true;
	}
}
