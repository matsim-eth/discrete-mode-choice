package ch.ethz.matsim.mode_choice.v2.framework;

import java.util.List;

import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class DefaultModeChoiceResult implements ModeChoiceResult {
	final private List<TripCandidate> tripCandidates;

	public DefaultModeChoiceResult(List<TripCandidate> tripCandidates) {
		this.tripCandidates = tripCandidates;
	}

	@Override
	public List<TripCandidate> getTripCandidates() {
		return tripCandidates;
	}
}
