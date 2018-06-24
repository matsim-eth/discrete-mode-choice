package ch.ethz.matsim.mode_choice.v2.constraints;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.utils.InitialVehicleLinkFinder;

public class VehicularTripConstraint implements TripConstraint {
	final private Map<String, Id<Link>> currentLinkIds = new HashMap<>();
	final private Collection<String> constrainedModes;
	final private List<ModeChoiceTrip> trips;

	private int currentIndex;

	public VehicularTripConstraint(List<ModeChoiceTrip> trips, Collection<String> constrainedModes,
			Map<String, Id<Link>> initialLinkIds) {
		this.currentLinkIds.putAll(initialLinkIds);
		this.trips = trips;
		this.currentIndex = 0;
		this.constrainedModes = constrainedModes;
	}

	@Override
	public boolean validateBeforeEstimation(String mode) {
		if (constrainedModes.contains(mode)) {
			Id<Link> originLinkId = trips.get(currentIndex).getTripInformation().getOriginActivity().getLinkId();

			if (!currentLinkIds.containsKey(mode)) {
				currentLinkIds.put(mode, originLinkId);
			}

			return originLinkId.equals(currentLinkIds.get(mode));
		}

		return true;
	}

	@Override
	public boolean validateAfterEstimation(TripCandidate candidate) {
		return true;
	}

	@Override
	public void acceptCandidate(TripCandidate candidate) {
		if (constrainedModes.contains(candidate.getMode())) {
			currentLinkIds.put(candidate.getMode(),
					trips.get(currentIndex).getTripInformation().getDestinationActivity().getLinkId());
		}

		currentIndex++;
	}

	public static class Factory implements TripConstraintFactory {
		final private Collection<String> constrainedModes;
		final private Optional<InitialVehicleLinkFinder> linkFinder;

		public Factory(Collection<String> constrainedModes) {
			this.constrainedModes = constrainedModes;
			this.linkFinder = Optional.empty();
		}

		public Factory(Collection<String> constrainedModes, InitialVehicleLinkFinder linkFinder) {
			this.constrainedModes = constrainedModes;
			this.linkFinder = Optional.of(linkFinder);
		}

		@Override
		public TripConstraint createConstraint(List<ModeChoiceTrip> trips, Collection<String> availableModes) {
			Map<String, Id<Link>> initialLinkIds = new HashMap<>();

			if (linkFinder.isPresent()) {
				for (String mode : constrainedModes) {
					linkFinder.get().findInitialLinkId(mode, trips).ifPresent(l -> initialLinkIds.put(mode, l));
				}
			}

			return new VehicularTripConstraint(trips, constrainedModes, initialLinkIds);
		}
	}
}
