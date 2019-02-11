package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.Facility;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultRoutedTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

public abstract class AbstractTripRouterEstimator implements TripEstimator {
	private final TripRouter tripRouter;
	private final Network network;
	private final ActivityFacilities facilities;

	public AbstractTripRouterEstimator(TripRouter tripRouter, Network network, ActivityFacilities facilities) {
		this.tripRouter = tripRouter;
		this.network = network;
		this.facilities = facilities;
	}

	@Override
	public final TripCandidate estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips) {
		// I) Find the correct origin and destination facilities
		Facility originFacility = facilities.getFacilities().get(trip.getOriginActivity().getFacilityId());

		if (originFacility == null) {
			originFacility = new LinkWrapperFacility(network.getLinks().get(trip.getOriginActivity().getLinkId()));
		}

		Facility destinationFacility = facilities.getFacilities().get(trip.getDestinationActivity().getFacilityId());

		if (destinationFacility == null) {
			destinationFacility = new LinkWrapperFacility(
					network.getLinks().get(trip.getDestinationActivity().getLinkId()));
		}

		// II) Perform the routing
		List<? extends PlanElement> elements = tripRouter.calcRoute(mode, originFacility, destinationFacility,
				trip.getDepartureTime(), person);

		// III) Perform utility estimation
		return estimateTripCandidate(person, mode, trip, previousTrips, elements);
	}

	protected double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		return 0.0;
	}

	protected TripCandidate estimateTripCandidate(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		double utility = estimateTrip(person, mode, trip, previousTrips, routedTrip);
		return new DefaultRoutedTripCandidate(utility, mode, routedTrip);
	}
}
