package ch.ethz.matsim.mode_choice.v2.replanning;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;
import org.matsim.core.router.StageActivityTypes;

import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;

public class ModeChoiceModelModule extends AbstractMultithreadedModule {
	final private StageActivityTypes stageActivityTypes;
	final private ModeChoiceModel modeChoiceModel;

	public ModeChoiceModelModule(GlobalConfigGroup globalConfigGroup, ModeChoiceModel modeChoiceModel,
			StageActivityTypes stageActivityTyes) {
		super(globalConfigGroup);

		this.modeChoiceModel = modeChoiceModel;
		this.stageActivityTypes = stageActivityTyes;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		return new ModeChoiceModelAlgorithm(MatsimRandom.getLocalInstance(), stageActivityTypes, modeChoiceModel);
	}

}
