package ch.ethz.matsim.discrete_mode_choice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import ch.ethz.matsim.discrete_mode_choice.model.utilities.MaximumSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

public class MaximumUtilityTest {
	@Test
	public void testMaximumUtility() throws NoFeasibleChoiceException {
		TripFilter tripFilter = new CompositeTripFilter(Collections.emptySet());
		ModeAvailability modeAvailability = new DefaultModeAvailability(Arrays.asList("car", "pt", "walk"));
		TripConstraintFactory constraintFactory = new CompositeTripConstraintFactory();
		FallbackBehaviour fallbackBehaviour = FallbackBehaviour.EXCEPTION;
		ConstantTripEstimator estimator = new ConstantTripEstimator();
		UtilitySelectorFactory selectorFactory = new MaximumSelector.Factory();

		List<DiscreteModeChoiceTrip> trips = Collections
				.singletonList(new DiscreteModeChoiceTrip(null, null, null, null, 0.0, 0, 0));

		TripBasedModel model = new TripBasedModel(estimator, tripFilter, modeAvailability, constraintFactory,
				selectorFactory, fallbackBehaviour);

		List<TripCandidate> result;		
		
		// Test 1
		estimator.setAlternative("car", -1.0);
		estimator.setAlternative("pt", -1.5);
		estimator.setAlternative("walk", -2.0);

		result = model.chooseModes(null, trips, new Random(0));
		assertEquals(1, result.size());
		assertEquals("car", result.get(0).getMode());
		assertEquals(-1.0, result.get(0).getUtility(), 1e-3);
		
		// Test 2
		estimator.setAlternative("car", -1.0);
		estimator.setAlternative("pt", 2.5);
		estimator.setAlternative("walk", -2.0);

		result = model.chooseModes(null, trips, new Random(0));
		assertEquals(1, result.size());
		assertEquals("pt", result.get(0).getMode());
		assertEquals(2.5, result.get(0).getUtility(), 1e-3);
		
		// Test 3
		estimator.setAlternative("car", -1.0);
		estimator.setAlternative("pt", -1.5);
		estimator.setAlternative("walk", -0.9);

		result = model.chooseModes(null, trips, new Random(0));
		assertEquals(1, result.size());
		assertEquals("walk", result.get(0).getMode());
		assertEquals(-0.9, result.get(0).getUtility(), 1e-3);
	}
}
