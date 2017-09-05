package ch.ethz.matsim.mode_choice.alternatives;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.PopulationUtils;

public class TestTripChainAlternatives {
	@Test
	public void test1() {

		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives();
		Plan plan = PopulationUtils.createPlan();

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

		List<String> chainModes = Arrays.asList("car", "bike");
		List<String> nonChainModes = Arrays.asList("walk", "pt");

		Assert.assertEquals(6, tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes).size());
	}

	@Test
	public void test2() {
		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives();
		Plan plan = PopulationUtils.createPlan();

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

		List<String> chainModes = Arrays.asList("car", "bike");
		List<String> nonChainModes = Arrays.asList("walk");

		Assert.assertEquals(5, tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes).size());
	}

	@Test
	public void test3() {
		TripChainAlternatives tripChainAlternatives = new TripChainAlternatives();
		Plan plan = PopulationUtils.createPlan();
		    
		plan.addActivity(PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("104661")));
		plan.addLeg(PopulationUtils.createLeg("walk"));
		plan.addActivity(PopulationUtils.createActivityFromLinkId("other", Id.createLinkId("41276")));
		plan.addLeg(PopulationUtils.createLeg("walk"));
		plan.addActivity(PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("104661")));
		plan.addLeg(PopulationUtils.createLeg("walk"));
		plan.addActivity(PopulationUtils.createActivityFromLinkId("other", Id.createLinkId("41276")));
		plan.addLeg(PopulationUtils.createLeg("walk"));
		plan.addActivity(PopulationUtils.createActivityFromLinkId("home", Id.createLinkId("104661")));
		
		List<String> chainModes = Arrays.asList("car", "bike");
		List<String> nonChainModes = Arrays.asList("walk", "pt");
		
		List<List<String>> alternatives = tripChainAlternatives.getTripChainAlternatives(plan, chainModes, nonChainModes);
		
		for (List<String> chain : alternatives) {
			Assert.assertFalse(
					chain.get(0).equals("car")
			&& chain.get(1).equals("pt")
			&& chain.get(2).equals("car")
			&& chain.get(3).equals("car"));
		}		
	}
}
