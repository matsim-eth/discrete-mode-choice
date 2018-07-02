package ch.ethz.matsim.mode_choice.v2.framework.plan_based;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.TourBasedModel;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.TourFinder;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.mode_choice.v2.framework.utils.ModeChainGeneratorFactory;

public class PlanBasedModel extends TourBasedModel {
	public PlanBasedModel(TourEstimator estimator, ModeAvailability modeAvailability,
			TourConstraintFactory constraintFactory, TourFinder tourFinder,
			UtilitySelectorFactory<TourCandidate> selectorFactory,
			ModeChainGeneratorFactory modeChainGeneratorFactory) {
		super(estimator, modeAvailability, constraintFactory, new PlanTourFinder(), selectorFactory,
				modeChainGeneratorFactory);
	}
}