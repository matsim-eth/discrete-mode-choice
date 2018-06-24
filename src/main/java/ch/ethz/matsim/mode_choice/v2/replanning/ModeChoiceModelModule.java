package ch.ethz.matsim.mode_choice.v2.replanning;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;

import com.google.inject.Provider;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;

public class ModeChoiceModelModule extends AbstractMultithreadedModule {
	final private Provider<ModeChoiceModel> modeChoiceModelProvider;

	public ModeChoiceModelModule(GlobalConfigGroup globalConfigGroup,
			Provider<ModeChoiceModel> modeChoiceModelProvider) {
		super(globalConfigGroup);

		this.modeChoiceModelProvider = modeChoiceModelProvider;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		ModeChoiceModel modeChoiceModel = modeChoiceModelProvider.get();
		return new ModeChoiceModelAlgorithm(MatsimRandom.getLocalInstance(), modeChoiceModel);
	}

}
