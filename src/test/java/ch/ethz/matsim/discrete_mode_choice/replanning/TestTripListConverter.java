package ch.ethz.matsim.discrete_mode_choice.replanning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PopulationUtils;

import ch.ethz.matsim.discrete_mode_choice.model.DiscreteModeChoiceTrip;

public class TestTripListConverter {
	@Test
	public void testActivityEndTime() {
		Config config = ConfigUtils.createConfig();

		PopulationFactory populationFactory = PopulationUtils.createPopulation(ConfigUtils.createConfig()).getFactory();

		Person person = populationFactory.createPerson(Id.createPersonId("p"));
		Plan plan = populationFactory.createPlan();
		plan.setPerson(person);

		Activity activity;
		Leg leg;

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setEndTime(1234.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(3000.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setEndTime(4234.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(10.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		plan.addActivity(activity);

		List<DiscreteModeChoiceTrip> result = new TripListConverter(config).convert(plan);

		assertEquals(1234.0, result.get(0).getDepartureTime());
		assertEquals(4234.0, result.get(1).getDepartureTime());
	}

	@Test
	public void testActivityMaximumDuration() {
		Config config = ConfigUtils.createConfig();
		PopulationFactory populationFactory = PopulationUtils.createPopulation(ConfigUtils.createConfig()).getFactory();

		Person person = populationFactory.createPerson(Id.createPersonId("p"));
		Plan plan = populationFactory.createPlan();
		plan.setPerson(person);

		Activity activity;
		Leg leg;

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setEndTime(1234.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(500.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setMaximumDuration(50.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(5.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		plan.addActivity(activity);

		List<DiscreteModeChoiceTrip> result = new TripListConverter(config).convert(plan);

		assertEquals(1234.0, result.get(0).getDepartureTime());
		assertEquals(1234.0 + 500.0 + 50.0, result.get(1).getDepartureTime());
	}

	@Test
	public void testInvalidChain() {
		Config config = ConfigUtils.createConfig();
		PopulationFactory populationFactory = PopulationUtils.createPopulation(ConfigUtils.createConfig()).getFactory();

		Person person = populationFactory.createPerson(Id.createPersonId("p"));
		Plan plan = populationFactory.createPlan();
		plan.setPerson(person);

		Activity activity;
		Leg leg;

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setEndTime(1234.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setMaximumDuration(50.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		plan.addActivity(activity);

		assertThrows(IllegalStateException.class, () -> {
			new TripListConverter(config).convert(plan);
		});
	}

	@Test
	public void testReconstructTravelTime() {
		Config config = ConfigUtils.createConfig();
		PopulationFactory populationFactory = PopulationUtils.createPopulation(ConfigUtils.createConfig()).getFactory();

		Person person = populationFactory.createPerson(Id.createPersonId("p"));
		Plan plan = populationFactory.createPlan();
		plan.setPerson(person);

		Activity activity;
		Leg leg;

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setEndTime(1000.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(20.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic interaction", new Coord(0.0, 0.0));
		activity.setMaximumDuration(500.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(5.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		activity.setMaximumDuration(5000.0);
		plan.addActivity(activity);

		leg = PopulationUtils.createLeg("generic");
		leg.setTravelTime(0.0);
		plan.addLeg(leg);

		activity = PopulationUtils.createActivityFromCoord("generic", new Coord(0.0, 0.0));
		plan.addActivity(activity);

		List<DiscreteModeChoiceTrip> result = new TripListConverter(config).convert(plan);

		assertEquals(2, result.size());
		assertEquals(1000.0, result.get(0).getDepartureTime());
		assertEquals(6525.0, result.get(1).getDepartureTime());
	}
}
