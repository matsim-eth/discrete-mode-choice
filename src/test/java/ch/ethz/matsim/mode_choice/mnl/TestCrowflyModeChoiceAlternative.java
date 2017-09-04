package ch.ethz.matsim.mode_choice.mnl;

import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.ModeRoutingParams;
import org.matsim.core.scenario.ScenarioUtils;

import junit.framework.Assert;

public class TestCrowflyModeChoiceAlternative {
	@Test
	public void testRoutingParams() {
		
		
		
		
		ModeRoutingParams routingParams = new ModeRoutingParams();
		ModeParams modelParams = new ModeParams("mode");

		CrowflyModeChoiceAlternative alternative = new CrowflyModeChoiceAlternative(0.0, routingParams, modelParams);
		
		Assert.assertEquals(expected, actual);
	}
}
