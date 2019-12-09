package ch.ethz.matsim.discrete_mode_choice.replanning;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;
import org.matsim.core.utils.misc.Time;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

/**
 * Helper class for converting a MATSim plan into a list of
 * DiscreteModeChoiceTrip.
 * 
 * @author sebhoerl
 */
public final class TripListConverter {
	private final static Logger logger = Logger.getLogger(TripListConverter.class);

	private TripListConverter() {

	}

	/**
	 * Convert a MATSim plan into a list of DiscreteModeChoiceTrip and extract the
	 * respective legs. It is expected that the plan is already flattened (i.e.
	 * there are no interaction activities).
	 */
	public static List<DiscreteModeChoiceTrip> convert(Plan plan, MainModeIdentifier mainModeIdentifier) {
		List<Trip> initialTrips = TripStructureUtils.getTrips(plan);
		List<DiscreteModeChoiceTrip> trips = new ArrayList<>(initialTrips.size());

		double time = 0.0;
		int index = 0;

		for (Trip initialTrip : initialTrips) {
			Activity originActivity = initialTrip.getOriginActivity();
			Activity destinationActivity = initialTrip.getDestinationActivity();

			if (!Time.isUndefinedTime(originActivity.getEndTime())) {
				time = originActivity.getEndTime();
			} else if (!Time.isUndefinedTime(originActivity.getMaximumDuration())) {
				time += originActivity.getMaximumDuration();
			} else {
				logger.warn(String.format(
						"Found origin activity with invalid end time and maximum duration in agent %s. Falling back to %s.",
						plan.getPerson().getId().toString(), Time.writeTime(time)));
			}

			String initialMode = mainModeIdentifier.identifyMainMode(initialTrip.getTripElements());

			trips.add(new DiscreteModeChoiceTrip(originActivity, destinationActivity, initialMode,
					initialTrip.getTripElements(), time, plan.getPerson().hashCode(), index));
			index++;
		}

		return trips;
	}
}
