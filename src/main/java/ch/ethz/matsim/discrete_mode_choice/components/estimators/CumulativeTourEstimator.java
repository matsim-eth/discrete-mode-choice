package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.DefaultTourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This class is a TourEstimator which is based on a TripEstimator. Every trip
 * in the tour is estimated by the underlying TripEstimator and utilities are
 * summed up to arrive at a total utility for the whole tour.
 * 
 * @author sebhoerl
 */
public class CumulativeTourEstimator implements TourEstimator {
	final private TripEstimator delegate;

	public CumulativeTourEstimator(TripEstimator delegate) {
		this.delegate = delegate;
	}

	@Override
	public TourCandidate estimateTour(Person person, List<String> modes, List<DiscreteModeChoiceTrip> trips,
			List<TourCandidate> preceedingTours) {
		List<TripCandidate> tripCandidates = new LinkedList<>();
		double utility = 0.0;

		for (int i = 0; i < modes.size(); i++) {
			String mode = modes.get(i);
			DiscreteModeChoiceTrip trip = trips.get(i);

			TripCandidate tripCandidate = delegate.estimateTrip(person, mode, trip, tripCandidates);
			utility += tripCandidate.getUtility();

			tripCandidates.add(tripCandidate);
		}

		return new DefaultTourCandidate(utility, tripCandidates);
	}
}
