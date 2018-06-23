package ch.ethz.matsim.mode_choice.v2.framework.tour_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

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

public class TourBasedModel implements ModeChoiceModel {
	final private TourFinder tourFinder;
	final private TourEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private TourConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<TourCandidate> selectorFactory;

	public TourBasedModel(TourEstimator estimator, ModeAvailability modeAvailability,
			TourConstraintFactory constraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.tourFinder = tourFinder;
		this.selectorFactory = selectorFactory;
	}

	@Override
	public List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random) {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		TourConstraint constraint = constraintFactory.createConstraint(trips, modes);

		List<TourCandidate> result = new LinkedList<>();

		for (List<ModeChoiceTrip> tour : tourFinder.findTours(trips)) {
			ModeChainGenerator generator = new ModeChainGenerator(modes, tour.size());
			UtilitySelector<TourCandidate> selector = selectorFactory.createUtilitySelector();

			StreamSupport.stream(Spliterators.spliterator(generator, generator.getNumberOfAlternatives(), 0), false)//
					.filter(constraint::validateBeforeEstimation) //
					.map(ms -> estimator.estimateTour(ms, tour, result)) //
					.filter(constraint::validateAfterEstimation) //
					.forEach(selector::addCandidate);

			TourCandidate selectedCandidate = (TourCandidate) selector.select(random);

			result.add(selectedCandidate);
			constraint.acceptTour(selectedCandidate);
		}

		List<TripCandidate> tripResult = new ArrayList<>(trips.size());
		result.forEach(tour -> tripResult.addAll(tour.getTripCandidates()));
		return tripResult;
	}
}
