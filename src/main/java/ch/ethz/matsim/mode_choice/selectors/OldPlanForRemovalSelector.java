package ch.ethz.matsim.mode_choice.selectors;

import org.matsim.api.core.v01.population.BasicPlan;
import org.matsim.api.core.v01.population.HasPlansAndId;
import org.matsim.core.replanning.selectors.PlanSelector;

public class OldPlanForRemovalSelector<T extends BasicPlan, I> implements PlanSelector<T, I> {

	@Override
	public T selectPlan(HasPlansAndId<T, I> member) {

		for (T plan : member.getPlans()) {
			
			if (!plan.equals(member.getSelectedPlan())) {
				return plan;
			}
		}		
		
		return null;
	}

}
