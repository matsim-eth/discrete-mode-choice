package ch.ethz.matsim.mode_choice.mnl;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPrediction;
import ch.ethz.matsim.mode_choice.mnl.prediction.TripPredictor;

public class BasicModeChoiceAlternative implements ModeChoiceAlternative {
	final private BasicModeChoiceParameters params;
	final private TripPredictor tripPredictor;

	public BasicModeChoiceAlternative(BasicModeChoiceParameters params, TripPredictor tripPredictor) {
		this.params = params;
		this.tripPredictor = tripPredictor;
	}

	@Override
	public double estimateUtility(ModeChoiceTrip trip) {
		TripPrediction prediction = tripPredictor.predictTrip(trip);

		return params.getConstant() + params.getBetaTravelTime() * prediction.getPredictedTravelTime()
				+ params.getBetaDistance() * prediction.getPredictedTravelDistance();
	}

	@Override
	public boolean isChainMode() {
		return params.isChainBased();
	}
}
