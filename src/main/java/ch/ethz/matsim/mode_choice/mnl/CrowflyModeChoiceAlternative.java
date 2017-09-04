package ch.ethz.matsim.mode_choice.mnl;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.ModeRoutingParams;
import org.matsim.core.router.TripStructureUtils.Trip;
import org.matsim.core.utils.geometry.CoordUtils;

public class CrowflyModeChoiceAlternative implements ModeChoiceAlternative {
	final private double marginalUtilityOfMoney;
	final private ModeRoutingParams routingParams;
	final private ModeParams modelParams;

	public CrowflyModeChoiceAlternative(double marginalUtilityOfMoney, ModeRoutingParams routingParams, ModeParams modelParams) {
		this.routingParams = routingParams;
		this.modelParams = modelParams;
		this.marginalUtilityOfMoney = marginalUtilityOfMoney;
	}

	@Override
	public double estimateUtility(Person person, Link originLink, Link destinationLink) {
		Coord originCoord = originLink.getCoord();
		Coord destinationCoord = destinationLink.getCoord();

		double crowflyDistance = CoordUtils.calcEuclideanDistance(originCoord, destinationCoord);
		double crowflyTravelTime = crowflyDistance * routingParams.getBeelineDistanceFactor()
				/ routingParams.getTeleportedModeSpeed();

		return modelParams.getConstant() 
				+ modelParams.getMarginalUtilityOfTraveling() * crowflyTravelTime
				+ modelParams.getMarginalUtilityOfDistance() * crowflyDistance
				+ marginalUtilityOfMoney * modelParams.getMonetaryDistanceRate() * crowflyDistance;
	}
}
