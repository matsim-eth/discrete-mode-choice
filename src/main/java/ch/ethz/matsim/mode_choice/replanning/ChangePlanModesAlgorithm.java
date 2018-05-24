package ch.ethz.matsim.mode_choice.replanning;

import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.algorithms.PlanAlgorithm;
import org.matsim.core.router.TripStructureUtils;

import ch.ethz.matsim.mode_choice.ModeChoiceModel;

public class ChangePlanModesAlgorithm implements PlanAlgorithm {
	private final Random random;
	private final ModeChoiceModel modeChoiceModel;

	public ChangePlanModesAlgorithm(Random random, ModeChoiceModel modeChoiceModel) {
		this.random = random;
		this.modeChoiceModel = modeChoiceModel;
	}

	@Override
	public void run(Plan plan) {
		List<String> selectedModes = modeChoiceModel.chooseModes(plan, random);
		List<Leg> legs = TripStructureUtils.getLegs(plan);

		for (int i = 0; i < legs.size(); i++) {
			legs.get(i).setMode(selectedModes.get(i));
			legs.get(i).setRoute(null);
		}
	}
}
