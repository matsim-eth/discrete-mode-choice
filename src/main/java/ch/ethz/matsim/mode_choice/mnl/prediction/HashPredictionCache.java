package ch.ethz.matsim.mode_choice.mnl.prediction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.matsim.mode_choice.ModeChoiceTrip;

public class HashPredictionCache implements PredictionCache {
	final private Map<ModeChoiceTrip, TripPrediction> cache = Collections.synchronizedMap(new HashMap<>());
	
	@Override
	public TripPrediction get(ModeChoiceTrip trip) {
		return cache.get(trip);
	}
	
	@Override
	public void put(ModeChoiceTrip trip, TripPrediction prediction) {
		cache.put(trip, prediction);
	}
	
	@Override
	public void clear() {
		cache.clear();
	}
}
