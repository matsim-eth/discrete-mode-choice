package ch.ethz.matsim.discrete_mode_choice.examples;

import ch.ethz.matsim.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;

public class MyExtension extends AbstractDiscreteModeChoiceExtension {
	@Override
	protected void installExtension() {
		bindTripEstimator("MyEstimatorName").to(MyTripEstimator.class);
	}
}
