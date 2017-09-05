package ch.ethz.matsim.mode_choice.alternatives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ModeParams;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup.ModeRoutingParams;
import org.matsim.core.population.PopulationUtils;

import ch.ethz.matsim.mode_choice.mnl.CrowflyModeChoiceAlternative;
import ch.ethz.matsim.mode_choice.mnl.CrowflyModeChoiceParameters;
import ch.ethz.matsim.mode_choice.mnl.TestLink;

public class TestTripChainAlternatives {

	@Test
	public void test1() {
		
		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives();
		Plan plan = PopulationUtils.createPlan();
		Id id;
		Activity act1 = PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("1"));
		Leg leg1 = PopulationUtils.createLeg("car");
		Activity act2 = PopulationUtils.createActivityFromLinkId("work", Id.createLinkId("2"));
		Leg leg2 = PopulationUtils.createLeg("car");
		Activity act3 = PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("1"));
		
		plan.addActivity(act1);
		plan.addLeg(leg1);
		plan.addActivity(act2);
		plan.addLeg(leg2);
		plan.addActivity(act3);
		String[] chainModes = {"car","bike"};
		String[] nonChainModes = {"walk", "pt"};
	
		Assert.assertEquals(6, tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes).size());
	}
	
	@Test
	public void test2() {
		
		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives();
		Plan plan = PopulationUtils.createPlan();
		Id id;
		Activity act1 = PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("1"));
		Leg leg1 = PopulationUtils.createLeg("car");
		Activity act2 = PopulationUtils.createActivityFromLinkId("work", Id.createLinkId("2"));
		Leg leg2 = PopulationUtils.createLeg("car");
		Activity act3 = PopulationUtils.createActivityFromLinkId("leisure", Id.createLinkId("3"));
		Leg leg3 = PopulationUtils.createLeg("car");
		Activity act4 = PopulationUtils.createActivityFromLinkId("work", Id.createLinkId("2"));
		Leg leg4 = PopulationUtils.createLeg("car");
		Activity act5 = PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("1"));
		
		plan.addActivity(act1);
		plan.addLeg(leg1);
		plan.addActivity(act2);
		plan.addLeg(leg2);
		plan.addActivity(act3);
		plan.addLeg(leg3);
		plan.addActivity(act4);
		plan.addLeg(leg4);
		plan.addActivity(act5);
		String[] chainModes = {"car","bike"};
		String[] nonChainModes = {"walk"};
		
		Assert.assertEquals(5, tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes).size());
	}

}
