package ch.ethz.matsim.discrete_mode_choice.modules;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.matsim.core.config.Config;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.config.groups.SubtourModeChoiceConfigGroup;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;

import ch.ethz.matsim.discrete_mode_choice.modules.ModelModule.ModelType;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.replanning.NonSelectedPlanSelector;

public final class DiscreteModeChoiceConfigurator {
	private DiscreteModeChoiceConfigurator() {

	}

	static public void configureAsSubtourModeChoiceReplacement(Config config) {
		for (StrategySettings strategy : config.strategy().getStrategySettings()) {
			if (strategy.getStrategyName().equals(DefaultStrategy.SubtourModeChoice)) {
				strategy.setStrategyName(DiscreteModeChoiceModule.STRATEGY_NAME);
			}
		}

		SubtourModeChoiceConfigGroup smcConfig = config.subtourModeChoice();
		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

		if (dmcConfig == null) {
			dmcConfig = new DiscreteModeChoiceConfigGroup();
			config.addModule(dmcConfig);
		}

		dmcConfig.setModelType(ModelType.Tour);
		dmcConfig.setSelector(SelectorModule.RANDOM);
		dmcConfig.setTourConstraints(Collections.singleton(ConstraintModule.VEHICLE_CONTINUITY));
		dmcConfig.setTourEstimator(EstimatorModule.UNIFORM);
		dmcConfig.setTourFinder(TourFinderModule.PLAN_BASED);

		dmcConfig.getVehicleTourConstraintConfig().setRequireContinuity(Arrays.asList(smcConfig.getChainBasedModes()));
		dmcConfig.getVehicleTourConstraintConfig().setRequireStartAtHome(Arrays.asList(smcConfig.getChainBasedModes()));
		dmcConfig.getVehicleTourConstraintConfig().setRequireEndAtHome(Arrays.asList(smcConfig.getChainBasedModes()));

		if (smcConfig.considerCarAvailability()) {
			dmcConfig.setModeAvailability(ModeAvailabilityModule.CAR);
			dmcConfig.getCarModeAvailabilityConfig().setAvailableModes(Arrays.asList(smcConfig.getModes()));
		} else {
			dmcConfig.setModeAvailability(ModeAvailabilityModule.DEFAULT);
			dmcConfig.getDefaultModeAvailabilityConfig().setAvailableModes(Arrays.asList(smcConfig.getModes()));
		}
	}

	static public void configureAsImportanceSampler(Config config) {
		configureAsSubtourModeChoiceReplacement(config);
		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

		dmcConfig.setSelector(SelectorModule.MULTINOMIAL_LOGIT);
		dmcConfig.setTourEstimator(EstimatorModule.MATSIM_DAY_SCORING);
	}

	private final static double DEFAULT_REPLANNING_RATE = 0.20;

	static public void configureAsModeChoiceInTheLoop(Config config) {
		configureAsModeChoiceInTheLoop(config, DEFAULT_REPLANNING_RATE);
	}

	static public void configureAsModeChoiceInTheLoop(Config config, double replanningRate) {
		StrategyConfigGroup strategyConfigGroup = config.strategy();
		strategyConfigGroup.clearStrategySettings();

		strategyConfigGroup.setMaxAgentPlanMemorySize(1);
		strategyConfigGroup.setFractionOfIterationsToDisableInnovation(Double.POSITIVE_INFINITY);
		strategyConfigGroup.setPlanSelectorForRemoval(NonSelectedPlanSelector.NAME);

		StrategySettings dmcStrategy = new StrategySettings();
		dmcStrategy.setStrategyName(DiscreteModeChoiceModule.STRATEGY_NAME);
		dmcStrategy.setWeight(replanningRate);
		strategyConfigGroup.addStrategySettings(dmcStrategy);

		StrategySettings selectorStrategy = new StrategySettings();
		selectorStrategy.setStrategyName(DefaultSelector.KeepLastSelected);
		selectorStrategy.setWeight(1.0 - replanningRate);
		strategyConfigGroup.addStrategySettings(selectorStrategy);

		checkModeChoiceInTheLoop(strategyConfigGroup);

		DiscreteModeChoiceConfigGroup dmcConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

		if (dmcConfig == null) {
			dmcConfig = new DiscreteModeChoiceConfigGroup();
			config.addModule(dmcConfig);
		}

		dmcConfig.setEnforceSinglePlan(true);
	}

	public static void checkModeChoiceInTheLoop(StrategyConfigGroup strategyConfigGroup) {
		if (strategyConfigGroup.getMaxAgentPlanMemorySize() != 1) {
			throw new IllegalStateException(
					"Option strategy.maxAgentPlanMemorySize should be 1 if mode-choice-in-the-loop is enforced.");
		}

		Set<String> activeStrategies = new HashSet<>();

		for (StrategySettings strategySettings : strategyConfigGroup.getStrategySettings()) {
			if (strategySettings.getDisableAfter() != 0) {
				activeStrategies.add(strategySettings.getStrategyName());
			}
		}

		if (!activeStrategies.contains(DefaultSelector.KeepLastSelected)) {
			throw new IllegalStateException(
					"KeepLastSelected should be an active strategy if mode-choice-in-the-loop is enforced");
		}

		if (!activeStrategies.contains(DiscreteModeChoiceModule.STRATEGY_NAME)) {
			throw new IllegalStateException("Strategy " + DiscreteModeChoiceModule.STRATEGY_NAME
					+ " must be active if single plan mode is enforced");
		}

		activeStrategies.remove(DefaultSelector.KeepLastSelected);
		activeStrategies.remove(DiscreteModeChoiceModule.STRATEGY_NAME);

		if (activeStrategies.size() > 0) {
			throw new IllegalStateException(
					"All these strategies should be disabled (disableAfter == 0) if mode-choice-in-the-loop is enforced: "
							+ activeStrategies);
		}

		if (!strategyConfigGroup.getPlanSelectorForRemoval().equals(NonSelectedPlanSelector.NAME)) {
			throw new IllegalStateException(
					"Removal selector should be NonSelectedPlanSelector if mode-choice-in-the-loop is enforced.");
		}
	}
}
