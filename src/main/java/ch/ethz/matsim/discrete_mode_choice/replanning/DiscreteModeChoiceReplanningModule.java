package ch.ethz.matsim.discrete_mode_choice.replanning;

import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.TripRouter;

import com.google.inject.Provider;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;

/**
 * This replanning module creates new instances of the
 * DiscreteModeChoiceAlgorithm.
 * 
 * @author sebhoerl
 */
public class DiscreteModeChoiceReplanningModule extends AbstractMultithreadedModule {
	public static final String NAME = "DiscreteModeChoice";

	final private Provider<DiscreteModeChoiceModel> modelProvider;
	final private Provider<TripRouter> tripRouterProvider;
	final private PopulationFactory populationFactory;

	public DiscreteModeChoiceReplanningModule(GlobalConfigGroup globalConfigGroup,
			Provider<DiscreteModeChoiceModel> modeChoiceModelProvider, PopulationFactory populationFactory,
			Provider<TripRouter> tripRouterProvider) {
		super(globalConfigGroup);

		this.modelProvider = modeChoiceModelProvider;
		this.populationFactory = populationFactory;
		this.tripRouterProvider = tripRouterProvider;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		DiscreteModeChoiceModel choiceModel = modelProvider.get();
		TripRouter tripRouter = tripRouterProvider.get();

		MainModeIdentifier mainModeIdentifier = tripRouter.getMainModeIdentifier();
		StageActivityTypes stageActivityTypes = tripRouter.getStageActivityTypes();

		return new DiscreteModeChoiceAlgorithm(MatsimRandom.getLocalInstance(), choiceModel, populationFactory,
				mainModeIdentifier, stageActivityTypes);
	}
}
