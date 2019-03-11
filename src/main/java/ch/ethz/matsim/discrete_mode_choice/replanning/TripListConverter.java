package ch.ethz.matsim.discrete_mode_choice.replanning;

import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PlanElement;
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
	public static void convert(Plan plan, List<DiscreteModeChoiceTrip> trips, List<Leg> legs) {
		List<? extends PlanElement> elements = plan.getPlanElements();

		double time = 0.0;

		for (int i = 1; i < elements.size() - 1; i += 2) {
			Activity originActivity = (Activity) elements.get(i - 1);
			Leg leg = (Leg) elements.get(i);
			Activity destinationActivity = (Activity) elements.get(i + 1);

			if (!Time.isUndefinedTime(originActivity.getEndTime())) {
				time = originActivity.getEndTime();
			} else if (!Time.isUndefinedTime(originActivity.getMaximumDuration())) {
				time += originActivity.getMaximumDuration();
			} else {
				logger.warn(String.format(
						"Found origin activity with invalid end time and maximum duration in agent %s. Falling back to %s.",
						plan.getPerson().getId().toString(), Time.writeTime(time)));
			}

			trips.add(new DiscreteModeChoiceTrip(originActivity, destinationActivity, leg.getMode(), time,
					plan.getPerson().hashCode(), i));
			legs.add(leg);

			// Either this will be reset by the end time of the next origin activity, or we
			// make sure that we move forward in time (if all activities are
			// duration-based).
			time += leg.getTravelTime();
		}
	}
}
