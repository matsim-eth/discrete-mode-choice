package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Config group for the SubtourModeConstraint.
 * 
 * @author sebhoerl
 *
 */
public class SubtourModeConstraintConfigGroup extends ComponentConfigGroup {
	private Collection<String> availableModes = new HashSet<>();
	private Collection<String> constrainedModes = new HashSet<>();
	private boolean keepUnavailableModes = true;

	public final static String CONSTRAINED_MODES = "constrainedModes";
	public final static String AVAILABLE_MODES = "availableModes";
	public final static String KEEP_UNAVAILABLE_MODES = "keepUnavailableModes";

	public SubtourModeConstraintConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
	}

	@Override
	public Map<String, String> getComments() {
		Map<String, String> comments = new HashMap<>();

		comments.put(CONSTRAINED_MODES,
				"Modes for which the sub-tour behaviour should be replicated. If all available modes are put here, this equals to SubTourModeChoice with singleLegProbability == 0.0; if only the constrained modes are put here, it equals singleLegProbability > 0.0");
		comments.put(AVAILABLE_MODES,
				"Defines which modes are available more mode choice in general. Makes mainly sense in combination with keepUnavailableModes.");

		return comments;
	}

	public void setConstrainedModes(Collection<String> constrainedModes) {
		this.constrainedModes = new HashSet<>(constrainedModes);
	}

	public Collection<String> getConstrainedModes() {
		return constrainedModes;
	}

	@StringSetter(CONSTRAINED_MODES)
	public void setConstrainedModesAsString(String constrainedModes) {
		this.constrainedModes = Arrays.asList(constrainedModes.split(",")).stream().map(String::trim)
				.collect(Collectors.toSet());
	}

	@StringGetter(CONSTRAINED_MODES)
	public String getConstrainedModesAsString() {
		return String.join(", ", constrainedModes);
	}

	public void setAvailableModes(Collection<String> availableModes) {
		this.availableModes = new HashSet<>(availableModes);
	}

	public Collection<String> getAvailableModes() {
		return availableModes;
	}

	@StringSetter(AVAILABLE_MODES)
	public void setAvailableModesAsString(String availableModes) {
		this.availableModes = Arrays.asList(availableModes.split(",")).stream().map(String::trim)
				.collect(Collectors.toSet());
	}

	@StringGetter(AVAILABLE_MODES)
	public String getAvailableModesAsString() {
		return String.join(", ", availableModes);
	}

	@StringSetter(KEEP_UNAVAILABLE_MODES)
	public void setKeepUnavailableModes(boolean keepUnavailableModes) {
		this.keepUnavailableModes = keepUnavailableModes;
	}

	@StringGetter(KEEP_UNAVAILABLE_MODES)
	public boolean getKeepUnavailableModes() {
		return keepUnavailableModes;
	}
}
