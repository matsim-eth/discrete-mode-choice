package ch.ethz.matsim.mode_choice.framework.tour_based;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.matsim.mode_choice.framework.ModeChoiceResult;
import ch.ethz.matsim.mode_choice.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public class TourBasedModeChoiceResult implements ModeChoiceResult {
	final private List<TourCandidate> tourCandidates;

	public TourBasedModeChoiceResult(List<TourCandidate> tourCandidates) {
		this.tourCandidates = tourCandidates;
	}

	public List<TourCandidate> getTourCandidates() {
		return tourCandidates;
	}

	@Override
	public List<TripCandidate> getTripCandidates() {
		return tourCandidates.stream().map(TourCandidate::getTripCandidates).flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
