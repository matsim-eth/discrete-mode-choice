package ch.ethz.matsim.mode_choice.replanning;

import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.router.TripStructureUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChangePlanModesAlgorithm implements PlanAlgorithm {

	private final Random rng;
	ModeChoiceModel modeChoiceModel;
	private Network network;

	public ChangePlanModesAlgorithm(final Random rng, ModeChoiceModel modeChoiceModel, Network network) {
		this.rng = rng;
		this.modeChoiceModel = modeChoiceModel;
		this.network = network;
	}

	@Override
	public void run(Plan plan) {
		List<String> selectedModes = modeChoiceModel.chooseModes(plan);
		List<Leg> legs = TripStructureUtils.getLegs(plan);

		for (int i = 0; i < legs.size(); i++) {
			legs.get(i).setMode(selectedModes.get(i));
			legs.get(i).setRoute(null);
		}
	}
}
