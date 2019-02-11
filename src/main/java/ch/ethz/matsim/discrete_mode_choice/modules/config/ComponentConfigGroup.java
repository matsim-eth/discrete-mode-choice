package ch.ethz.matsim.discrete_mode_choice.modules.config;

import org.matsim.core.config.ReflectiveConfigGroup;

public abstract class ComponentConfigGroup extends ReflectiveConfigGroup {
	private final String componentName;
	private final String componentType;

	public ComponentConfigGroup(String componentType, String componentName) {
		super(String.format("%s:%s", componentType, componentName));

		this.componentName = componentName;
		this.componentType = componentType;
	}

	public String getComponentName() {
		return componentName;
	}

	public String getComponentType() {
		return componentType;
	}
}
