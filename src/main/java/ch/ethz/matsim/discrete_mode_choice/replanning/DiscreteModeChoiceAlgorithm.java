package ch.ethz.matsim.discrete_mode_choice.replanning;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.TripRouter;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.NoFeasibleChoiceException;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.RoutedTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This replanning algorithm uses a predefined discrete mode choice model to
 * perform mode decisions for a given plan.
 * 
 * @author sebhoerl
 */
public class DiscreteModeChoiceAlgorithm implements PlanAlgorithm {
	private final Random random;
	private final DiscreteModeChoiceModel modeChoiceModel;

	private final StageActivityTypes stageActivityTypes;
	private final MainModeIdentifier mainModeIdentifier;
	private final PopulationFactory populationFactory;

	public DiscreteModeChoiceAlgorithm(Random random, DiscreteModeChoiceModel modeChoiceModel,
			StageActivityTypes stageActivityTypes, MainModeIdentifier mainModeIdentifier, PopulationFactory populationFactory) {
		this.random = random;
		this.modeChoiceModel = modeChoiceModel;
		this.stageActivityTypes = stageActivityTypes;
		this.mainModeIdentifier = mainModeIdentifier;
		this.populationFactory = populationFactory;
	}

	@Override
	/**
	 * Performs mode choice on a plan. We assume that TripsToLegs has been called
	 * before, hence the code is working diretly on legs.
	 */
	public void run(Plan plan) {
		// I) First build a list of DiscreteModeChoiceTrips
		List<DiscreteModeChoiceTrip> trips = TripListConverter.convert(plan, stageActivityTypes, mainModeIdentifier);

		// II) Run mode choice

		try {
			// Perform mode choice and retrieve candidates
			List<TripCandidate> chosenCandidates = modeChoiceModel.chooseModes(plan.getPerson(), trips, random);

			for (int i = 0; i < trips.size(); i++) {
				DiscreteModeChoiceTrip trip = trips.get(i);
				TripCandidate candidate = chosenCandidates.get(i);

				List<? extends PlanElement> insertElements;
				
				if (candidate instanceof RoutedTripCandidate) {
					RoutedTripCandidate routedCandidate = (RoutedTripCandidate) candidate;
					insertElements = routedCandidate.getRoutedPlanElements();
				} else {
					Leg insertLeg = populationFactory.createLeg(candidate.getMode());
					insertElements = Collections.singletonList(insertLeg);
				}
				
				TripRouter.insertTrip(plan, trip.getOriginActivity(), insertElements, trip.getDestinationActivity());
			}
		} catch (NoFeasibleChoiceException e) {
			throw new IllegalStateException(e);
		}
	}
}
