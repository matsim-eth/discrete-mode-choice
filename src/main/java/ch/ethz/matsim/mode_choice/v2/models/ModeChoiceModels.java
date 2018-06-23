package ch.ethz.matsim.mode_choice.v2.models;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.PlanBasedModel;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.TourBasedModel;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.TourFinder;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.constraints.TourConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.tour_based.estimation.TourEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.TripBasedModel;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.constraints.TripConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.MultinomialSelector;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelectorFactory;

public class ModeChoiceModels {
	static public ModeChoiceModel createTripMultinomialLogitModel(TripEstimator estimator,
			TripConstraintFactory constraintFactory, ModeAvailability modeAvailability) {
		UtilitySelectorFactory<TripCandidate> selectorFactory = new MultinomialSelector.Factory<>();
		return new TripBasedModel(estimator, modeAvailability, constraintFactory, selectorFactory);
	}

	static public ModeChoiceModel createTourMultinomialLogitModel(TourEstimator estimator,
			TourConstraintFactory constraintFactory, ModeAvailability modeAvailability, TourFinder tourFinder) {
		UtilitySelectorFactory<TourCandidate> selectorFactory = new MultinomialSelector.Factory<>();
		return new TourBasedModel(estimator, modeAvailability, constraintFactory, tourFinder, selectorFactory);
	}

	static public ModeChoiceModel createPlanMultinomialLogitModel(PlanEstimator estimator,
			PlanConstraintFactory constraintFactory, ModeAvailability modeAvailability) {
		UtilitySelectorFactory<PlanCandidate> selectorFactory = new MultinomialSelector.Factory<>();
		return new PlanBasedModel(estimator, modeAvailability, constraintFactory, selectorFactory);
	}
}
