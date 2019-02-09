package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Collection;

import org.matsim.core.config.ReflectiveConfigGroup;

public class ModeAvailabilityConfigGroup extends ReflectiveConfigGroup {
	private static public String GROUP_NAME = "modeAvailability";
	
	public DefaultModeAvailabilityConfigGroup() {
		super("modeA")
	}

	public Collection<String> getAvailableModes() {

	}
}
