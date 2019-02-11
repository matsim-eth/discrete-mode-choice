package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class VehicleConstraintConfigGroup extends ComponentConfigGroup {
	private Collection<String> requireStartAtHome = new HashSet<>();
	private Collection<String> requireEndAtHome = new HashSet<>();
	private Collection<String> requireContinuity = new HashSet<>(Arrays.asList("car", "bike"));
	private boolean requireHomeExists = false;

	private static final String REQUIRE_START_AT_HOME = "requireStartAtHome";
	private static final String REQUIRE_END_AT_HOME = "requireEndAtHome";
	private static final String REQUIRE_CONTINUITY = "requireContinuity";
	private static final String REQUIRE_HOME_EXISTS = "requireHomeExists";

	public VehicleConstraintConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
	}

	@Override
	public Map<String, String> getComments() {
		Map<String, String> comments = new HashMap<>();

		comments.put(REQUIRE_START_AT_HOME, "List of vehicular modes that must start at a home activity.");
		comments.put(REQUIRE_END_AT_HOME, "List of vehicular modes that must end at a home activity.");
		comments.put(REQUIRE_CONTINUITY,
				"List of vehicular modes that must be consistent, i.e. a trip can only be performed if the vehicle has been moved there before.");
		comments.put(REQUIRE_HOME_EXISTS,
				"Defines whether an agent without a home activity can use a constrained vehicular mode. If it is set to true agents without a home activity cannot use constrained modes. If it is set to false they can use constrained modes at any stage during their plan.");

		return comments;
	}

	public void setRequireStartAtHome(Collection<String> requireStartAtHome) {
		this.requireStartAtHome = new HashSet<>(requireStartAtHome);
	}

	public void setRequireEndAtHome(Collection<String> requireEndAtHome) {
		this.requireEndAtHome = new HashSet<>(requireEndAtHome);
	}

	public void setRequireContinuity(Collection<String> requireContinuity) {
		this.requireContinuity = new HashSet<>(requireContinuity);
	}

	@StringSetter(REQUIRE_HOME_EXISTS)
	public void setRequireHomeExists(boolean requireHomeExists) {
		this.requireHomeExists = requireHomeExists;
	}

	@StringSetter(REQUIRE_START_AT_HOME)
	public void setRequireStartAtHomeAsString(String requireStartAtHome) {
		this.requireStartAtHome = new HashSet<>(
				Arrays.asList(requireStartAtHome.split(",")).stream().map(String::trim).collect(Collectors.toSet()));
	}

	@StringSetter(REQUIRE_END_AT_HOME)
	public void setRequireEndAtHomeAsString(String requireEndAtHome) {
		this.requireEndAtHome = new HashSet<>(
				Arrays.asList(requireEndAtHome.split(",")).stream().map(String::trim).collect(Collectors.toSet()));
	}

	@StringSetter(REQUIRE_CONTINUITY)
	public void setRequireContinuityAsString(String requireContinuity) {
		this.requireContinuity = new HashSet<>(
				Arrays.asList(requireContinuity.split(",")).stream().map(String::trim).collect(Collectors.toSet()));
	}

	public Collection<String> getRequireStartAtHome() {
		return requireStartAtHome;
	}

	public Collection<String> getRequireEndAtHome() {
		return requireEndAtHome;
	}

	public Collection<String> getRequireContinuity() {
		return requireContinuity;
	}

	@StringGetter(REQUIRE_HOME_EXISTS)
	public boolean getRequireHomeExists() {
		return requireHomeExists;
	}

	@StringGetter(REQUIRE_START_AT_HOME)
	public String getRequireStartAtHomeAsString() {
		return String.join(", ", requireStartAtHome);
	}

	@StringGetter(REQUIRE_END_AT_HOME)
	public String getRequireEndAtHomeAsString() {
		return String.join(", ", requireEndAtHome);
	}

	@StringGetter(REQUIRE_CONTINUITY)
	public String getRequireContinuityAsString() {
		return String.join(", ", requireContinuity);
	}
}
