package ch.ethz.matsim.discrete_mode_choice.model.trip_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

/**
 * This class defines a trip-based discrete choice model.
 * 
 * @author sebhoerl
 *
 */
public class TripBasedModel implements DiscreteModeChoiceModel {
	private final static Logger logger = Logger.getLogger(TripBasedModel.class);

	private final TripEstimator estimator;
	private final ModeAvailability modeAvailability;
	private final TripConstraintFactory constraintFactory;
	private final UtilitySelectorFactory<TripCandidate> selectorFactory;
	private final FallbackBehaviour fallbackBehaviour;

	public TripBasedModel(TripEstimator estimator, ModeAvailability modeAvailability,
			TripConstraintFactory constraintFactory, UtilitySelectorFactory<TripCandidate> selectorFactory,
			FallbackBehaviour fallbackBehaviour) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.selectorFactory = selectorFactory;
		this.fallbackBehaviour = fallbackBehaviour;
	}

	@Override
	public List<TripCandidate> chooseModes(Person person, List<DiscreteModeChoiceTrip> trips, Random random)
			throws NoFeasibleChoiceException {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(person, trips));
		TripConstraint constraint = constraintFactory.createConstraint(person, trips, modes);

		List<TripCandidate> tripCandidates = new ArrayList<>(trips.size());
		List<String> tripCandidateModes = new ArrayList<>(trips.size());

		for (DiscreteModeChoiceTrip trip : trips) {
			UtilitySelector<TripCandidate> selector = selectorFactory.createUtilitySelector();

			for (String mode : modes) {
				if (!constraint.validateBeforeEstimation(trip, mode, tripCandidateModes)) {
					continue;
				}

				TripCandidate candidate = estimator.estimateTrip(person, mode, trip, tripCandidates);

				if (!constraint.validateAfterEstimation(trip, candidate, tripCandidates)) {
					continue;
				}

				selector.addCandidate(candidate);
			}

			Optional<TripCandidate> selectedCandidate = selector.select(random);

			if (!selectedCandidate.isPresent()) {
				switch (fallbackBehaviour) {
				case INITIAL_CHOICE:
					TripCandidate fallbackCandidate = estimator.estimateTrip(person, trip.getInitialMode(), trip,
							tripCandidates);
					logger.info(buildFallbackMessage(person, "Setting trip back to initial mode."));
					selectedCandidate = Optional.ofNullable(fallbackCandidate);
					break;
				case IGNORE_AGENT:
					return handleIgnoreAgent(person, trips);
				case EXCEPTION:
					throw new NoFeasibleChoiceException(buildFallbackMessage(person, ""));
				}
			}

			tripCandidates.add(selectedCandidate.get());
			tripCandidateModes.add(selectedCandidate.get().getMode());
		}

		return tripCandidates;
	}

	private List<TripCandidate> handleIgnoreAgent(Person person, List<DiscreteModeChoiceTrip> trips) {
		List<TripCandidate> candidates = new ArrayList<>(trips.size());

		for (DiscreteModeChoiceTrip trip : trips) {
			candidates.add(estimator.estimateTrip(person, trip.getInitialMode(), trip, candidates));
		}

		logger.warn(buildFallbackMessage(person, "Setting whole plan back to initial modes."));
		return candidates;
	}

	private String buildFallbackMessage(Person person, String appendix) {
		return String.format("No feasible mode choice candidate for agent %s. %s", person.getId().toString(), appendix);
	}
}
