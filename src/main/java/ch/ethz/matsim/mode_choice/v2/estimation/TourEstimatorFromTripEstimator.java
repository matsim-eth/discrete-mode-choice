package ch.ethz.matsim.mode_choice.v2.estimation;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripEstimator;

public class TourEstimatorFromTripEstimator implements TourEstimator {
	final private TripEstimator delegate;

	public TourEstimatorFromTripEstimator(TripEstimator delegate) {
		this.delegate = delegate;
	}

	@Override
	public TourCandidate estimateTour(List<String> modes, List<ModeChoiceTrip> trips,
			List<TourCandidate> preceedingTours) {
		List<TripCandidate> tripCandidates = new LinkedList<>();
		double utility = 0.0;

		for (int i = 0; i < modes.size(); i++) {
			String mode = modes.get(i);
			ModeChoiceTrip trip = trips.get(i);

			TripCandidate tripCandidate = delegate.estimateTrip(mode, trip, tripCandidates);
			utility += tripCandidate.getUtility();

			tripCandidates.add(tripCandidate);
		}

		return new DefaultTourCandidate(utility, tripCandidates);
	}
}
