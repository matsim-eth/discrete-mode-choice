package ch.ethz.matsim.discrete_mode_choice.components.estimators;

import java.util.List;

import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.FacilitiesUtils;
import org.matsim.facilities.Facility;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripEstimator;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.DefaultRoutedTripCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This is an abstract estimator class that makes it easy to rely on MATSim's
 * TripRouter. Instead of just getting a proposed mode, this class already
 * routes the trip with the given mode in the background. All that remains is to
 * analyze the PlanElements to estimate a utility.
 * 
 * @author sebhoerl
 */
public abstract class AbstractTripRouterEstimator implements TripEstimator {
	private final TripRouter tripRouter;
	private final ActivityFacilities facilities;

	public AbstractTripRouterEstimator(TripRouter tripRouter, ActivityFacilities facilities) {
		this.tripRouter = tripRouter;
		this.facilities = facilities;
	}

	@Override
	public final TripCandidate estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips) {
		// I) Find the correct origin and destination facilities
		
		Facility originFacility = FacilitiesUtils.toFacility(trip.getOriginActivity(), facilities);
		Facility destinationFacility = FacilitiesUtils.toFacility(trip.getDestinationActivity(), facilities);
		
		//Facility originFacility = getFacilityForActivity(trip.getOriginActivity());
		//Facility destinationFacility = getFacilityForActivity(trip.getDestinationActivity());

		// II) Perform the routing
		List<? extends PlanElement> elements = tripRouter.calcRoute(mode, originFacility, destinationFacility,
				trip.getDepartureTime(), person);

		// III) Perform utility estimation
		return estimateTripCandidate(person, mode, trip, previousTrips, elements);
	}

	/**
	 * Implement this if you just want to calculate a utility, but don't want to
	 * return a custom TripCandidate object.
	 */
	protected double estimateTrip(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		return 0.0;
	}

	/**
	 * Implement this if you want to return a custom TripCandidate object rather
	 * than just a utility.
	 */
	protected TripCandidate estimateTripCandidate(Person person, String mode, DiscreteModeChoiceTrip trip,
			List<TripCandidate> previousTrips, List<? extends PlanElement> routedTrip) {
		double utility = estimateTrip(person, mode, trip, previousTrips, routedTrip);
		return new DefaultRoutedTripCandidate(utility, mode, routedTrip);
	}
}
