package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;
import java.util.Random;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public interface ModeChoiceModel {
	List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random);
}
