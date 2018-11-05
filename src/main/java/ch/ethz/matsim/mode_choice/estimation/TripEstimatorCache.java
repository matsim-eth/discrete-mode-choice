package ch.ethz.matsim.mode_choice.estimation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripEstimator;

public class TripEstimatorCache implements TripEstimator {
	final private Map<String, Map<String, TripCandidate>> cache = new HashMap<>();
	final private TripEstimator delegate;

	public TripEstimatorCache(TripEstimator delegate, Collection<String> modes) {
		this.delegate = delegate;

		for (String mode : modes) {
			cache.put(mode, new HashMap<>());
		}
	}

	@Override
	public TripCandidate estimateTrip(String mode, ModeChoiceTrip trip, List<TripCandidate> preceedingTrips) {
		Map<String, TripCandidate> modeCache = cache.get(mode);

		if (modeCache != null) {
			String key = buildKey(trip);

			if (modeCache.containsKey(key)) {
				return modeCache.get(key);
			} else {
				TripCandidate result = delegate.estimateTrip(mode, trip, preceedingTrips);
				modeCache.put(key, result);
				return result;
			}
		} else {
			return delegate.estimateTrip(mode, trip, preceedingTrips);
		}

	}

	protected String buildKey(ModeChoiceTrip trip) {
		int tripIndex = trip.getPlan().indexOf(trip.getTripInformation());
		return trip.getPerson().getId().toString() + "::" + String.valueOf(tripIndex);
	}
}
