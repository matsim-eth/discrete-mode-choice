package ch.ethz.matsim.mode_choice.v2.framework.tour_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelector;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.mode_choice.v2.framework.utils.ModeChainGenerator;
import ch.ethz.matsim.mode_choice.v2.framework.utils.ModeChainGeneratorFactory;

public class TourBasedModel implements ModeChoiceModel {
	final private static Logger logger = Logger.getLogger(TourBasedModel.class);

	final private TourFinder tourFinder;
	final private TourEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private TourConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<TourCandidate> selectorFactory;
	final private ModeChainGeneratorFactory modeChainGeneratorFactory;

	public TourBasedModel(TourEstimator estimator, ModeAvailability modeAvailability,
			TourConstraintFactory constraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory,
			ModeChainGeneratorFactory modeChainGeneratorFactory) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.tourFinder = tourFinder;
		this.selectorFactory = selectorFactory;
		this.modeChainGeneratorFactory = modeChainGeneratorFactory;
	}

	@Override
	public List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random) {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		TourConstraint constraint = constraintFactory.createConstraint(trips, modes);

		List<TourCandidate> tourCandidates = new LinkedList<>();
		List<List<String>> tourCandidateModes = new LinkedList<>();

		for (List<ModeChoiceTrip> tourTrips : tourFinder.findTours(trips)) {
			ModeChainGenerator generator = modeChainGeneratorFactory.createModeChainGenerator(modes, tourTrips);
			UtilitySelector<TourCandidate> selector = selectorFactory.createUtilitySelector();

			while (generator.hasNext()) {
				List<String> tourModes = generator.next();

				if (!constraint.validateBeforeEstimation(tourModes, tourCandidateModes)) {
					continue;
				}

				TourCandidate candidate = estimator.estimateTour(tourModes, tourTrips, tourCandidates);

				if (!constraint.validateAfterEstimation(candidate, tourCandidates)) {
					continue;
				}

				selector.addCandidate(candidate);
			}

			TourCandidate selectedCandidate;

			if (selector.getNumberOfCandidates() > 0) {
				selectedCandidate = (TourCandidate) selector.select(random);
			} else {
				logger.warn("No feasible mode choice candidate for agent " + trips.get(0).getPerson().getId());

				List<String> initialModes = trips.stream().map(ModeChoiceTrip::getInitialMode)
						.collect(Collectors.toList());

				selectedCandidate = estimator.estimateTour(initialModes, tourTrips, tourCandidates);
			}

			tourCandidates.add(selectedCandidate);
			tourCandidateModes.add(
					selectedCandidate.getTripCandidates().stream().map(c -> c.getMode()).collect(Collectors.toList()));
		}

		List<TripCandidate> tripResult = new ArrayList<>(trips.size());
		tourCandidates.forEach(tour -> tripResult.addAll(tour.getTripCandidates()));
		return tripResult;
	}
}
