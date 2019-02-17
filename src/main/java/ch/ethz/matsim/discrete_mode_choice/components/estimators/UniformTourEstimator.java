package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.DefaultTourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultTripCandidate;

/**
 * This estimator simply return a zero utility for every tour candidate that it
 * sees. Useful for random selection setups.
 * 
 * @author sebhoerl
 */
public class UniformTourEstimator implements TourEstimator {
	@Override
	public TourCandidate estimateTour(Person person, List<String> modes, List<DiscreteModeChoiceTrip> trips,
			List<TourCandidate> previousTours) {
		return new DefaultTourCandidate(1.0,
				modes.stream().map(mode -> new DefaultTripCandidate(1.0, mode)).collect(Collectors.toList()));
	}
}
