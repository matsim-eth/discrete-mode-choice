package ch.ethz.matsim.discrete_mode_choice.examples;

import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;

import com.google.inject.Inject;

import ch.ethz.matsim.discrete_mode_choice.components.estimators.AbstractTripRouterEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

public class MyTripEstimator extends AbstractTripRouterEstimator {
	@Inject
	public MyTripEstimator(TripRouter tripRouter, Network network, ActivityFacilities facilities) {
		super(tripRouter, network, facilities);
	}

	@Override
	protected double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		// I) Calculate total travel time
		double totalTravelTime = 0.0;

		for (PlanElement element : routedTrip) {
			if (element instanceof Leg) {
				Leg leg = (Leg) element;
				totalTravelTime += leg.getTravelTime();
			}
		}

		// II) Attach mode-specific factor

		double beta = 0.0;

		switch (mode) {
		case TransportMode.car:
			beta = 1.0;
			break;
		}

		double utility = beta * totalTravelTime;
		return utility;
	}
}
