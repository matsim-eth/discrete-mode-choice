package ch.ethz.matsim.discrete_mode_choice.replanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.router.TripRouter;
import org.matsim.core.utils.misc.Time;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel.NoFeasibleChoiceException;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.RoutedTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This replanning algorithm uses a predefined discrete mode choice model to
 * perform mode decisions for a given plan.
 * 
 * @author sebhoerl
 */
public class DiscreteModeChoiceAlgorithm implements PlanAlgorithm {
	private final static Logger logger = Logger.getLogger(DiscreteModeChoiceAlgorithm.class);

	private final Random random;
	private final DiscreteModeChoiceModel modeChoiceModel;

	public DiscreteModeChoiceAlgorithm(Random random, DiscreteModeChoiceModel modeChoiceModel) {
		this.random = random;
		this.modeChoiceModel = modeChoiceModel;
	}

	@Override
	/**
	 * Performs mode choice on a plan. We assume that TripsToLegs has been called
	 * before, hence the code is working diretly on legs.
	 */
	public void run(Plan plan) {
		// I) First build a list of DiscreteModeChoiceTrips

		List<? extends PlanElement> elements = plan.getPlanElements();
		List<DiscreteModeChoiceTrip> trips = new ArrayList<>((elements.size() - 2) / 2);
		List<Leg> legs = new ArrayList<>((elements.size() - 2) / 2);

		double time = 0.0;

		for (int i = 1; i < elements.size() - 1; i += 2) {
			Activity originActivity = (Activity) elements.get(i - 1);
			Leg leg = (Leg) elements.get(i);
			Activity destinationActivity = (Activity) elements.get(i + 1);

			if (!Time.isUndefinedTime(originActivity.getEndTime())) {
				time = originActivity.getEndTime();
			} else {
				logger.warn(String.format(
						"Found origin activity with undefined end time in agent %s. Falling back to previous end time or midnight.",
						plan.getPerson().getId().toString()));
			}

			trips.add(new DiscreteModeChoiceTrip(originActivity, destinationActivity, leg.getMode(), time,
					plan.getPerson().hashCode(), i));
			legs.add(leg);
		}

		// II) Run mode choice

		try {
			// Perform mode choice and retrieve candidates
			List<TripCandidate> chosenCandidates = modeChoiceModel.chooseModes(plan.getPerson(), trips, random);

			for (int i = 0; i < trips.size(); i++) {
				TripCandidate candidate = chosenCandidates.get(i);

				// Set new mode of the leg
				Leg targetLeg = legs.get(i);
				targetLeg.setMode(candidate.getMode());

				// But alternatively put the whole routed plan segment if routing has been
				// performed in the choice
				if (candidate instanceof RoutedTripCandidate) {
					DiscreteModeChoiceTrip trip = trips.get(i);
					RoutedTripCandidate routedCandidate = (RoutedTripCandidate) candidate;
					List<? extends PlanElement> routedElements = routedCandidate.getRoutedPlanElements();

					TripRouter.insertTrip(plan, trip.getOriginActivity(), routedElements,
							trip.getDestinationActivity());
				} else {
					targetLeg.setRoute(null);
				}
			}
		} catch (NoFeasibleChoiceException e) {
			throw new IllegalStateException(e);
		}
	}
}
