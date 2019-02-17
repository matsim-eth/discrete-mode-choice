package ch.ethz.matsim.discrete_mode_choice.examples.frozen_randomness;

import ch.ethz.matsim.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;

public class MyFrozenRandomnessExtension extends AbstractDiscreteModeChoiceExtension {
	@Override
	protected void installExtension() {
		bindTripEstimator("FrozenRandomness").to(MyFrozenRandomnessTripEstimator.class);
	}
}
