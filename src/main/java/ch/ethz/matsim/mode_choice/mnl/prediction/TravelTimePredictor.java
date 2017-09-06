package ch.ethz.matsim.mode_choice.mnl.prediction;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public interface TravelTimePredictor {
	double predictTravelTime(ModeChoiceTrip trip);
}
