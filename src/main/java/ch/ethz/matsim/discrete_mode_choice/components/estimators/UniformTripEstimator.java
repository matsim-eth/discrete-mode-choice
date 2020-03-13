package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceUtils;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This estimator simply return a zero utility for every trip candidate that it
 * sees. Useful for random selection setups.
 * 
 * @author sebhoerl
 */
public class UniformTripEstimator implements TripEstimator {
	private final Config config;

	public UniformTripEstimator(Config config) {
		this.config = config;
	}

	@Override
	public TripCandidate estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips) {
		double duration = DiscreteModeChoiceUtils.advanceTime(trip.getInitialElements(), trip.getDepartureTime(),
				config);
		return new DefaultTripCandidate(1.0, mode, duration);
	}
}
