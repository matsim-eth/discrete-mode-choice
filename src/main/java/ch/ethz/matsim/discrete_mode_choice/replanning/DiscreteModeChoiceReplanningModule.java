package ch.ethz.matsim.discrete_mode_choice.replanning;

import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.router.MainModeIdentifier;
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
	final private MainModeIdentifier mainModeIdentifier;

	public DiscreteModeChoiceReplanningModule(GlobalConfigGroup globalConfigGroup,
			Provider<DiscreteModeChoiceModel> modeChoiceModelProvider, Provider<TripRouter> tripRouterProvider,
			PopulationFactory populationFactory, MainModeIdentifier mainModeIdentifier) {
		super(globalConfigGroup);

		this.modelProvider = modeChoiceModelProvider;
		this.tripRouterProvider = tripRouterProvider;
		this.populationFactory = populationFactory;
		this.mainModeIdentifier = mainModeIdentifier;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		DiscreteModeChoiceModel choiceModel = modelProvider.get();
		TripRouter tripRouter = tripRouterProvider.get();

		return new DiscreteModeChoiceAlgorithm(MatsimRandom.getLocalInstance(), choiceModel, mainModeIdentifier,
				populationFactory);
	}
}
