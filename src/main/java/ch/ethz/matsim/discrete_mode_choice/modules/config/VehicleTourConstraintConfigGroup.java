package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Config group for VehicleTourConstraint.
 * 
 * @author sebhoerl
 *
 */
public class VehicleTourConstraintConfigGroup extends ComponentConfigGroup {
	private Collection<String> restrictedModes = new HashSet<>(Arrays.asList("car", "bike"));
	private HomeType homeType = HomeType.USE_FIRST_ACTIVITY;
	private String homeActivityType = "home";

	private static final String RESTRICTED_MODES = "restrictedModes";
	private static final String HOME_TYPE = "homeType";
	private static final String HOME_ACTIVITY_TYPE = "homeActivityType";

	public enum HomeType {
		USE_FIRST_ACTIVITY, USE_ACTIVITY_TYPE
	}

	public VehicleTourConstraintConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
	}

	@Override
	public Map<String, String> getComments() {
		Map<String, String> comments = new HashMap<>();

		comments.put(RESTRICTED_MODES,
				"Defines which modes must fulfill continuity constraints (can only be used where they have been brough to before)");

		String options = Arrays.asList(HomeType.values()).stream().map(String::valueOf)
				.collect(Collectors.joining(", "));
		comments.put(HOME_TYPE, "Defines how to determine where the home of an agent is: " + options);

		comments.put(HOME_ACTIVITY_TYPE,
				"If USE_ACTIVITY_TYPE is chosen for homeType, this option defines which activity type to look for.");

		return comments;
	}

	public void setRestrictedModes(Collection<String> restrictedModes) {
		this.restrictedModes = new HashSet<>(restrictedModes);
	}

	@StringSetter(RESTRICTED_MODES)
	public void setRestrictedModesAsString(String restrictedModes) {
		this.restrictedModes = new HashSet<>(
				Arrays.asList(restrictedModes.split(",")).stream().map(String::trim).collect(Collectors.toSet()));
	}

	public Collection<String> getRestrictedModes() {
		return restrictedModes;
	}

	@StringGetter(RESTRICTED_MODES)
	public String getRestrictedModesAsString() {
		return String.join(", ", restrictedModes);
	}

	@StringGetter(HOME_ACTIVITY_TYPE)
	public String getHomeActivityType() {
		return homeActivityType;
	}

	@StringSetter(HOME_ACTIVITY_TYPE)
	public void setHomeActivityType(String homeActivityType) {
		this.homeActivityType = homeActivityType;
	}

	@StringGetter(HOME_TYPE)
	public HomeType getHomeType() {
		return homeType;
	}

	@StringSetter(HOME_TYPE)
	public void setHomeType(HomeType homeType) {
		this.homeType = homeType;
	}
}
