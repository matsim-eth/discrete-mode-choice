package ch.ethz.matsim.mode_choice.replanning;

import javax.inject.Inject;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.ReRoute;
import org.matsim.core.replanning.modules.TripsToLegsModule;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;

import com.google.inject.Provider;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ModeChoiceStrategy implements Provider<PlanStrategy> {
	private final GlobalConfigGroup globalConfigGroup;
	private final Provider<TripRouter> tripRouterProvider;
	private final ActivityFacilities activityFacilities;
	private final ModeChoiceModel modeChoiceModel;

	@Inject
	ModeChoiceStrategy(GlobalConfigGroup globalConfigGroup, ActivityFacilities activityFacilities,
			Provider<TripRouter> tripRouterProvider, ModeChoiceModel modeChoiceModel) {
		this.globalConfigGroup = globalConfigGroup;
		this.activityFacilities = activityFacilities;
		this.tripRouterProvider = tripRouterProvider;
		this.modeChoiceModel = modeChoiceModel;
	}

	@Override
	public PlanStrategy get() {
		PlanStrategyImpl strategy = new PlanStrategyImpl(new RandomPlanSelector());
		strategy.addStrategyModule(new TripsToLegsModule(tripRouterProvider, globalConfigGroup));
		// strategy.addStrategyModule(new ChooseSingleLegMode(globalConfigGroup,
		// modeChoiceModel, network));
		strategy.addStrategyModule(new ChoosePlanModes(globalConfigGroup, modeChoiceModel));
		strategy.addStrategyModule(new ReRoute(activityFacilities, tripRouterProvider, globalConfigGroup));
		return strategy;
	}

}
