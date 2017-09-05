package ch.ethz.matsim.mode_choice.mnl;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.network.Link;

public class TestCrowflyModeChoiceAlternative {
	@Test
	public void testRoutingParams() {
		Link originLink = new TestLink(new Coord(0.0, 0.0));
		Link destinationLink = new TestLink(new Coord(0.0, 1000.0));

		CrowflyModeChoiceParameters params = new CrowflyModeChoiceParameters(30.0, 2.0, 0.0, -0.3);

		CrowflyModeChoiceAlternative alternative = new CrowflyModeChoiceAlternative(params, false);
		Assert.assertEquals(-8.0, alternative.estimateUtility(null, originLink, destinationLink), 1e-10);
	}
}
