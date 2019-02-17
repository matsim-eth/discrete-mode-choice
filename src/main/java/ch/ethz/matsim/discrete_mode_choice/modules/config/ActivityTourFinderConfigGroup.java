package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the ActivityTourFinder.
 * 
 * @author sebhoerl
 */
public class ActivityTourFinderConfigGroup extends ComponentConfigGroup {
	private String activityType;

	public static final String ACTIVITY_TYPE = "activityType";

	public ActivityTourFinderConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
	}

	@Override
	public Map<String, String> getComments() {
		Map<String, String> comments = new HashMap<>();

		comments.put(ACTIVITY_TYPE,
				"Activity type which should be considered as start and end of a tour. If a plan does not start or end with such an activity additional tours are added.");

		return comments;
	}

	@StringGetter(ACTIVITY_TYPE)
	public String getActivityType() {
		return activityType;
	}

	@StringSetter(ACTIVITY_TYPE)
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
}
