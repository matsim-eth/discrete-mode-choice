package ch.ethz.matsim.mode_choice.replanning;

import javax.inject.Inject;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.groups.ChangeModeConfigGroup;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.replanning.modules.ChangeSingleLegMode;
import org.matsim.core.replanning.modules.ReRoute;
import org.matsim.core.replanning.modules.TripsToLegsModule;
import org.matsim.core.replanning.selectors.RandomPlanSelector;
import org.matsim.core.router.TripRouter;
import org.matsim.facilities.ActivityFacilities;

import com.google.inject.Provider;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ModeChoiceStrategy implements Provider<PlanStrategy> {

	private final GlobalConfigGroup globalConfigGroup;
	private Provider<TripRouter> tripRouterProvider;
	private ActivityFacilities activityFacilities;
	private Network network;
	private ModeChoiceModel modeChoiceModel;
	
	@Inject
	ModeChoiceStrategy(GlobalConfigGroup globalConfigGroup,	ActivityFacilities activityFacilities,
			Provider<TripRouter> tripRouterProvider, Network network,
			ModeChoiceModel modeChoiceModel) {
		this.globalConfigGroup = globalConfigGroup;
		this.activityFacilities = activityFacilities;
		this.tripRouterProvider = tripRouterProvider;
		this.network = network;
		this.modeChoiceModel = modeChoiceModel;
	}

    @Override
	public PlanStrategy get() {
		PlanStrategyImpl strategy = new PlanStrategyImpl(new RandomPlanSelector());
		strategy.addStrategyModule(new TripsToLegsModule(tripRouterProvider, globalConfigGroup));
		//strategy.addStrategyModule(new ChooseSingleLegMode(globalConfigGroup, modeChoiceModel, network));
		strategy.addStrategyModule(new ChoosePlanModes(globalConfigGroup, modeChoiceModel, network));
		strategy.addStrategyModule(new ReRoute(activityFacilities, tripRouterProvider, globalConfigGroup));
		return strategy;
	}

}
