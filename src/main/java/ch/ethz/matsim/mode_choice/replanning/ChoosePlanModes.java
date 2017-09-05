package ch.ethz.matsim.mode_choice.replanning;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChoosePlanModes extends AbstractMultithreadedModule  {

	private ModeChoiceModel modeChoiceModel;
	private Network network;
	
	public ChoosePlanModes(GlobalConfigGroup globalConfigGroup, 
			ModeChoiceModel modeChoiceModel, Network network) {
		super(globalConfigGroup);
		this.modeChoiceModel = modeChoiceModel;
		this.network = network;
	}
	
	@Override
	public PlanAlgorithm getPlanAlgoInstance() {

		ChangePlanModesAlgorithm cslma = new ChangePlanModesAlgorithm(
				MatsimRandom.getLocalInstance(), modeChoiceModel, network); 
		
		return cslma;
	}

}
