package ch.ethz.matsim.mode_choice.estimation;

import java.util.List;

import ch.ethz.matsim.mode_choice.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;

public class DefaultTourCandidate implements TourCandidate {
	final private double utility;
	final private List<TripCandidate> tripCandidates;
	private boolean isFallback = false;

	public DefaultTourCandidate(double utility, List<TripCandidate> tripCandidates) {
		this.utility = utility;
		this.tripCandidates = tripCandidates;
	}

	@Override
	public double getUtility() {
		return utility;
	}

	@Override
	public List<TripCandidate> getTripCandidates() {
		return tripCandidates;
	}

	@Override
	public boolean isFallback() {
		return isFallback;
	}

	@Override
	public void setFallback(boolean isFallback) {
		this.isFallback = isFallback;
	}
}
