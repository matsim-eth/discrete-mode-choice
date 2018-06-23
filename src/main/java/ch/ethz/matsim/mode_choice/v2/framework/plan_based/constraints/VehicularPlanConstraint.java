package ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.router.TripStructureUtils.Trip;

public class VehicularPlanConstraint {
	final private Map<String, Id<Link>> initialLinkIdsByMode;
	final private Collection<String> constrainedModes;
	final private List<Trip> trips;
	final private boolean inferInitialLinks;

	public VehicularPlanConstraint(List<Trip> trips, Collection<String> contrainedModes,
			Map<String, Id<Link>> initialLinkIdsByMode, boolean inferInitialLinks) {
		this.constrainedModes = contrainedModes;
		this.inferInitialLinks = inferInitialLinks;
		this.trips = trips;
		this.initialLinkIdsByMode = initialLinkIdsByMode;
	}

	public boolean validate(List<String> modes) {
		Map<String, Id<Link>> currentLinkIdsByMode = new HashMap<>(initialLinkIdsByMode);

		Iterator<String> modeIterator = modes.iterator();
		Iterator<Trip> tripIterator = trips.iterator();

		while (modeIterator.hasNext() || tripIterator.hasNext()) {
			String mode = modeIterator.next();
			Trip trip = tripIterator.next();

			if (constrainedModes.contains(mode)) {
				if (!currentLinkIdsByMode.containsKey(mode)) {
					if (inferInitialLinks) {
						currentLinkIdsByMode.put(mode, trip.getOriginActivity().getLinkId());
					} else {
						throw new IllegalStateException(String.format("No initial link given for mode '%s'", mode));
					}
				}

				Id<Link> currentLinkId = currentLinkIdsByMode.get(mode);

				if (!trip.getOriginActivity().getLinkId().equals(currentLinkId)) {
					return false;
				}
			}
		}

		return true;
	}
}
