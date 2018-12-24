package ch.ethz.matsim.mode_choice.framework.trip_based;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.mode_choice.framework.DefaultModeChoiceResult;
import ch.ethz.matsim.mode_choice.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.framework.ModeChoiceResult;
import ch.ethz.matsim.mode_choice.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.framework.trip_based.estimation.TripEstimator;
import ch.ethz.matsim.mode_choice.framework.utilities.UtilitySelector;
import ch.ethz.matsim.mode_choice.framework.utilities.UtilitySelectorFactory;

public class TripBasedModel implements ModeChoiceModel {
	final private static Logger logger = Logger.getLogger(TripBasedModel.class);

	final private TripEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private TripConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<TripCandidate> selectorFactory;
	final private FallbackBehaviour fallbackBehaviour;

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
	public ModeChoiceResult chooseModes(List<ModeChoiceTrip> trips, Random random) throws NoFeasibleChoiceException {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		TripConstraint constraint = constraintFactory.createConstraint(trips, modes);

		List<TripCandidate> tripCandidates = new ArrayList<>(trips.size());
		List<String> tripCandidateModes = new ArrayList<>(trips.size());

		boolean ignoreAgentRequested = false;

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

			TripCandidate selectedCandidate = null;

			if (selector.getNumberOfCandidates() > 0) {
				Optional<TripCandidate> cand = selector.select(random);
				if (cand.isPresent())
					selectedCandidate = cand.get();
				else {
					if (fallbackBehaviour.equals(FallbackBehaviour.INITIAL_CHOICE)) {
						selectedCandidate = handleInitialChoiceFallback(trip, tripCandidates);
					} else if (fallbackBehaviour.equals(FallbackBehaviour.EXCEPTION)) {
						throw new NoFeasibleChoiceException(
								buildFallbackMessage(trip.getPerson(), "Throwing exception."));
					} else if (fallbackBehaviour.equals(FallbackBehaviour.IGNORE_AGENT)) {
						ignoreAgentRequested = true;
						break;
					}
				}
			} else if (fallbackBehaviour.equals(FallbackBehaviour.INITIAL_CHOICE)) {
				selectedCandidate = handleInitialChoiceFallback(trip, tripCandidates);
			} else if (fallbackBehaviour.equals(FallbackBehaviour.EXCEPTION)) {
				throw new NoFeasibleChoiceException(buildFallbackMessage(trip.getPerson(), "Throwing exception."));
			} else if (fallbackBehaviour.equals(FallbackBehaviour.IGNORE_AGENT)) {
				ignoreAgentRequested = true;
				break;
			}

			tripCandidates.add(selectedCandidate);
			tripCandidateModes.add(selectedCandidate.getMode());
		}

		if (ignoreAgentRequested) {
			tripCandidates = handleIgnoreAgentFallback(trips);
		}

		return new DefaultModeChoiceResult(tripCandidates);
	}

	private String buildFallbackMessage(Person person, String appendix) {
		return String.format("No feasible mode choice candidate for agent %s. %s", person.getId().toString(), appendix);
	}

	private TripCandidate handleInitialChoiceFallback(ModeChoiceTrip trip, List<TripCandidate> tripCandidates) {
		logger.warn(buildFallbackMessage(trip.getPerson(), "Using fallback."));

		TripCandidate fallbackCandidate = estimator.estimateTrip(trip.getInitialMode(), trip, tripCandidates);
		fallbackCandidate.setFallback(true);

		return fallbackCandidate;
	}

	private List<TripCandidate> handleIgnoreAgentFallback(List<ModeChoiceTrip> trips) {
		logger.warn(buildFallbackMessage(trips.get(0).getPerson(), "Ignoring agent."));

		List<TripCandidate> tripCandidates = new LinkedList<>();

		for (ModeChoiceTrip trip : trips) {
			tripCandidates.add(estimator.estimateTrip(trip.getInitialMode(), trip, tripCandidates));
		}

		tripCandidates.forEach(c -> c.setFallback(true));

		return tripCandidates;
	}
}
