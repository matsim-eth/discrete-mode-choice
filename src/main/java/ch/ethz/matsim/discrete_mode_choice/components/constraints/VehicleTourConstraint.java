package ch.ethz.matsim.discrete_mode_choice.components.constraints;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.matsim.api.core.v01.BasicLocation;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.components.utils.LocationUtils;
import ch.ethz.matsim.discrete_mode_choice.components.utils.home_finder.HomeFinder;
import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;

public class VehicleTourConstraint implements TourConstraint {
	final private List<DiscreteModeChoiceTrip> trips;
	final private Id<? extends BasicLocation> homeLocationId;

	private Collection<String> requireStartAtHome;
	private Collection<String> requireContinuity;
	private Collection<String> requireEndAtHome;
	private boolean requireExistingHome;
	private Collection<String> testModes;

	public VehicleTourConstraint(List<DiscreteModeChoiceTrip> trips, Id<? extends BasicLocation> homeLocationId,
			Collection<String> requireStartAtHome, Collection<String> requireContinuity,
			Collection<String> requireEndAtHome, boolean requireExistingHome, Collection<String> testModes) {
		this.trips = trips;
		this.homeLocationId = homeLocationId;
		this.requireStartAtHome = requireStartAtHome;
		this.requireContinuity = requireContinuity;
		this.requireEndAtHome = requireEndAtHome;
		this.requireExistingHome = requireExistingHome;
		this.testModes = testModes;
	}

	private int getFirstIndex(String mode, List<String> modes) {
		for (int i = 0; i < modes.size(); i++) {
			if (modes.get(i).equals(mode)) {
				return i;
			}
		}

		return -1;
	}

	private int getLastIndex(String mode, List<String> modes) {
		for (int i = trips.size() - 1; i >= 0; i--) {
			if (modes.get(i).equals(mode)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> modes, List<List<String>> previousModes) {
		for (String testMode : testModes) {
			if (modes.contains(testMode)) {
				int firstIndex = getFirstIndex(testMode, modes);

				if (requireStartAtHome.contains(testMode) && !LocationUtils
						.getLocationId(trips.get(firstIndex).getOriginActivity()).equals(homeLocationId)) {
					if (homeLocationId != null || requireExistingHome) {
						return false;
					}
				}

				int lastIndex = getLastIndex(testMode, modes);

				if (requireEndAtHome.contains(testMode) && !LocationUtils
						.getLocationId(trips.get(lastIndex).getDestinationActivity()).equals(homeLocationId)) {
					if (homeLocationId != null || requireExistingHome) {
						return false;
					}
				}

				if (requireContinuity.contains(testMode)) {
					Id<? extends BasicLocation> currentLocationId = LocationUtils
							.getLocationId(trips.get(firstIndex).getDestinationActivity());

					for (int index = firstIndex + 1; index <= lastIndex; index++) {
						if (modes.get(index).equals(testMode)) {
							DiscreteModeChoiceTrip trip = trips.get(index);

							if (!currentLocationId.equals(LocationUtils.getLocationId(trip.getOriginActivity()))) {
								return false;
							}

							currentLocationId = LocationUtils.getLocationId(trip.getDestinationActivity());
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(TourCandidate candidate, List<TourCandidate> previousCandidates) {
		return true;
	}

	public static class Factory implements TourConstraintFactory {
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
		public TourConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> trips,
				Collection<String> availableModes) {
			Set<String> testModes = new HashSet<>();
			testModes.addAll(requireStartAtHome);
			testModes.addAll(requireEndAtHome);
			testModes.addAll(requireContinuity);

			return new VehicleTourConstraint(trips, homeFinder.getHomeLocationId(trips), requireStartAtHome,
					requireContinuity, requireEndAtHome, requireExistingHome, testModes);
		}
	}
}
