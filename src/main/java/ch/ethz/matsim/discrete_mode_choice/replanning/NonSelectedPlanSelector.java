package ch.ethz.matsim.discrete_mode_choice.replanning;

import org.matsim.api.core.v01.population.HasPlansAndId;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.selectors.PlanSelector;

import com.google.inject.Provider;

/**
 * This is a plan selector for replanning that always selects a plan that is
 * *not* selected currently. This is especially useful when keeping only one
 * plan in an agent's memory but replanning is frequently. This way always the
 * new replanned version will be kept.
 * 
 * @author sebhoerl
 */
public class NonSelectedPlanSelector implements PlanSelector<Plan, Person> {
	static public final String NAME = "NonSelectedPlanSelector";

	@Override
	public Plan selectPlan(HasPlansAndId<Plan, Person> member) {
		for (Plan plan : member.getPlans()) {
			if (!plan.equals(member.getSelectedPlan())) {
				return plan;
			}
		}
		return null;
	}

	static public class SelectorProvider implements Provider<PlanStrategy> {
		@Override
		public PlanStrategy get() {
			return new PlanStrategyImpl.Builder(new NonSelectedPlanSelector()).build();
		}
	}
}