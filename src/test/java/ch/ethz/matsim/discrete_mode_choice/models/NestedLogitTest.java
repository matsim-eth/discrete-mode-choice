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
import ch.ethz.matsim.discrete_mode_choice.model.nested.DefaultNest;
import ch.ethz.matsim.discrete_mode_choice.model.nested.DefaultNestStructure;
import ch.ethz.matsim.discrete_mode_choice.model.nested.NestedLogitSelector;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TripFilter;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;

public class NestedLogitTest {
	@Test
	public void testRedBusBlueBus() throws NoFeasibleChoiceException {
		TripFilter tripFilter = new CompositeTripFilter(Collections.emptySet());
		ModeAvailability modeAvailability = new DefaultModeAvailability(
				Arrays.asList("car", "redbus", "bluebus"));
		TripConstraintFactory constraintFactory = new CompositeTripConstraintFactory();
		FallbackBehaviour fallbackBehaviour = FallbackBehaviour.EXCEPTION;
		ConstantTripEstimator estimator = new ConstantTripEstimator();

		double minimumUtility = Double.NEGATIVE_INFINITY;
		double maximumUtility = Double.POSITIVE_INFINITY;

		DefaultNestStructure structure = new DefaultNestStructure();
		DefaultNest ptNest = new DefaultNest("pt", 1.0);
		structure.addNest(structure.getRoot(), ptNest);

		UtilitySelectorFactory selectorFactory = new NestedLogitSelector.Factory(structure, minimumUtility,
				maximumUtility);

		List<DiscreteModeChoiceTrip> trips = Collections
				.singletonList(new DiscreteModeChoiceTrip(null, null, null, null, 0.0, 0, 0));

		TripBasedModel model = new TripBasedModel(estimator, tripFilter, modeAvailability, constraintFactory,
				selectorFactory, fallbackBehaviour);
		Map<String, Integer> choices = new HashMap<>();
		Random random = new Random(0);

		int numberOfSamples = 10000;

		estimator.setAlternative("car", -1.0, structure.getRoot());
		estimator.setAlternative("redbus", -1.0, ptNest);
		estimator.setAlternative("bluebus", -1.0, ptNest);
		estimator.setAlternative("walk", -1.0, structure.getRoot());

		{
			ptNest.setScaleParameter(1.0); // NO nesting in principle
			choices.clear();

			for (int i = 0; i < numberOfSamples; i++) {
				List<TripCandidate> result = model.chooseModes(null, trips, random);
				String mode = result.get(0).getMode();
				choices.put(mode, choices.getOrDefault(mode, 0) + 1);
			}

			assertEquals(0.33, (double) choices.get("car") / numberOfSamples, 1e-2);
			assertEquals(0.33, (double) choices.get("redbus") / numberOfSamples, 1e-2);
			assertEquals(0.33, (double) choices.get("bluebus") / numberOfSamples, 1e-2);
		}

		{
			ptNest.setScaleParameter(20.0); // Strong nesting
			choices.clear();

			for (int i = 0; i < numberOfSamples; i++) {
				List<TripCandidate> result = model.chooseModes(null, trips, random);
				String mode = result.get(0).getMode();
				choices.put(mode, choices.getOrDefault(mode, 0) + 1);
			}

			assertEquals(0.5, (double) choices.get("car") / numberOfSamples, 1e-2);
			assertEquals(0.25, (double) choices.get("redbus") / numberOfSamples, 1e-2);
			assertEquals(0.25, (double) choices.get("bluebus") / numberOfSamples, 1e-2);
		}
	}
}
