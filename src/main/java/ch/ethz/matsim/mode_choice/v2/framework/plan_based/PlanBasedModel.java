package ch.ethz.matsim.mode_choice.v2.framework.plan_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import ch.ethz.matsim.mode_choice.v2.framework.ModeAvailability;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceModel;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraint;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.constraints.PlanConstraintFactory;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.plan_based.estimation.PlanEstimator;
import ch.ethz.matsim.mode_choice.v2.framework.trip_based.estimation.TripCandidate;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelector;
import ch.ethz.matsim.mode_choice.v2.framework.utilities.UtilitySelectorFactory;
import ch.ethz.matsim.mode_choice.v2.framework.utils.ModeChainGenerator;

public class PlanBasedModel implements ModeChoiceModel {
	final private PlanEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private PlanConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<PlanCandidate> selectorFactory;

	public PlanBasedModel(PlanEstimator estimator, ModeAvailability modeAvailability,
			PlanConstraintFactory constraintFactory, UtilitySelectorFactory<PlanCandidate> selectorFactory) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.selectorFactory = selectorFactory;
	}

	@Override
	public List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random) {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		PlanConstraint constraint = constraintFactory.createConstraint(trips, modes);

		ModeChainGenerator generator = new ModeChainGenerator(modes, trips.size());
		UtilitySelector<PlanCandidate> selector = selectorFactory.createUtilitySelector();

		StreamSupport.stream(Spliterators.spliterator(generator, generator.getNumberOfAlternatives(), 0), false) //
				.filter(constraint::validateBeforeEstimation) //
				.map(ms -> estimator.estimatePlan(ms, trips)) //
				.filter(constraint::validateAfterEstimation) //
				.forEach(selector::addCandidate);

		return ((PlanCandidate) selector.select(random)).getTripCandidates();
	}
}
