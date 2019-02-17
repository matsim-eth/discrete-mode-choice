package ch.ethz.matsim.discrete_mode_choice.components.constraints;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.components.utils.LocationUtils;
import ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.TripConstraintFactory;
import ch.ethz.matsim.discrete_mode_choice.model.trip_based.candidates.TripCandidate;

/**
 * This is a vehicle constraint (see VehicleTourConstraint), but on a trip
 * level. Here the case is more difficult than on the tour-level and not all of
 * the dynamic can be enforced. Have a look at the code for the exact
 * implementation.
 * 
 * Attention! This is not tested and may be faulty. Feel free to add some test
 * cases to see if this actually does what it is supposed to do.
 * 
 * TODO: Revise this and check if it makes sense!
 * 
 * @author sebhoerl
 */
public class VehicleTripConstraint implements TripConstraint {
	private final static Logger logger = Logger.getLogger(VehicleTripConstraint.class);

	private final List<DiscreteModeChoiceTrip> trips;

	private Collection<String> requireStartAtHome;
	private Collection<String> requireContinuity;
	private Collection<String> requireEndAtHome;
	private boolean requireExistingHome;

	private Id<? extends BasicLocation> homeLocationId;

	public VehicleTripConstraint(List<DiscreteModeChoiceTrip> trips, Id<? extends BasicLocation> homeLocationId,
			Collection<String> requireStartAtHome, Collection<String> requireContinuity,
			Collection<String> requireEndAtHome, boolean requireExistingHome) {
		this.trips = trips;
		this.homeLocationId = homeLocationId;
		this.requireStartAtHome = requireStartAtHome;
		this.requireEndAtHome = requireEndAtHome;
		this.requireContinuity = requireContinuity;
		this.requireExistingHome = requireExistingHome;
	}

	private Id<? extends BasicLocation> getCurrentLocationId(String mode, List<String> previousModes) {
		for (int i = previousModes.size() - 1; i >= 0; i--) {
			if (previousModes.get(i).equals(mode)) {
				return LocationUtils.getLocationId(trips.get(i).getDestinationActivity());
			}
		}

		return null;
	}

	private boolean canReturnHome(List<String> previousModes) {
		for (int index = previousModes.size(); index < trips.size(); index++) {
			if (trips.get(index).getDestinationActivity().getLinkId().equals(homeLocationId)) {
				return true;
			}
		}

		if (homeLocationId != null || requireExistingHome) {
			return false;
		} else {
			return true;
		}
	}

	private boolean willReturn(Id<? extends BasicLocation> locationId, List<String> previousModes) {
		for (int index = previousModes.size(); index < trips.size(); index++) {
			DiscreteModeChoiceTrip trip = trips.get(index);

			if (LocationUtils.getLocationId(trip.getDestinationActivity()).equals(locationId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean validateBeforeEstimation(DiscreteModeChoiceTrip trip, String mode, List<String> previousModes) {
		// First, we check whether we start using a restricted mode out of home although
		// we require to start at home
		if (requireStartAtHome.contains(mode)) {
			boolean isFirst = !previousModes.contains(mode);

			if (isFirst && !LocationUtils.getLocationId(trip.getOriginActivity()).equals(homeLocationId)) {
				// The trip is the first of the restricted mode, but we're not home!

				if (homeLocationId != null || requireExistingHome) {
					return false;
				}
			}
		}

		// Second, we make sure we are only using a restricted mode at a location where
		// it has been moved to before.
		if (requireContinuity.contains(mode)) {
			Id<? extends BasicLocation> currentLinkId = getCurrentLocationId(mode, previousModes);

			if (currentLinkId != null) { // We have moved the vehicle already
				if (currentLinkId.equals(LocationUtils.getLocationId(trip.getOriginActivity()))) {
					// But the vehicle is not where we're currently trying to depart
					return false;
				}
			}
		}

		// Third, we look at the requirement to end at home. This is tricky, especially
		// whith multiple modes. With one mode
		// it is straightforward: We can only go on a trip with that mode if we will
		// ever arrive back home. And we need to
		// make sure that when we are on a trip with that mode we don't choose any other
		// mode until we are home.
		// I'm not sure how easy it is to generalized this to multiple modes that can
		// float around anywhere in the network. For now, we restrict the use of one
		// active mode. Active here means that a mode has been moved away from its
		// starting position.
		if (requireEndAtHome.size() > 0) {
			// Make sure we can go back home
			if (requireEndAtHome.contains(mode)) {
				if (!canReturnHome(previousModes)) {
					return false;
				}
			}

			String activeMode = null;
			Id<? extends BasicLocation> currentActiveModeLocationId = null;

			for (String restrictedMode : requireEndAtHome) {
				Id<? extends BasicLocation> currentLocationId = getCurrentLocationId(restrictedMode, previousModes);

				if (currentLocationId != null && !currentLocationId.equals(homeLocationId)) {
					// Vehicle has been moved and is out of home
					activeMode = restrictedMode;
					currentActiveModeLocationId = currentLocationId;
					break;
				}
			}

			if (activeMode != null && !activeMode.equals(mode)) {
				// There is an active mode, otherwise we can do what we want
				// And here we check the case where we already know that we want to use
				// something else than active mode

				if (requireEndAtHome.contains(mode)) {
					// If the proposal is another constrained mode, we forbid to use it here,
					// because we're already
					// on the road with activeMode
					return false;
				}

				if (!willReturn(currentActiveModeLocationId, previousModes)) {
					// In case we are able to return to the current location, we can do some walking
					// or similar in between, becasue we know that we will have a chance later to
					// pick up the vehicle again. However, if we do not return to the current
					// location we cannot bring it back.
					return false; // Here we are enforcing the active mode
				}
			}
		}

		// If none of the constraints catched some infeasible situation, the mode
		// proposal is fine!
		return true;
	}

	@Override
	public boolean validateAfterEstimation(DiscreteModeChoiceTrip trip, TripCandidate candidate,
			List<TripCandidate> previousCandidates) {
		return true;
	}

	static public class Factory implements TripConstraintFactory {
		private Collection<String> requireStartAtHome;
		private Collection<String> requireContinuity;
		private Collection<String> requireEndAtHome;
		private boolean requireExistingHome;
		private final HomeFinder homeFinder;

		public Factory(Collection<String> requireStartAtHome, Collection<String> requireContinuity,
				Collection<String> requireEndAtHome, boolean requireExistingHome, HomeFinder homeFinder) {
			this.requireStartAtHome = requireStartAtHome;
			this.requireContinuity = requireContinuity;
			this.requireEndAtHome = requireEndAtHome;
			this.requireExistingHome = requireExistingHome;
			this.homeFinder = homeFinder;
		}

		@Override
		public TripConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> trips,
				Collection<String> availableModes) {
			logger.warn("VehicleTripConstraint is not tested. Use at own risk!");

			return new VehicleTripConstraint(trips, homeFinder.getHomeLocationId(trips), requireStartAtHome,
					requireContinuity, requireEndAtHome, requireExistingHome);
		}
	}
}
