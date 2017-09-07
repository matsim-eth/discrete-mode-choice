package ch.ethz.matsim.mode_choice.mnl.prediction;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public interface PredictionCache {
	TripPrediction get(ModeChoiceTrip trip);
	void put(ModeChoiceTrip trip, TripPrediction prediction);
	void clear();
}