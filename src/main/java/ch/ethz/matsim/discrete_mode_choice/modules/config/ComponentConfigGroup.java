package ch.ethz.matsim.discrete_mode_choice.modules.config;

import org.matsim.core.config.ReflectiveConfigGroup;

public abstract class ComponentConfigGroup extends ReflectiveConfigGroup {
	private final String componentName;

	public ComponentConfigGroup(String componentType, String componentName) {
		super(componentType);
		this.componentName = componentName;
	}

	public String getComponentName() {
		return componentName;
	}
}
