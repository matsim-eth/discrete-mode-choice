package ch.ethz.matsim.discrete_mode_choice.modules.config;

import java.util.Collection;

import org.matsim.core.config.ReflectiveConfigGroup;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;
import ch.ethz.matsim.discrete_mode_choice.modules.ModelModule;

public class DiscreteModeChoiceConfigGroup extends ReflectiveConfigGroup {
	public static String NAME = "DiscreteModeChoice";

	public static String PERFORM_REROUTE = "performReroute";
	public static String ENFORCE_SINGLE_PLAN = "enforceSinglePlan";

	private boolean performReroute = false;
	private boolean enforceSinglePlan = false;

	public DiscreteModeChoiceConfigGroup() {
		super(NAME);
	}

	public boolean getPerformReroute() {
		return performReroute;
	}

	public void setPerformReroute(boolean performReroute) {
		this.performReroute = performReroute;
	}

	public boolean getEnforceSinglePlan() {
		return enforceSinglePlan;
	}

	public void setEnforceSinglePlan(boolean enforceSinglePlan) {
		this.enforceSinglePlan = enforceSinglePlan;
	}

	public DiscreteModeChoiceModel.FallbackBehaviour getFallbackBehaviour() {
		
	}
	
	public String getModeAvailabilityComponent() {
		
	}
	
	public String getTourEstimatorComponent() {
		
	}
	
	public String getTripEstimatorComponent() {
		
	}
	
	public String getTourFinderComponent() {
		
	}
	
	public ComponentConfigGroup getComponentConfig(String componentType, String componentName);
	
	public ActivityTourFinderConfigGroup getActivityTourFinderConfigGroup();
	
	public ModeAvailabilityConfigGroup getDefaultModeAvailabilityConfig();
	
	public ModeAvailabilityConfigGroup getCarModeAvailabilityConfig();
	
	public MultinomialLogitSelectorConfigGroup getMultinomialLogitSelectorConfig();
	
	public String getTourSelectorComponent();
	
	public String getTripSelectorComponent();

	
	public Collection<String> getActiveTourConstraints();
	
	public Collection<String> getActiveTripConstraints();
	
	public LinkAttributeConstraintConfigGroup getLinkAttributeConstraintConfigGroup();
	
	public ShapeFileConstraintConfigGroup getShapeFileConstraintConfigGroup();
	
	public VehicleConstraintConfigGroup getVehicleTripConstraintConfig();
	
	public VehicleConstraintConfigGroup getVehicleTourConstraintConfig();
	
	public ModelModule.ModelType getModelType();
}
