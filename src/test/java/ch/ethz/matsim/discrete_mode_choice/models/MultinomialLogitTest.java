package ch.ethz.matsim.discrete_mode_choice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.FallbackBehaviour;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.NoFeasibleChoiceException;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.constraints.CompositeTripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.filters.CompositeTripFilter;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.DefaultModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TripFilter;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.MultinomialLogitSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

public class MultinomialLogitTest {
	@Test
	public void testMultinomialLogit() throws NoFeasibleChoiceException {
		TripFilter tripFilter = new CompositeTripFilter(Collections.emptySet());
		ModeAvailability modeAvailability = new DefaultModeAvailability(Arrays.asList("car", "pt", "walk"));
		TripConstraintFactory constraintFactory = new CompositeTripConstraintFactory();
		FallbackBehaviour fallbackBehaviour = FallbackBehaviour.EXCEPTION;
		ConstantTripEstimator estimator = new ConstantTripEstimator();

		double minimumUtility = Double.NEGATIVE_INFINITY;
		double maximumUtility = Double.POSITIVE_INFINITY;
		boolean considerMinimumUtility = false;

		UtilitySelectorFactory selectorFactory = new MultinomialLogitSelector.Factory(minimumUtility, maximumUtility,
				considerMinimumUtility);

		List<DiscreteModeChoiceTrip> trips = Collections
				.singletonList(new DiscreteModeChoiceTrip(null, null, null, null, 0.0, 0, 0));

		TripBasedModel model = new TripBasedModel(estimator, tripFilter, modeAvailability, constraintFactory,
				selectorFactory, fallbackBehaviour);
		Map<String, Integer> choices = new HashMap<>();
		Random random = new Random(0);

		int numberOfSamples = 1000000;

		estimator.setAlternative("car", -1.0);
		estimator.setAlternative("pt", -1.5);
		estimator.setAlternative("walk", -2.0);

		for (int i = 0; i < numberOfSamples; i++) {
			List<TripCandidate> result = model.chooseModes(null, trips, random);
			String mode = result.get(0).getMode();
			choices.put(mode, choices.getOrDefault(mode, 0) + 1);
		}

		assertEquals(0.506480391055654, (double) choices.get("car") / numberOfSamples, 1e-2);
		assertEquals(0.3071958857184984, (double) choices.get("pt") / numberOfSamples, 1e-2);
		assertEquals(0.1863237232258476, (double) choices.get("walk") / numberOfSamples, 1e-2);
	}
}