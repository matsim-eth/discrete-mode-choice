package ch.ethz.matsim.discrete_mode_choice.components.constraints;

import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourCandidate;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraint;
import ch.ethz.matsim.discrete_mode_choice.model.tour_based.TourConstraintFactory;

public class VehicleTourConstraint implements TourConstraint {
	final private List<DiscreteModeChoiceTrip> trips;
	final private Id<Link> homeLinkId;

	private Collection<String> requireStartAtHome;
	private Collection<String> requireContinuity;
	private Collection<String> requireEndAtHome;
	private boolean requireExistingHome;

	public VehicleTourConstraint(List<DiscreteModeChoiceTrip> trips, Id<Link> homeLinkId,
			Collection<String> requireStartAtHome, Collection<String> requireContinuity,
			Collection<String> requireEndAtHome, boolean requireExistingHome) {
		this.trips = trips;
		this.homeLinkId = homeLinkId;
		this.requireStartAtHome = requireStartAtHome;
		this.requireContinuity = requireContinuity;
		this.requireEndAtHome = requireEndAtHome;
		this.requireExistingHome = requireExistingHome;
	}

	private int getFirstLinkId(String mode, List<String> modes) {
		for (int i = 0; i < modes.size(); i++) {
			if (modes.get(i).equals(mode)) {
				return i;
			}
		}

		return -1;
	}

	private int getLastLinkId(String mode, List<String> modes) {
		for (int i = trips.size(); i >= 0; i--) {
			if (modes.get(i).equals(mode)) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public boolean validateBeforeEstimation(List<String> modes, List<List<String>> previousModes) {
		for (String testMode : requireStartAtHome) {
			if (modes.contains(testMode)) {
				int firstIndex = getFirstLinkId(testMode, modes);

				if (requireStartAtHome.contains(testMode)
						&& !trips.get(firstIndex).getOriginActivity().getLinkId().equals(homeLinkId)) {
					if (homeLinkId != null || requireExistingHome) {
						return false;
					}
				}

				int lastIndex = getLastLinkId(testMode, modes);

				if (requireEndAtHome.contains(testMode)
						&& !trips.get(lastIndex).getDestinationActivity().getLinkId().equals(homeLinkId)) {
					if (homeLinkId != null || requireExistingHome) {
						return false;
					}
				}

				if (requireContinuity.contains(testMode)) {

					Id<Link> currentLinkId = trips.get(firstIndex).getDestinationActivity().getLinkId();

					for (int index = firstIndex + 1; index <= lastIndex; index++) {
						if (modes.get(index).equals(testMode)) {
							DiscreteModeChoiceTrip trip = trips.get(index);

							if (!currentLinkId.equals(trip.getOriginActivity().getLinkId())) {
								return false;
							}

							currentLinkId = trip.getDestinationActivity().getLinkId();
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

		public Factory(Collection<String> requireStartAtHome, Collection<String> requireContinuity,
				Collection<String> requireEndAtHome, boolean requireExistingHome) {
			this.requireStartAtHome = requireStartAtHome;
			this.requireContinuity = requireContinuity;
			this.requireEndAtHome = requireEndAtHome;
			this.requireExistingHome = requireExistingHome;
		}

		@Override
		public TourConstraint createConstraint(Person person, List<DiscreteModeChoiceTrip> trips,
				Collection<String> availableModes) {
			return new VehicleTourConstraint(trips, getHomeLinkId(trips), requireStartAtHome, requireContinuity,
					requireEndAtHome, requireExistingHome);
		}

		private Id<Link> getHomeLinkId(List<DiscreteModeChoiceTrip> trips) {
			for (DiscreteModeChoiceTrip trip : trips) {
				if (trip.getOriginActivity().getType().equals("home")) {
					return trip.getOriginActivity().getLinkId();
				}

				if (trip.getDestinationActivity().getType().equals("home")) {
					return trip.getDestinationActivity().getLinkId();
				}
			}

			return null;
		}
	}
}
