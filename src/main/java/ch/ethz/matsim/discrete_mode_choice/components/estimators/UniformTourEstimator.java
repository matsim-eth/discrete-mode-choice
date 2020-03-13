package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceUtils;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.DefaultTourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This estimator simply return a zero utility for every tour candidate that it
 * sees. Useful for random selection setups.
 * 
 * @author sebhoerl
 */
public class UniformTourEstimator implements TourEstimator {
	private final Config config;

	public UniformTourEstimator(Config config) {
		this.config = config;
	}

	@Override
	public TourCandidate estimateTour(Person person, List<String> modes, List<DiscreteModeChoiceTrip> trips,
			List<TourCandidate> previousTours) {
		List<TripCandidate> tripCandidates = new ArrayList<>(modes.size());

		for (int index = 0; index < modes.size(); index++) {
			DiscreteModeChoiceTrip trip = trips.get(index);
			double duration = DiscreteModeChoiceUtils.advanceTime(trip.getInitialElements(), trip.getDepartureTime(),
					config);
			tripCandidates.add(new DefaultTripCandidate(1.0, modes.get(index), duration));
		}

		return new DefaultTourCandidate(1.0, tripCandidates);
	}
}
