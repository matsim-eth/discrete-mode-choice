package ch.ethz.matsim.discrete_mode_choice.examples.my_estimator;

import ch.ethz.matsim.discrete_mode_choice.modules.AbstractDiscreteModeChoiceExtension;

public class MyExtension extends AbstractDiscreteModeChoiceExtension {
	@Override
	protected void installExtension() {
		bindTripEstimator("MyEstimatorName").to(MyTripEstimator.class);
	}
}
