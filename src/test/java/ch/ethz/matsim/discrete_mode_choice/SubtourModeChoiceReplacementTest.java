package ch.ethz.matsim.discrete_mode_choice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.algorithms.ChooseRandomLegModeForSubtour;
import org.matsim.core.population.algorithms.PermissibleModesCalculator;
import org.matsim.core.population.algorithms.PermissibleModesCalculatorImpl;
import org.matsim.core.replanning.modules.SubtourModeChoice;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;

import ch.ethz.matsim.discrete_mode_choice.components.constraints.SubtourModeConstraint;
import ch.ethz.matsim.discrete_mode_choice.components.constraints.VehicleTourConstraint;
import ch.ethz.matsim.discrete_mode_choice.components.estimators.UniformTourEstimator;
import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.PlanTourFinder;
import ch.ethz.matsim.discrete_mode_choice.components.tour_finder.TourFinder;
import ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder.FirstActivityHomeFinder;
import ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.FallbackBehaviour;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.NoFeasibleChoiceException;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.constraints.CompositeTourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.filters.CompositeTourFilter;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.CarModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.DefaultModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_availability.ModeAvailability;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.DefaultModeChainGenerator;
import ch.ethz.matsim.discrete_mode_choice.model.mode_chain.ModeChainGeneratorFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourBasedModel;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourFilter;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.RandomSelector;
import ch.ethz.matsim.discrete_mode_choice.model.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.discrete_mode_choice.test_utils.PlanBuilder;
import ch.ethz.matsim.discrete_mode_choice.test_utils.PlanTester;

public class SubtourModeChoiceReplacementTest {
	@Test
	public void testChoiceSet() throws NoFeasibleChoiceException {
		List<String> modes = Arrays.asList("walk", "pt");
		List<String> constrainedModes = Arrays.asList();
		boolean considerCarAvailability = true;
		int samples = 1000;

		Set<List<String>> dmcChains;
		Set<List<String>> smcChains;
		PlanBuilder planBuilder;

		// Test I) Simple plan with one tour

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Two tours

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Three tours
		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		samples = 5000;
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);
	}

	@Test
	public void testConstrainedChoiceSet() throws NoFeasibleChoiceException {
		List<String> modes = Arrays.asList("walk", "car");
		List<String> constrainedModes = Arrays.asList("car");
		boolean considerCarAvailability = true;
		int samples = 1000;

		Set<List<String>> dmcChains;
		Set<List<String>> smcChains;
		PlanBuilder planBuilder;

		// Test I) Simple plan with one tour

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Two tours

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Three tours
		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		samples = 5000;
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);
	}

	@Test
	public void testLargerCase() throws NoFeasibleChoiceException {
		List<String> modes = Arrays.asList("walk", "car", "pt", "bike");
		List<String> constrainedModes = Arrays.asList("car", "bike");
		boolean considerCarAvailability = true;
		int samples = 1000;

		Set<List<String>> dmcChains;
		Set<List<String>> smcChains;
		PlanBuilder planBuilder;

		// Test I) Simple plan with one tour

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Two tours

		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);

		// Test II) Three tours
		planBuilder = new PlanBuilder() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A") //
				.addLeg() //
				.addActivityWithLinkId("home", "B") //
				.addLeg() //
				.addActivityWithLinkId("home", "C") //
				.addLeg() //
				.addActivityWithLinkId("home", "A");

		// Don't allow single legs
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, false, samples);

		assertEquals(dmcChains, smcChains);

		// Allow single legs
		samples = 5000;
		dmcChains = computeDMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		samples = 20000;
		smcChains = computeSMC(planBuilder, modes, constrainedModes, considerCarAvailability, true, samples);

		assertEquals(dmcChains, smcChains);
	}

	private Set<List<String>> computeDMC(PlanBuilder planBuilder, List<String> modes, List<String> constrainedModes,
			boolean considerCarAvailability, boolean allowSingleLegs, int samples) throws NoFeasibleChoiceException {
		TourEstimator estimator = new UniformTourEstimator();
		ModeAvailability modeAvailability = considerCarAvailability ? new CarModeAvailability(modes)
				: new DefaultModeAvailability(modes);
		TourFinder tourFinder = new PlanTourFinder();
		UtilitySelectorFactory<TourCandidate> selectorFactory = new RandomSelector.Factory<>();
		ModeChainGeneratorFactory modeChainGeneratorFactory = new DefaultModeChainGenerator.Factory();
		FallbackBehaviour fallbackBehaviour = FallbackBehaviour.EXCEPTION;

		HomeFinder homeFinder = new FirstActivityHomeFinder();
		TourConstraintFactory vehicleConstraintFactory = new VehicleTourConstraint.Factory(constrainedModes,
				homeFinder);

		TourConstraintFactory subtourModeChoiceConstraintFactory = new SubtourModeConstraint.Factory(
				allowSingleLegs ? constrainedModes : modes);

		CompositeTourConstraintFactory constraintFactory = new CompositeTourConstraintFactory();
		constraintFactory.addFactory(vehicleConstraintFactory);
		constraintFactory.addFactory(subtourModeChoiceConstraintFactory);

		TourFilter tourFilter = new CompositeTourFilter(Collections.emptySet());

		DiscreteModeChoiceModel model = new TourBasedModel(estimator, modeAvailability, constraintFactory, tourFinder,
				tourFilter, selectorFactory, modeChainGeneratorFactory, fallbackBehaviour);

		Plan plan = planBuilder.buildPlan();
		List<DiscreteModeChoiceTrip> trips = planBuilder.buildDiscreteModeChoiceTrips();
		Random random = new Random(0);

		Set<List<String>> chains = new HashSet<>();

		for (int i = 0; i < samples; i++) {
			List<TripCandidate> result = model.chooseModes(plan.getPerson(), trips, random);
			chains.add(PlanTester.getModeChain(result));
		}

		return chains;
	}

	private Set<List<String>> computeSMC(PlanBuilder planBuilder, List<String> modes, List<String> constrainedModes,
			boolean considerCarAvailability, boolean allowSingleLegs, int samples) {
		double singleLegProbability = allowSingleLegs ? 0.5 : 0.0;

		String[] availableModes = modes.toArray(new String[] {});
		String[] chainBasedModes = constrainedModes.toArray(new String[] {});

		MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();
		PermissibleModesCalculator permissibleModesCalculator = new PermissibleModesCalculatorImpl(availableModes,
				considerCarAvailability);
		Random rng = new Random(0);
		SubtourModeChoice.Behavior behavior = SubtourModeChoice.Behavior.fromSpecifiedModesToSpecifiedModes;

		ChooseRandomLegModeForSubtour smc = new ChooseRandomLegModeForSubtour(mainModeIdentifier,
				permissibleModesCalculator, availableModes, chainBasedModes, rng, behavior, singleLegProbability);

		Set<List<String>> chains = new HashSet<>();
		Plan plan = planBuilder.buildPlan();

		for (int i = 0; i < samples; i++) {
			smc.run(plan);
			chains.add(PlanTester.getModeChain(plan));
		}

		return chains;
	}
}
