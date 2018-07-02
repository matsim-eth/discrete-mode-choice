package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class TourConstraintFromTripConstraint implements TourConstraint {
	final private TripConstraintFactory factory;
	final private List<ModeChoiceTrip> trips;
	final private Collection<String> availableModes;

	public TourConstraintFromTripConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes,
			TripConstraintFactory factory) {
		this.factory = factory;
		this.trips = trips;
		this.availableModes = availableModes;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> currentTourModes, List<List<String>> previousTourModes) {
		TripConstraint constraint = factory.createConstraint(trips, availableModes);

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
		TripConstraint constraint = factory.createConstraint(trips, availableModes);

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

	static public class Factory implements TourConstraintFactory {
		final private TripConstraintFactory factory;

		public Factory(TripConstraintFactory factory) {
			this.factory = factory;
		}

		@Override
		public TourConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
			return new TourConstraintFromTripConstraint(trips, availableModes, factory);
		}
	}
}
