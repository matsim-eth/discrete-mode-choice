package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.matsim.core.controler.events.IterationStartsEvent;
import org.matsim.core.controler.listener.IterationStartsListener;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripEstimator;

public class TripEstimatorCache implements IterationStartsListener {
	final private Map<String, TripCandidate> cache = new HashMap<>();

	public TripEstimator cached(TripEstimator delegate) {
		return new Decorator(delegate);
	}

	public class Decorator implements TripEstimator {
		final private TripEstimator delegate;

		private Decorator(TripEstimator delegate) {
			this.delegate = delegate;
		}

		@Override
		public TripCandidate estimateTrip(String mode, ModeChoiceTrip trip, List<TripCandidate> preceedingTrips) {
			String key = buildKey(trip, mode);

			synchronized (cache) {
				if (cache.containsKey(key)) {
					return cache.get(key);
				}
			}

			TripCandidate result = delegate.estimateTrip(mode, trip, preceedingTrips);

			synchronized (cache) {
				cache.put(key, result);
			}

			return result;
		}
	}

	protected String buildKey(ModeChoiceTrip trip, String mode) {
		int tripIndex = trip.getPlan().indexOf(trip.getTripInformation());
		return trip.getPerson().getId().toString() + "::" + String.valueOf(tripIndex) + "::" + mode;
	}

	public void clear() {
		this.cache.clear();
	}

	@Override
	public void notifyIterationStarts(IterationStartsEvent event) {
		clear();
	}
}
