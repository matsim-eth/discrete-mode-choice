package ch.ethz.matsim.mode_choice.mnl;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.PredictionCache;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitPredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.PublicTransitTripPrediction;

public class BasicPublicTransitModeChoiceAlternative implements ModeChoiceAlternative {
	final private BasicPublicTransitModeChoiceParameters params;
	final private PublicTransitPredictor transitPredictor;
	final private PredictionCache cache;

	public BasicPublicTransitModeChoiceAlternative(BasicPublicTransitModeChoiceParameters params,
			PublicTransitPredictor transitPredictor, PredictionCache cache) {
		this.params = params;
		this.transitPredictor = transitPredictor;
		this.cache = cache;
	}
	
	@Override
	public boolean isFeasible(ModeChoiceTrip trip) {
		return true;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		PublicTransitTripPrediction prediction = (PublicTransitTripPrediction) cache.get(trip);

		if (prediction == null) {
			prediction = transitPredictor.predictTrip(trip);
			cache.put(trip, prediction);
		}
		
		if (prediction.isOnlyTransitWalk()) {
			return Double.NEGATIVE_INFINITY;
		}

		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelDistance()
				+ params.getBetaDistance() * prediction.getPredictedTravelDistance()
				+ params.getBetaTransferTravelTime() * prediction.getTransferTime()
				+ params.getBetaTransferDistance() * prediction.getTransferDistance()
				+ params.getBetaNumberOfLineSwitches() * prediction.getNumberOfLineSwitches()
				+ params.getBetaWaitingTime() * prediction.getWaitingTime();
	}

	@Override
	public boolean isChainMode() {
		return false;
	}
}
