package ch.ethz.matsim.mode_choice.replanning;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.groups.GlobalConfigGroup;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.replanning.modules.AbstractMultithreadedModule;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChooseSingleLegMode extends AbstractMultithreadedModule  {

	private ModeChoiceModel modeChoiceModel;
	private Network network;
	
	public ChooseSingleLegMode(GlobalConfigGroup globalConfigGroup, 
			ModeChoiceModel modeChoiceModel, Network network) {
		super(globalConfigGroup);
		this.modeChoiceModel = modeChoiceModel;
		this.network = network;
	}
	
	@Override
	public PlanAlgorithm getPlanAlgoInstance() {

		ChnageSingleLegModeAlgorithm cslma = new ChnageSingleLegModeAlgorithm(
				MatsimRandom.getLocalInstance(), modeChoiceModel, network); 
		
		return cslma;
	}

}
