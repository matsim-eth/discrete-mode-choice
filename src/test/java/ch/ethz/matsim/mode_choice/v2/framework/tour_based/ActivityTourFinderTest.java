package ch.ethz.matsim.mode_choice.v2.framework.tour_based;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;

import ch.ethz.matsim.mode_choice.v2.framework.DefaultModeChoiceTrip;
import ch.ethz.matsim.mode_choice.v2.framework.ModeChoiceTrip;

public class ActivityTourFinderTest {
	private List<ModeChoiceTrip> createFixture(String... activityTypes) {
		Plan plan = PopulationUtils.createPlan();
		boolean isFirst = true;

		for (String activityType : activityTypes) {
			if (!isFirst) {
				PopulationUtils.createAndAddLeg(plan, "");
			}

			PopulationUtils.createAndAddActivity(plan, activityType);
			isFirst = false;
		}

		List<Trip> trips = TripStructureUtils.getTrips(plan, new StageActivityTypesImpl());
		List<ModeChoiceTrip> modeChoiceTrips = new LinkedList<>();

		for (Trip trip : trips) {
			modeChoiceTrips.add(new DefaultModeChoiceTrip(null, trips, trip, ""));
		}

		return modeChoiceTrips;
	}

	@Test
	public void testActivityTourFinder() {
		ActivityTourFinder finder = new ActivityTourFinder("home");
		
		List<ModeChoiceTrip> trips;
		List<List<ModeChoiceTrip>> result;
		
		trips = createFixture("home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(2, result.stream().mapToInt(List::size).sum());
		
		trips = createFixture("other", "home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(3, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(1, result.get(0).size());
		Assert.assertEquals(2, result.get(1).size());
		
		trips = createFixture("home", "work", "home", "other");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(3, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(2, result.get(0).size());
		Assert.assertEquals(1, result.get(1).size());
		
		trips = createFixture("home", "work", "shop", "home", "other", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(5, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(3, result.get(0).size());
		Assert.assertEquals(2, result.get(1).size());
		
		trips = createFixture("home", "work", "home", "home", "work", "home");
		result = finder.findTours(trips);
		Assert.assertEquals(3, result.size());
		Assert.assertEquals(5, result.stream().mapToInt(List::size).sum());
		Assert.assertEquals(2, result.get(0).size());
		Assert.assertEquals(1, result.get(1).size());
		Assert.assertEquals(2, result.get(2).size());
	}
}
