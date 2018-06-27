package ch.ethz.matsim.mode_choice.v2.framework.plan_based;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

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
import ch.ethz.matsim.mode_choice.v2.framework.utils.ModeChainGeneratorFactory;

public class PlanBasedModel implements ModeChoiceModel {
	final private Logger logger = Logger.getLogger(PlanBasedModel.class);
	
	final private PlanEstimator estimator;
	final private ModeAvailability modeAvailability;
	final private PlanConstraintFactory constraintFactory;
	final private UtilitySelectorFactory<PlanCandidate> selectorFactory;
	final private ModeChainGeneratorFactory modeChainGeneratorFactory;

	public PlanBasedModel(PlanEstimator estimator, ModeAvailability modeAvailability,
			PlanConstraintFactory constraintFactory, UtilitySelectorFactory<PlanCandidate> selectorFactory,
			ModeChainGeneratorFactory modeChainGeneratorFactory) {
		this.estimator = estimator;
		this.modeAvailability = modeAvailability;
		this.constraintFactory = constraintFactory;
		this.selectorFactory = selectorFactory;
		this.modeChainGeneratorFactory = modeChainGeneratorFactory;
	}

	@Override
	public List<TripCandidate> chooseModes(List<ModeChoiceTrip> trips, Random random) {
		List<String> modes = new ArrayList<>(modeAvailability.getAvailableModes(trips));
		PlanConstraint constraint = constraintFactory.createConstraint(trips, modes);

		ModeChainGenerator generator = modeChainGeneratorFactory.createModeChainGenerator(modes, trips);
		UtilitySelector<PlanCandidate> selector = selectorFactory.createUtilitySelector();

		while (generator.hasNext()) {
			List<String> planModes = generator.next();

			if (!constraint.validateBeforeEstimation(planModes)) {
				continue;
			}

			PlanCandidate candidate = estimator.estimatePlan(planModes, trips);

			if (!constraint.validateAfterEstimation(candidate)) {
				continue;
			}

			selector.addCandidate(candidate);
		}

		if (selector.getNumberOfCandidates() == 0) {
			logger.warn("No feasible mode choice candidate for agent " + trips.get(0).getPerson().getId());
			
			List<String> initialModes = trips.stream().map(ModeChoiceTrip::getInitialMode).collect(Collectors.toList());
			return estimator.estimatePlan(initialModes, trips).getTripCandidates();
		}

		return selector.select(random).getTripCandidates();
	}
}
