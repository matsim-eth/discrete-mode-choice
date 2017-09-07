package ch.ethz.matsim.mode_choice.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.EmptyPredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class BasicModeChoiceAlternative implements ModeChoiceAlternative {
	final private BasicModeChoiceParameters params;
	final private TripPredictor tripPredictor;
	final private PredictionCache cache;

	public BasicModeChoiceAlternative(BasicModeChoiceParameters params, TripPredictor tripPredictor,
			PredictionCache cache) {
		this.params = params;
		this.tripPredictor = tripPredictor;
		this.cache = cache;
	}

	public BasicModeChoiceAlternative(BasicModeChoiceParameters params, TripPredictor tripPredictor) {
		this(params, tripPredictor, new EmptyPredictionCache());
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = cache.get(trip);

		if (prediction == null) {
			prediction = tripPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}

		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
				+ params.getBetaDistance() * prediction.getPredictedTravelDistance();
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
