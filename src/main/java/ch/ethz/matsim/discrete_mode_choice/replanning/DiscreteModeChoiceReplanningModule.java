package ch.ethz.matsim.discrete_mode_choice.replanning;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;

import com.google.inject.Provider;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceModel;

/**
 * This replanning module creates new instances of the
 * DiscreteModeChoiceAlgorithm.
 * 
 * @sebhoerl
 */
public class DiscreteModeChoiceReplanningModule extends AbstractMultithreadedModule {
	public static final String NAME = "DiscreteModeChoice";
	
	final private Provider<DiscreteModeChoiceModel> modelProvider;

	public DiscreteModeChoiceReplanningModule(GlobalConfigGroup globalConfigGroup,
			Provider<DiscreteModeChoiceModel> modeChoiceModelProvider) {
		super(globalConfigGroup);
		this.modelProvider = modeChoiceModelProvider;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		DiscreteModeChoiceModel choiceModel = modelProvider.get();
		return new DiscreteModeChoiceAlgorithm(MatsimRandom.getLocalInstance(), choiceModel);
	}
}
