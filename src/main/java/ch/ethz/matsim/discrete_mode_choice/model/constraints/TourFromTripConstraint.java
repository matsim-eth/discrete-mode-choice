package ch.ethz.matsim.discrete_mode_choice.model.constraints;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * Defines a constraint that applies trip-based constraint on the tour level.
 * This means that the trip constraint must be fulfilled for each trip in the
 * tour for the tour to be feasible.
 * 
 * @author sebhoerl
 */
public class TourFromTripConstraint implements TourConstraint {
	private final List<DiscreteModeChoiceTrip> trips;
	private final TripConstraint constraint;

	TourFromTripConstraint(List<DiscreteModeChoiceTrip> trips, TripConstraint constraint) {
		this.constraint = constraint;
		this.trips = trips;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> currentTourModes, List<List<String>> previousTourModes) {
		List<String> previousTripModes = new LinkedList<>();
		previousTourModes.forEach(previousTripModes::addAll);
		int numberOfPreviousTrips = previousTripModes.size();

		for (int i = 0; i < currentTourModes.size(); i++) {
			int currentTripIndex = numberOfPreviousTrips + i;

			if (!constraint.validateBeforeEstimation(trips.get(currentTripIndex), currentTourModes.get(i),
					previousTripModes)) {
				return false;
			}

			previousTripModes.add(currentTourModes.get(i));
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(TourCandidate currentTourCandidate,
			List<TourCandidate> previousTourCandidates) {
		List<TripCandidate> previousTripCandidates = new LinkedList<>();
		previousTourCandidates.stream().map(TourCandidate::getTripCandidates).forEach(previousTripCandidates::addAll);
		int numberOfPreviousCandidates = previousTripCandidates.size();

		for (int i = 0; i < currentTourCandidate.getTripCandidates().size(); i++) {
			int currentTripIndex = numberOfPreviousCandidates + i;
			TripCandidate currentTripCandidate = currentTourCandidate.getTripCandidates().get(i);

			if (!constraint.validateAfterEstimation(trips.get(currentTripIndex), currentTripCandidate,
					previousTripCandidates)) {
				return false;
			}

			previousTripCandidates.add(currentTripCandidate);
		}

		return true;
	}
}
