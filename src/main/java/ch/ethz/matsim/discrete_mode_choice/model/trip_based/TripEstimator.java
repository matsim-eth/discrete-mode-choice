package ch.ethz.matsim.discrete_mode_choice.model.trip_based;

import java.util.List;

import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This interface represents a function that calculates a utility (or additional
 * information) for a given trip performed with a certain mode.
 * 
 * @author sebhoerl
 */
public interface TripEstimator {
	TripCandidate estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip, List<TripCandidate> previousTrips);
}
