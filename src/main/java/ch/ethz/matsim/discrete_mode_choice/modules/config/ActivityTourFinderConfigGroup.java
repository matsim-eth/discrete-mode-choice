package ch.ethz.matsim.discrete_mode_choice.modules.config;

public class ActivityTourFinderConfigGroup extends ComponentConfigGroup {
	public ActivityTourFinderConfigGroup(String componentType, String componentName) {
		super("TourFinder", "ActivityBased");
	}
	
	public String getActivityType() {
		return activityType;
	}
}
