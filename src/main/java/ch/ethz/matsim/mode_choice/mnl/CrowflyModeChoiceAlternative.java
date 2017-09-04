package ch.ethz.matsim.mode_choice.mnl;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.ModeRoutingParams;
import org.matsim.core.router.TripStructureUtils.Trip;
import org.matsim.core.utils.geometry.CoordUtils;

public class CrowflyModeChoiceAlternative implements ModeChoiceAlternative {
	final private CrowflyModeChoiceParameters params;

	public CrowflyModeChoiceAlternative(CrowflyModeChoiceParameters params) {
		this.params = params;
	}

	@Override
	public double estimateUtility(Person person, Link originLink, Link destinationLink) {
		Coord originCoord = originLink.getCoord();
		Coord destinationCoord = destinationLink.getCoord();

		double crowflyDistance = CoordUtils.calcEuclideanDistance(originCoord, destinationCoord);
		double crowflyTravelTime = crowflyDistance / params.getSpeed();
		
		return params.getConstant()
				+ params.getBetaTravelTime() * crowflyTravelTime
				+ params.getBetaDistance() * crowflyDistance;
	}
}
