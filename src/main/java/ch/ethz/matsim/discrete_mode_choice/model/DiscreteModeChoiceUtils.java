package ch.ethz.matsim.discrete_mode_choice.model;

import java.util.List;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.config.Config;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.misc.Time;

public class DiscreteModeChoiceUtils {
	private DiscreteModeChoiceUtils() {
	}

	static public double advanceTime(List<? extends PlanElement> elements, double now, Config config) {
		for (PlanElement element : elements) {
			if (element instanceof Activity) {
				now = PopulationUtils.decideOnActivityEndTime((Activity) element, now, config);
			} else {
				now += PopulationUtils.decideOnTravelTimeForLeg((Leg) element);
			}
		}

		checkTime(now);
		return now;
	}

	static public void checkTime(double time) {
		if (Time.isUndefinedTime(time)) {
			throw new IllegalStateException("Trip departure time could not be reconstructed reliably.");
		}
	}
}
