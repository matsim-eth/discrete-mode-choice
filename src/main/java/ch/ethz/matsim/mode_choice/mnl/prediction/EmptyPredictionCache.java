package ch.ethz.matsim.mode_choice.mnl.prediction;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class EmptyPredictionCache implements PredictionCache {
	@Override
	public TripPrediction get(ModeChoiceTrip trip) {
		return null;
	}

	@Override
	public void put(ModeChoiceTrip trip, TripPrediction prediction) {

	}

	@Override
	public void clear() {

	}
}
