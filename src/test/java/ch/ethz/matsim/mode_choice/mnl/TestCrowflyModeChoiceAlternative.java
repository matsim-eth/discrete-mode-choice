package ch.ethz.matsim.mode_choice.mnl;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.matsim.mode_choice.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.mnl.prediction.CrowflyDistancePredictor;
import ch.ethz.matsim.mode_choice.mnl.prediction.FixedSpeedPredictor;

public class TestCrowflyModeChoiceAlternative {
	@Test
	public void testRoutingParams() {
		Link originLink = new TestLink(new Coord(0.0, 0.0));
		Link destinationLink = new TestLink(new Coord(0.0, 1000.0));

		BasicModeChoiceParameters params = new BasicModeChoiceParameters(2.0, 0.0, -0.3, false);

		BasicModeChoiceAlternative alternative = new BasicModeChoiceAlternative(params, new FixedSpeedPredictor(30.0, new CrowflyDistancePredictor()));
		Assert.assertEquals(-8.0, alternative.estimateUtility(null, new DefaultModeChoiceTrip(originLink, destinationLink, 0.0)), 1e-10);
	}
}
