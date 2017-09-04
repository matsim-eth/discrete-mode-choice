package ch.ethz.matsim.mode_choice.selectors;

import org.matsim.api.core.v01.population.HasPlansAndId;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.replanning.selectors.PlanSelector;

public class OldPlanForRemovalSelector implements PlanSelector<Plan, Person> {

	@Override
	public Plan selectPlan(HasPlansAndId<Plan, Person> member) {

		for (Plan plan : member.getPlans()) {

			if (!plan.equals(member.getSelectedPlan())) {
				return plan;
			}
		}

		return null;
	}

}
