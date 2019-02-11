package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ModeAvailabilityConfigGroup extends ComponentConfigGroup {
	private Collection<String> availableModes = new HashSet<>(Arrays.asList("car", "bike", "pt", "walk"));

	public static final String AVAILABLE_MODES = "availableModes";

	public ModeAvailabilityConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
	}

	public void setAvailableModes(Collection<String> availableModes) {
		this.availableModes = new HashSet<>(availableModes);
	}

	public Collection<String> getAvailableModes() {
		return availableModes;
	}

	@StringSetter(AVAILABLE_MODES)
	public void setAvailableModesAsString(String constrainedModes) {
		this.availableModes = Arrays.asList(constrainedModes.split(",")).stream().map(String::trim)
				.collect(Collectors.toSet());
	}

	@StringGetter(AVAILABLE_MODES)
	public String getAvailableModesAsString() {
		return String.join(", ", availableModes);
	}
}
