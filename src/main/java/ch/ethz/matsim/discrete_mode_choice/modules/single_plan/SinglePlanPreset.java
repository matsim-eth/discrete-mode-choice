package ch.ethz.matsim.discrete_mode_choice.modules.single_plan;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.matsim.core.config.Config;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultSelector;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule.DefaultStrategy;

import ch.ethz.matsim.discrete_mode_choice.modules.DiscreteModeChoiceModule;
import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.replanning.NonSelectedPlanSelector;

public class SinglePlanPreset {
	/* package */ @SuppressWarnings("deprecation")
	final static Collection<String> INNOVATION_STRATEGIES = Arrays.asList(DefaultStrategy.ChangeLegMode,
			DefaultStrategy.ChangeSingleLegMode, DefaultStrategy.ChangeSingleTripMode, DefaultStrategy.ChangeTripMode,
			DefaultStrategy.ReRoute, DefaultStrategy.SubtourModeChoice, DefaultStrategy.TimeAllocationMutator,
			DefaultStrategy.TimeAllocationMutator_ReRoute);

	/* package */ final static Collection<String> SELECTION_STRATEGIES = Arrays.asList(DefaultSelector.BestScore,
			DefaultSelector.ChangeExpBeta, DefaultSelector.KeepLastSelected, DefaultSelector.SelectExpBeta,
			DefaultSelector.SelectPathSizeLogit, DefaultSelector.SelectRandom);

	private final static double DEFAULT_REPLANNING_RATE = 0.25;

	public static void configure(Config config) {
		StrategyConfigGroup strategyConfigGroup = new StrategyConfigGroup();
		double replanningRate = DEFAULT_REPLANNING_RATE;

		for (StrategySettings strategy : strategyConfigGroup.getStrategySettings()) {
			if (strategy.getStrategyName().equals(DiscreteModeChoiceModule.STRATEGY_NAME)) {
				replanningRate = strategy.getWeight();
			}
		}

		configure(config, replanningRate);
	}

	public static void configure(Config config, double replanningRate) {
		StrategyConfigGroup strategyConfigGroup = new StrategyConfigGroup();
		strategyConfigGroup.clearStrategySettings();

		strategyConfigGroup.setMaxAgentPlanMemorySize(1);
		strategyConfigGroup.setFractionOfIterationsToDisableInnovation(0.0);
		strategyConfigGroup.setPlanSelectorForRemoval("NonSelectedPlanSelector");

		StrategySettings dcmStrategy = new StrategySettings();
		dcmStrategy.setStrategyName(DiscreteModeChoiceModule.STRATEGY_NAME);
		dcmStrategy.setWeight(replanningRate);

		StrategySettings selectorStrategy = new StrategySettings();
		selectorStrategy.setStrategyName(DefaultSelector.KeepLastSelected);
		selectorStrategy.setWeight(1.0 - replanningRate);

		check(strategyConfigGroup);

		DiscreteModeChoiceConfigGroup dcmConfig = (DiscreteModeChoiceConfigGroup) config.getModules()
				.get(DiscreteModeChoiceConfigGroup.GROUP_NAME);

		if (dcmConfig == null) {
			dcmConfig = new DiscreteModeChoiceConfigGroup();
			config.addModule(dcmConfig);
		}

		dcmConfig.setEnforceSinglePlan(true);
	}

	public static void check(StrategyConfigGroup strategyConfigGroup) {
		if (strategyConfigGroup.getMaxAgentPlanMemorySize() != 1) {
			throw new IllegalStateException(
					"Option strategy.maxAgentPlanMemorySize should be 1 if single plan mode is enforced.");
		}

		Set<String> activeStrategies = new HashSet<>();

		for (StrategySettings strategySettings : strategyConfigGroup.getStrategySettings()) {
			if (strategySettings.getDisableAfter() != 0) {
				activeStrategies.add(strategySettings.getStrategyName());
			}
		}

		if (!activeStrategies.contains(DefaultSelector.KeepLastSelected)) {
			throw new IllegalStateException(
					"KeepLastSelected should be an active strategy if single plan mode is enforced");
		}

		if (!activeStrategies.contains(DiscreteModeChoiceModule.STRATEGY_NAME)) {
			throw new IllegalStateException("Strategy " + DiscreteModeChoiceModule.STRATEGY_NAME
					+ " must be active if single plan mode is enforced");
		}

		activeStrategies.remove(DefaultSelector.KeepLastSelected);
		activeStrategies.remove(DiscreteModeChoiceModule.STRATEGY_NAME);

		if (activeStrategies.size() > 0) {
			throw new IllegalStateException(
					"All these strategies should be disabled (disableAfter == 0) if single plan mode is enforced: "
							+ activeStrategies);
		}

		if (!strategyConfigGroup.getPlanSelectorForRemoval().equals(NonSelectedPlanSelector.NAME)) {
			throw new IllegalStateException(
					"Removal selector should be NonSelectedPlanSelector if single plan mode is enforced.");
		}
	}
}
