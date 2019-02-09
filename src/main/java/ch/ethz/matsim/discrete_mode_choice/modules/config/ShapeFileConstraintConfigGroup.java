package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Collection;

import ch.ethz.matsim.discrete_mode_choice.components.constraints.ShapeFileConstraint.Requirement;

public class ShapeFileConstraintConfigGroup extends ComponentConfigGroup {
	public ShapeFileConstraintConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
		// TODO Auto-generated constructor stub
	}

	public Requirement getRequirement();

	public String getPath();

	public Collection<String> getConstrainedModes();
}
