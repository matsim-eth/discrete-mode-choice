package ch.ethz.matsim.mode_choice.v2.replanning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;

import ch.ethz.matsim.mode_choice.v2.framework.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel.NoFeasibleChoiceException;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceResult;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;

public class ModeChoiceModelAlgorithm implements PlanAlgorithm {
	private final Random random;
	private final ModeChoiceModel modeChoiceModel;

	public ModeChoiceModelAlgorithm(Random random, ModeChoiceModel modeChoiceModel) {
		this.random = random;
		this.modeChoiceModel = modeChoiceModel;
	}

	@Override
	public void run(Plan plan) {
		List<Trip> trips = TripStructureUtils.getTrips(plan, new StageActivityTypesImpl());
		List<ModeChoiceTrip> choiceTrips = new ArrayList<>(trips.size());

		for (Trip trip : trips) {
			String initialMode = trip.getLegsOnly().get(0).getMode();
			choiceTrips.add(new DefaultModeChoiceTrip(plan.getPerson(), trips, trip, initialMode));
		}

		try {
			ModeChoiceResult result = modeChoiceModel.chooseModes(choiceTrips, random);

			// TODO Here we can also set routes etc. directly!

			for (int i = 0; i < trips.size(); i++) {
				Leg leg = trips.get(i).getLegsOnly().get(0);
				TripCandidate candidate = result.getTripCandidates().get(i);

				leg.setMode(candidate.getMode());
				leg.setRoute(null);
			}
		} catch (NoFeasibleChoiceException e) {
			throw new RuntimeException(e);
		}
	}
}
