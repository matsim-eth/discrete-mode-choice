package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Collection;

import org.matsim.core.config.ReflectiveConfigGroup;

public class VehicleConstraintConfigGroup extends ReflectiveConfigGroup {

	public Collection<String> getRequireStartAtHome();
	public Collection<String> getRequireEndAtHome();
	public Collection<String> getRequireContinuity();
	public boolean getRequireHomeExists();
}
