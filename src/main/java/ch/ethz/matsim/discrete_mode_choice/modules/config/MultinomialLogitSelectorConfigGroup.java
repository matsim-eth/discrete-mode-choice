package ch.ethz.matsim.discrete_mode_choice.modules.config;

public class MultinomialLogitSelectorConfigGroup extends ComponentConfigGroup {

	public MultinomialLogitSelectorConfigGroup(String componentType, String componentName) {
		super(componentType, componentName);
		// TODO Auto-generated constructor stub
	}

	
	public double getMinimumUtility();
	public double getMaximumUtility();
}
