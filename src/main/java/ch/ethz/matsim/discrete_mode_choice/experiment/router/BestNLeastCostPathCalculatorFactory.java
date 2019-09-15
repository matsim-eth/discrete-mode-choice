package ch.ethz.matsim.discrete_mode_choice.experiment.router;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

public class BestNLeastCostPathCalculatorFactory implements LeastCostPathCalculatorFactory {
	private final LeastCostPathCalculatorFactory delegateFactory;
	private final BestNLeastCostPathSelector selector;

	private final double penalty;
	private final double maximumDelay;
	private final int maximumNumberOfAlternatives;

	public BestNLeastCostPathCalculatorFactory(LeastCostPathCalculatorFactory delegateFactory,
			BestNLeastCostPathSelector selector, double maximumDelay, int maximumNumberOfAlternatives, double penalty) {
		this.delegateFactory = delegateFactory;
		this.selector = selector;
		this.maximumDelay = maximumDelay;
		this.maximumNumberOfAlternatives = maximumNumberOfAlternatives;
		this.penalty = penalty;
	}

	@Override
	public LeastCostPathCalculator createPathCalculator(Network network, TravelDisutility travelDisutility,
			TravelTime travelTime) {
		PenalizedTravelDisutility penalizedTravelDisutility = new PenalizedTravelDisutility(travelDisutility, penalty);
		LeastCostPathCalculator delegateCalculator = delegateFactory.createPathCalculator(network,
				penalizedTravelDisutility, travelTime);
		return new BestNLeastCostPathCalculator(delegateCalculator, penalizedTravelDisutility, selector, maximumDelay,
				maximumNumberOfAlternatives);
	}

}
