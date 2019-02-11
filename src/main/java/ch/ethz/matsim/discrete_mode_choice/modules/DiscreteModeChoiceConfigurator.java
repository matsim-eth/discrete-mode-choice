package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collections;

import org.matsim.core.config.Config;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.SubtourModeChoiceConfigGroup;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;

import ch.ethz.matsim.discrete_mode_choice.modules.ModelModule.ModelType;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.single_plan.SinglePlanPreset;

public class DiscreteModeChoiceConfigurator {
	static public void configureForSinglePlan(Config config) {
		SinglePlanPreset.configure(config);
	}
	
	static public void configureAsImportanceSampler(Config config) {
		
	}

	static public void configureAsSubtourModeChoiceReplacement(Config config) {
		for (StrategySettings strategy : config.strategy().getStrategySettings()) {
			if (strategy.getStrategyName().equals(DefaultStrategy.SubtourModeChoice)) {
				strategy.setStrategyName(DiscreteModeChoiceModule.STRATEGY_NAME);
			}
		}

		DiscreteModeChoiceConfigGroup dmcConfig = new DiscreteModeChoiceConfigGroup();
		SubtourModeChoiceConfigGroup smcConfig = config.subtourModeChoice();

		if (config.getModules().containsKey(DiscreteModeChoiceConfigGroup.GROUP_NAME)) {
			config.removeModule(DiscreteModeChoiceConfigGroup.GROUP_NAME);
		}

		config.addModule(dmcConfig);

		dmcConfig.setModelType(ModelType.Tour);
		dmcConfig.setSelector(SelectorModule.RANDOM);
		dmcConfig.setTourConstraints(Collections.singleton(ConstraintModule.VEHICLE_CONTINUITY));
		dmcConfig.setTourEstimator(EstimatorModule.UNIFORM);
		dmcConfig.setTourFinder(TourFinderModule.PLAN_BASED);

		dmcConfig.getVehicleTourConstraintConfig().setRequireContinuity(Arrays.asList(smcConfig.getChainBasedModes()));

		if (smcConfig.considerCarAvailability()) {
			dmcConfig.setModeAvailability(ModeAvailabilityModule.CAR);
			dmcConfig.getCarModeAvailabilityConfig().setAvailableModes(Arrays.asList(smcConfig.getModes()));
		} else {
			dmcConfig.setModeAvailability(ModeAvailabilityModule.DEFAULT);
			dmcConfig.getDefaultModeAvailabilityConfig().setAvailableModes(Arrays.asList(smcConfig.getModes()));
		}
	}
}
