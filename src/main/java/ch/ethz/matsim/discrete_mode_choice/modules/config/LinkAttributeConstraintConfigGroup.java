package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Collection;

import ch.ethz.matsim.discrete_mode_choice.components.constraints.LinkAttributeConstraint.Requirement;

public class LinkAttributeConstraintConfigGroup extends ComponentConfigGroup {
	public LinkAttributeConstraintConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
		// TODO Auto-generated constructor stub
	}

	public Requirement getRequirement();

	public String getAttributeName();

	public String getAttributeValue();

	public Collection<String> getConstrainedModes();
}
