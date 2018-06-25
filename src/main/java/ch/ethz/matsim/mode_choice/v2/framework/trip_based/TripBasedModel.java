package ch.ethz.matsim.mode_choice.v2.framework.trip_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelector;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelectorFactory;

public class TripBasedModel implements ModeChoiceModel {
	final private TripEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private TripConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<TripCandidate> selectorFactory;

	public TripBasedModel(TripEstimator estimator, ModeAvailability modeAvailability,
			TripConstraintFactory constraintFactory, UtilitySelectorFactory<TripCandidate> selectorFactory) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.selectorFactory = selectorFactory;
	}

	@Override
	public List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random) {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		TripConstraint constraint = constraintFactory.createConstraint(trips, modes);

		List<TripCandidate> tripCandidates = new ArrayList<>(trips.size());
		List<String> tripCandidateModes = new ArrayList<>(trips.size());

		for (ModeChoiceTrip trip : trips) {
			UtilitySelector<TripCandidate> selector = selectorFactory.createUtilitySelector();

			for (String mode : modes) {
				if (!constraint.validateBeforeEstimation(trip, mode, tripCandidateModes)) {
					continue;
				}

				TripCandidate candidate = estimator.estimateTrip(mode, trip, tripCandidates);

				if (!constraint.validateAfterEstimation(trip, candidate, tripCandidates)) {
					continue;
				}

				selector.addCandidate(candidate);
			}

			TripCandidate selectedCandidate = selector.select(random);
			tripCandidates.add(selectedCandidate);
			tripCandidateModes.add(selectedCandidate.getMode());
		}

		return tripCandidates;
	}
}
