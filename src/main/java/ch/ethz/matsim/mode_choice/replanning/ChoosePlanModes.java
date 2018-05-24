package ch.ethz.matsim.mode_choice.replanning;

import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChoosePlanModes extends AbstractMultithreadedModule {

	private ModeChoiceModel modeChoiceModel;

	public ChoosePlanModes(GlobalConfigGroup globalConfigGroup, ModeChoiceModel modeChoiceModel) {
		super(globalConfigGroup);
		this.modeChoiceModel = modeChoiceModel;
	}

	@Override
	public PlanAlgorithm getPlanAlgoInstance() {
		return new ChangePlanModesAlgorithm(MatsimRandom.getLocalInstance(), modeChoiceModel);
	}

}
