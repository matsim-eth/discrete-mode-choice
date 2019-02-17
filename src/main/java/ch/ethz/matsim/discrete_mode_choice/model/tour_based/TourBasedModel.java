package ch.ethz.matsim.discrete_mode_choice.model.tour_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGeneratorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

/**
 * A choice model that makes decision on a tour basis. The major difference over
 * the trip-based model is, that it additionally relies on a TourFinder to
 * determine the tours in an agent's plan.
 * 
 * @author sebhoerl
 */
public class TourBasedModel implements DiscreteModeChoiceModel {
	final private static Logger logger = Logger.getLogger(TourBasedModel.class);

	final private TourFinder tourFinder;
	final private TourEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private TourConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<TourCandidate> selectorFactory;
	final private ModeChainGeneratorFactory modeChainGeneratorFactory;
	final private FallbackBehaviour fallbackBehaviour;

	public TourBasedModel(TourEstimator estimator, ModeAvailability modeAvailability,
			TourConstraintFactory constraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory, ModeChainGeneratorFactory modeChainGeneratorFactory,
			FallbackBehaviour fallbackBehaviour) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.tourFinder = tourFinder;
		this.selectorFactory = selectorFactory;
		this.modeChainGeneratorFactory = modeChainGeneratorFactory;
		this.fallbackBehaviour = fallbackBehaviour;
	}

	@Override
	public List<TripCandidate> chooseModes(Person person, List<DiscreteModeChoiceTrip> trips, Random random)
			throws NoFeasibleChoiceException {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(person, trips));
		TourConstraint constraint = constraintFactory.createConstraint(person, trips, modes);

		List<TourCandidate> tourCandidates = new LinkedList<>();
		List<List<String>> tourCandidateModes = new LinkedList<>();

		for (List<DiscreteModeChoiceTrip> tourTrips : tourFinder.findTours(trips)) {
			ModeChainGenerator generator = modeChainGeneratorFactory.createModeChainGenerator(modes, person, tourTrips);
			UtilitySelector<TourCandidate> selector = selectorFactory.createUtilitySelector();

			while (generator.hasNext()) {
				List<String> tourModes = generator.next();

				if (!constraint.validateBeforeEstimation(tourModes, tourCandidateModes)) {
					continue;
				}

				TourCandidate candidate = estimator.estimateTour(person, tourModes, tourTrips, tourCandidates);

				if (!constraint.validateAfterEstimation(candidate, tourCandidates)) {
					continue;
				}

				selector.addCandidate(candidate);
			}

			Optional<TourCandidate> selectedCandidate = selector.select(random);

			if (!selectedCandidate.isPresent()) {
				switch (fallbackBehaviour) {
				case INITIAL_CHOICE:
					List<String> initialModes = trips.stream().map(DiscreteModeChoiceTrip::getInitialMode)
							.collect(Collectors.toList());
					TourCandidate fallbackCandidate = estimator.estimateTour(person, initialModes, tourTrips,
							tourCandidates);
					logger.warn(buildFallbackMessage(person, "Setting tour modes back to initial choice."));
					selectedCandidate = Optional.of(fallbackCandidate);
					break;
				case IGNORE_AGENT:
					return handleIgnoreAgent(person, tourTrips);
				case EXCEPTION:
					throw new NoFeasibleChoiceException(buildFallbackMessage(person, ""));
				}
			}

			tourCandidates.add(selectedCandidate.get());
			tourCandidateModes.add(selectedCandidate.get().getTripCandidates().stream().map(c -> c.getMode())
					.collect(Collectors.toList()));
		}

		return createTripCandidates(tourCandidates);
	}

	private List<TripCandidate> createTripCandidates(List<TourCandidate> tourCandidates) {
		return tourCandidates.stream().map(TourCandidate::getTripCandidates).flatMap(List::stream)
				.collect(Collectors.toList());
	}

	private List<TripCandidate> handleIgnoreAgent(Person person, List<DiscreteModeChoiceTrip> trips) {
		List<TourCandidate> tourCandidates = new LinkedList<>();

		for (List<DiscreteModeChoiceTrip> tourTrips : tourFinder.findTours(trips)) {
			List<String> tourModes = tourTrips.stream().map(DiscreteModeChoiceTrip::getInitialMode)
					.collect(Collectors.toList());
			tourCandidates.add(estimator.estimateTour(person, tourModes, tourTrips, tourCandidates));
		}

		logger.warn(buildFallbackMessage(person, "Setting whole plan back to initial modes."));
		return createTripCandidates(tourCandidates);
	}

	private String buildFallbackMessage(Person person, String appendix) {
		return String.format("No feasible mode choice candidate for agent %s. %s", person.getId().toString(), appendix);
	}
}
