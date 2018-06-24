package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class TripCandidateCache {
	final private Map<String, TripCandidate> cache = new HashMap<>();

	public boolean exists(ModeChoiceTrip trip) {
		return cache.containsKey(buildKey(trip));
	}

	public void set(ModeChoiceTrip trip, TripCandidate candidate) {
		cache.put(buildKey(trip), candidate);
	}

	public TripCandidate get(ModeChoiceTrip trip) {
		TripCandidate candidate = cache.get(buildKey(trip));

		if (candidate == null) {
			throw new IllegalStateException("No cache for trip: " + trip.toString());
		}

		return candidate;
	}

	protected String buildKey(ModeChoiceTrip trip) {
		int tripIndex = trip.getPlan().indexOf(trip.getTripInformation());
		return trip.getPerson().getId().toString() + "::" + String.valueOf(tripIndex);
	}
}
