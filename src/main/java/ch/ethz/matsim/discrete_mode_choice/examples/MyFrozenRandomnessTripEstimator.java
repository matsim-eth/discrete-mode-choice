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

public class MyFrozenRandomnessTripEstimator extends AbstractTripRouterEstimator {
	@Inject
	public MyFrozenRandomnessTripEstimator(TripRouter tripRouter, Network network, ActivityFacilities facilities) {
		super(tripRouter, network, facilities);
	}

	@Override
	protected double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		// I) Calculate total travel time
		double totalTravelTime = 0.0;
		double totalTravelDistance = 0.0;

		for (PlanElement element : routedTrip) {
			if (element instanceof Leg) {
				Leg leg = (Leg) element;
				totalTravelTime += leg.getTravelTime() / 3600.0;
				totalTravelDistance += leg.getRoute().getDistance() * 1e-3;
			}
		}
		
		// II) Compute mode-specific utility
		double utility = 0;

		switch (mode) {
		case TransportMode.car:
			utility = -0.562 - 0.4 * 0.062 * totalTravelDistance;
			break;
		case TransportMode.pt:
			utility = -0.124 - 0.18 * totalTravelTime;
			break;
		case TransportMode.walk:
			utility = -1.14 * totalTravelTime;
			break;
		}
		
		utility += (Double) trip.getOriginActivity().getAttributes().getAttribute("next_error_" + mode);

		return utility;
	}
}
