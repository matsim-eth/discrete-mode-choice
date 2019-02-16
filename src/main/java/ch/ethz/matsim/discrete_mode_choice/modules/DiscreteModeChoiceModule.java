package ch.ethz.matsim.discrete_mode_choice.modules;

import org.matsim.core.controler.AbstractModule;

import com.google.inject.Inject;

import ch.ethz.matsim.discrete_mode_choice.modules.config.DiscreteModeChoiceConfigGroup;
import ch.ethz.matsim.discrete_mode_choice.modules.utils.ModeChoiceInTheLoopChecker;
import ch.ethz.matsim.discrete_mode_choice.replanning.DiscreteModeChoiceStrategyProvider;
import ch.ethz.matsim.discrete_mode_choice.replanning.NonSelectedPlanSelector;

public class DiscreteModeChoiceModule extends AbstractModule {
	public static final String STRATEGY_NAME = "DiscreteModeChoice";

	@Inject
	private DiscreteModeChoiceConfigGroup dmcConfig;

	@Override
	public void install() {
		addPlanStrategyBinding(STRATEGY_NAME).toProvider(DiscreteModeChoiceStrategyProvider.class);

		if (getConfig().strategy().getPlanSelectorForRemoval().equals(NonSelectedPlanSelector.NAME)) {
			bindPlanSelectorForRemoval().to(NonSelectedPlanSelector.class);
		}

		if (dmcConfig.getEnforceSinglePlan()) {
			addControlerListenerBinding().to(ModeChoiceInTheLoopChecker.class);
		}

		install(new ModelModule());
	}
}
