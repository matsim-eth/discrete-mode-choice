package ch.ethz.matsim.discrete_mode_choice.modules.single_plan;

import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.replanning.selectors.PlanSelector;

import com.google.inject.Inject;

import ch.ethz.matsim.discrete_mode_choice.replanning.NonSelectedPlanSelector;

public class SinglePlanChecker implements StartupListener {
	private final StrategyConfigGroup strategyConfig;
	private final PlanSelector<Plan, Person> removalSelector;

	@Inject
	public SinglePlanChecker(StrategyConfigGroup strategyConfig, PlanSelector<Plan, Person> removalSelector) {
		this.strategyConfig = strategyConfig;
		this.removalSelector = removalSelector;
	}

	@Override
	public void notifyStartup(StartupEvent event) {
		SinglePlanPreset.check(strategyConfig);

		if (!(removalSelector instanceof NonSelectedPlanSelector)) {
			throw new IllegalStateException(
					"Removal strategy should be NonSelectedPlanSelector if single plan mode is enforced.");
		}
	}
}
