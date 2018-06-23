package ch.ethz.matsim.mode_choice.v2.replanning;

import javax.inject.Inject;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.ReRoute;
import org.matsim.core.replanning.modules.TripsToLegsModule;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;

import com.google.inject.Provider;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;

public class ModeChoiceModelStrategy implements Provider<PlanStrategy> {
	private final GlobalConfigGroup globalConfigGroup;
	private final Provider<TripRouter> tripRouterProvider;
	private final ActivityFacilities activityFacilities;
	private final ModeChoiceModel modeChoiceModel;
	final private StageActivityTypes stageActivityTypes;

	@Inject
	ModeChoiceModelStrategy(GlobalConfigGroup globalConfigGroup, ActivityFacilities activityFacilities,
			Provider<TripRouter> tripRouterProvider, ModeChoiceModel modeChoiceModel,
			StageActivityTypes stageActivityTypes) {
		this.globalConfigGroup = globalConfigGroup;
		this.activityFacilities = activityFacilities;
		this.tripRouterProvider = tripRouterProvider;
		this.modeChoiceModel = modeChoiceModel;
		this.stageActivityTypes = stageActivityTypes;
	}

	@Override
	public PlanStrategy get() {
		PlanStrategyImpl.Builder builder = new PlanStrategyImpl.Builder(new RandomPlanSelector());
		builder.addStrategyModule(new TripsToLegsModule(tripRouterProvider, globalConfigGroup));
		builder.addStrategyModule(new ModeChoiceModelModule(globalConfigGroup, modeChoiceModel, stageActivityTypes));
		builder.addStrategyModule(new ReRoute(activityFacilities, tripRouterProvider, globalConfigGroup));
		return builder.build();
	}

}
