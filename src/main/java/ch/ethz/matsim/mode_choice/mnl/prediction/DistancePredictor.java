package ch.ethz.matsim.mode_choice.mnl.prediction;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public interface DistancePredictor {
	double predictDistance(ModeChoiceTrip trip);
}
